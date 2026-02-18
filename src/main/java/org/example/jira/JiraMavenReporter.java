package org.example.jira;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * JIRA Maven Reporter for Selenium + Cucumber
 *
 * Automatically creates JIRA issues for failed tests with:
 * - Test failure details
 * - Surefire HTML reports
 * - Cucumber HTML/JSON reports
 * - Screenshots (if available)
 * - Test logs
 */
public class JiraMavenReporter {
    private static final Path ROOT_DIR = Paths.get("").toAbsolutePath();
    private static final Path SITE_DIR = ROOT_DIR.resolve("target").resolve("site");
    private static final Path SUREFIRE_DIR = ROOT_DIR.resolve("target").resolve("surefire-reports");
    private static final Path CUCUMBER_DIR = ROOT_DIR.resolve("target").resolve("cucumber-reports");
    private static final Path SCREENSHOTS_DIR = ROOT_DIR.resolve("target").resolve("screenshots");
    private static final Path ARTIFACTS_DIR = ROOT_DIR.resolve("jira_artifacts");

    public static void main(String[] args) throws Exception {
        loadEnv(ROOT_DIR.resolve(".env"));
        JiraConfig config = JiraConfig.fromEnv();

        HttpClient client = HttpClient.newHttpClient();
        String authHeader = basicAuth(config.email(), config.apiToken());
        String issueTypeId = resolveIssueTypeId(client, config, authHeader);

        boolean skipTests = Boolean.parseBoolean(System.getProperty("jira.skip.tests", "false"));
        int testExit = skipTests ? 0 : runCommand(title("Running Maven Tests"), mavenCommand("clean", "test"));
        int reportExit = runCommand(title("Generating HTML Report"), mavenCommand("-DskipTests", "surefire-report:report-only"));

        List<TestFailure> failures = parseFailures();
        if (failures.isEmpty()) {
            System.out.println("\n[OK] All tests passed. No JIRA issues created.");
            System.exit(testExit);
        }

        List<Path> attachments = collectArtifacts();

        List<IssueInfo> created = new ArrayList<>();
        for (TestFailure failure : failures) {
            System.out.println("\n[INFO] Creating JIRA issue for: " + failure.testName());
            IssueInfo issue = createIssue(client, config, authHeader, issueTypeId, failure);
            attachFiles(client, config, authHeader, issue.key(), attachments);
            created.add(issue);
            System.out.println("[OK] Issue created: " + issue.key() + " - " + issue.url());
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("JIRA Issues Created");
        System.out.println("=".repeat(80));
        for (IssueInfo issue : created) {
            System.out.println("- " + issue.key() + ": " + issue.url());
        }

        if (reportExit != 0) {
            System.out.println("[WARNING] HTML report generation failed.");
        }

        if (skipTests && !failures.isEmpty()) {
            testExit = 1;
        }

        System.exit(testExit);
    }

    private static void loadEnv(Path envPath) throws IOException {
        if (!Files.exists(envPath)) {
            return;
        }
        for (String line : Files.readAllLines(envPath, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                continue;
            }
            String[] parts = trimmed.split("=", 2);
            String key = parts[0].trim();
            String value = parts[1].trim();
            if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
            }
            if (System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static int runCommand(String title, List<String> command) throws IOException, InterruptedException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(title);
        System.out.println("=".repeat(80) + "\n");
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(ROOT_DIR.toFile());
        builder.inheritIO();
        Process process = builder.start();
        return process.waitFor();
    }

    private static String title(String label) {
        return label;
    }

    private static List<String> mavenCommand(String... args) {
        List<String> command = new ArrayList<>();
        String mvn = isWindows() ? "mvn.cmd" : "mvn";
        command.add(mvn);
        for (String arg : args) {
            command.add(arg);
        }
        return command;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static List<TestFailure> parseFailures() throws Exception {
        if (!Files.isDirectory(SUREFIRE_DIR)) {
            return List.of();
        }

        List<TestFailure> failures = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SUREFIRE_DIR, "TEST-*.xml")) {
            for (Path report : stream) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                try (InputStream input = Files.newInputStream(report)) {
                    Document doc = builder.parse(input);
                    NodeList testcases = doc.getElementsByTagName("testcase");
                    for (int i = 0; i < testcases.getLength(); i++) {
                        Element testcase = (Element) testcases.item(i);
                        Element failureNode = getChildElement(testcase, "failure");
                        Element errorNode = getChildElement(testcase, "error");
                        Element issueNode = failureNode != null ? failureNode : errorNode;
                        if (issueNode == null) {
                            continue;
                        }
                        String className = testcase.getAttribute("classname");
                        String name = testcase.getAttribute("name");
                        String duration = testcase.getAttribute("time");
                        String message = issueNode.getAttribute("message");
                        String details = issueNode.getTextContent();
                        String testName = className.isBlank() ? name : className + "." + name;

                        failures.add(new TestFailure(testName, duration, safeText(message, "No error message"), safeText(details, "No stack trace available")));
                    }
                }
            }
        }
        return failures;
    }

    private static Element getChildElement(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        Node node = nodes.item(0);
        return node instanceof Element ? (Element) node : null;
    }

    private static String safeText(String text, String fallback) {
        if (text == null) {
            return fallback;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private static List<Path> collectArtifacts() throws IOException {
        Files.createDirectories(ARTIFACTS_DIR);
        List<Path> attachments = new ArrayList<>();

        // Surefire HTML reports
        Path siteZip = ARTIFACTS_DIR.resolve("surefire-site.zip");
        if (zipDirectory(SITE_DIR, siteZip)) {
            attachments.add(siteZip);
        }

        // Surefire XML/TXT reports
        Path reportsZip = ARTIFACTS_DIR.resolve("surefire-reports.zip");
        if (zipDirectory(SUREFIRE_DIR, reportsZip)) {
            attachments.add(reportsZip);
        }

        // Cucumber HTML/JSON reports
        Path cucumberZip = ARTIFACTS_DIR.resolve("cucumber-reports.zip");
        if (zipDirectory(CUCUMBER_DIR, cucumberZip)) {
            attachments.add(cucumberZip);
        }

        // Screenshots (if available)
        Path screenshotsZip = ARTIFACTS_DIR.resolve("screenshots.zip");
        if (zipDirectory(SCREENSHOTS_DIR, screenshotsZip)) {
            attachments.add(screenshotsZip);
        }

        // Test logs
        Path logsZip = ARTIFACTS_DIR.resolve("test-logs.zip");
        if (zipLogs(logsZip)) {
            attachments.add(logsZip);
        }

        return attachments;
    }

    private static boolean zipDirectory(Path sourceDir, Path zipPath) throws IOException {
        if (!Files.isDirectory(sourceDir)) {
            return false;
        }
        try (ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
            Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    ZipEntry entry = new ZipEntry(sourceDir.relativize(path).toString().replace("\\", "/"));
                    try {
                        zipOutput.putNextEntry(entry);
                        Files.copy(path, zipOutput);
                        zipOutput.closeEntry();
                    } catch (IOException ignored) {
                        // Keep going for other files.
                    }
                });
        }
        return Files.exists(zipPath);
    }

    private static boolean zipLogs(Path zipPath) throws IOException {
        List<Path> files = new ArrayList<>();
        addIfExists(files, ROOT_DIR.resolve("selenium.log"));
        addIfExists(files, ROOT_DIR.resolve("test_output.txt"));
        addIfExists(files, ROOT_DIR.resolve("target").resolve("chromedriver.log"));
        if (Files.isDirectory(SUREFIRE_DIR)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(SUREFIRE_DIR, "*.txt")) {
                for (Path path : stream) {
                    files.add(path);
                }
            }
        }
        if (files.isEmpty()) {
            return false;
        }
        try (ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
            for (Path file : files) {
                ZipEntry entry = new ZipEntry(ROOT_DIR.relativize(file).toString().replace("\\", "/"));
                zipOutput.putNextEntry(entry);
                Files.copy(file, zipOutput);
                zipOutput.closeEntry();
            }
        }
        return Files.exists(zipPath);
    }

    private static void addIfExists(List<Path> list, Path path) {
        if (Files.exists(path)) {
            list.add(path);
        }
    }

    private static String resolveIssueTypeId(HttpClient client, JiraConfig config, String authHeader) throws IOException, InterruptedException {
        String url = config.baseUrl()
            + "/rest/api/2/issue/createmeta?projectKeys="
            + URLEncoder.encode(config.projectKey(), StandardCharsets.UTF_8)
            + "&expand=projects.issuetypes";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Basic " + authHeader)
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new IOException("Failed to load issue types: " + response.statusCode() + " - " + response.body());
        }

        JSONObject payload = new JSONObject(response.body());
        JSONArray projects = payload.optJSONArray("projects");
        if (projects == null) {
            throw new IllegalStateException("No projects returned by Jira createmeta.");
        }

        List<IssueTypeInfo> issueTypes = new ArrayList<>();
        for (int i = 0; i < projects.length(); i++) {
            JSONObject project = projects.optJSONObject(i);
            if (project == null) {
                continue;
            }
            JSONArray types = project.optJSONArray("issuetypes");
            if (types == null) {
                continue;
            }
            for (int j = 0; j < types.length(); j++) {
                JSONObject type = types.optJSONObject(j);
                if (type == null) {
                    continue;
                }
                String id = type.optString("id", "");
                String name = type.optString("name", "");
                if (!id.isBlank() && !name.isBlank()) {
                    issueTypes.add(new IssueTypeInfo(id, name));
                }
            }
        }

        for (IssueTypeInfo type : issueTypes) {
            if (type.name().equalsIgnoreCase(config.issueType())) {
                return type.id();
            }
        }

        StringBuilder available = new StringBuilder();
        for (IssueTypeInfo type : issueTypes) {
            if (!available.isEmpty()) {
                available.append(", ");
            }
            available.append(type.name());
        }

        throw new IllegalStateException(
            "Issue type '" + config.issueType() + "' not found in project '" + config.projectKey() + "'. Available: " + available
        );
    }

    private static IssueInfo createIssue(HttpClient client, JiraConfig config, String authHeader, String issueTypeId, TestFailure failure) throws IOException, InterruptedException {
        String summary = "[Automated Test Failure] " + failure.testName();
        String description = buildDescription(failure);

        JSONObject fields = new JSONObject();
        fields.put("project", new JSONObject().put("key", config.projectKey()));
        fields.put("summary", summary);
        fields.put("description", description);
        fields.put("issuetype", new JSONObject().put("id", issueTypeId));
        fields.put("labels", new JSONArray().put("WEB").put("SELENIUM").put("CUCUMBER").put("JAVA").put("MAVEN").put("automated-test"));

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(config.baseUrl() + "/rest/api/2/issue"))
            .header("Authorization", "Basic " + authHeader)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new IOException("Failed to create issue: " + response.statusCode() + " - " + response.body());
        }

        JSONObject result = new JSONObject(response.body());
        String issueKey = result.optString("key", "");
        return new IssueInfo(issueKey, config.baseUrl() + "/browse/" + issueKey);
    }

    private static void attachFiles(HttpClient client, JiraConfig config, String authHeader, String issueKey, List<Path> files) throws IOException, InterruptedException {
        for (Path file : files) {
            if (!Files.exists(file)) {
                continue;
            }
            String boundary = "----jiraBoundary" + UUID.randomUUID();
            byte[] body = buildMultipartBody(boundary, file);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + "/rest/api/2/issue/" + issueKey + "/attachments"))
                .header("Authorization", "Basic " + authHeader)
                .header("X-Atlassian-Token", "no-check")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IOException("Failed to attach file " + file.getFileName() + ": " + response.statusCode() + " - " + response.body());
            }
        }
    }

    private static byte[] buildMultipartBody(String boundary, Path file) throws IOException {
        String filename = file.getFileName().toString();
        String header = "--" + boundary + "\r\n"
            + "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n"
            + "Content-Type: application/zip\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] fileBytes = Files.readAllBytes(file);
        byte[] footerBytes = footer.getBytes(StandardCharsets.UTF_8);

        byte[] body = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, body, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, body, headerBytes.length + fileBytes.length, footerBytes.length);
        return body;
    }

    private static String buildDescription(TestFailure failure) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "*Automated Test Failure Report*\n\n"
            + "*Test Name:* " + failure.testName() + "\n"
            + "*Duration:* " + failure.duration() + "s\n"
            + "*Timestamp:* " + timestamp + "\n\n"
            + "----\n\n"
            + "*Error Message:*\n"
            + "{code}\n" + failure.message() + "\n{code}\n\n"
            + "*Stack Trace:*\n"
            + "{code}\n" + failure.details() + "\n{code}\n\n"
            + "----\n\n"
            + "*Reports:*\n"
            + "- Surefire HTML report (target/site)\n"
            + "- Surefire XML and TXT reports (target/surefire-reports)\n"
            + "- Cucumber HTML and JSON reports (target/cucumber-reports)\n"
            + "- Screenshots on failure (target/screenshots)\n"
            + "- Test logs (selenium.log, test_output.txt, chromedriver.log when available)\n\n"
            + "*Environment:*\n"
            + "- Framework: Selenium + Cucumber + JUnit (Maven)\n"
            + "- Project: selenium-and-cucumber-saucelabs_web\n"
            + "- Browser: Chrome (incognito/headless)\n";
    }

    private static String basicAuth(String email, String token) {
        String value = email + ":" + token;
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private record JiraConfig(String baseUrl, String email, String apiToken, String projectKey, String issueType) {
        static JiraConfig fromEnv() {
            Map<String, String> env = System.getenv();
            String baseUrl = getEnv(env, "JIRA_BASE_URL");
            String email = getEnv(env, "JIRA_EMAIL");
            String token = env.getOrDefault("JIRA_API_SECRET", env.get("JIRA_API_TOKEN"));
            if (token == null || token.isBlank()) {
                token = getEnv(env, "JIRA_API_TOKEN");
            }
            String projectKey = getEnv(env, "JIRA_PROJECT_KEY");
            String issueType = env.getOrDefault("JIRA_ISSUE_TYPE", "Bug");

            List<String> missing = new ArrayList<>();
            if (baseUrl.isBlank()) missing.add("JIRA_BASE_URL");
            if (email.isBlank()) missing.add("JIRA_EMAIL");
            if (token.isBlank()) missing.add("JIRA_API_TOKEN");
            if (projectKey.isBlank()) missing.add("JIRA_PROJECT_KEY");

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing JIRA configuration: " + String.join(", ", missing));
            }
            return new JiraConfig(baseUrl.replaceAll("/$", ""), email, token, projectKey, issueType);
        }

        private static String getEnv(Map<String, String> env, String key) {
            String value = env.get(key);
            if (value == null) {
                value = System.getProperty(key, "");
            }
            return value == null ? "" : value;
        }
    }

    private record TestFailure(String testName, String duration, String message, String details) {
    }

    private record IssueTypeInfo(String id, String name) {
    }

    private record IssueInfo(String key, String url) {
    }
}

