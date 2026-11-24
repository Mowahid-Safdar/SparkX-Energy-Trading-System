
package sparkX.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiService {

    private static final String MODEL = "gemini-3.8-flash";

    private final HttpClient client;

    public GeminiService() {

        client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    }

    public String generateResponse(String prompt)
            throws IOException, InterruptedException {

        String apiKey = EnvLoader.get("GEMINI_API_KEY");

        String requestBody =
            "{"
            + "\"model\":\"" + MODEL + "\","
            + "\"input\":\"" + escapeJson(prompt) + "\""
            + "}";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(
                "https://generativelanguage.googleapis.com/v1beta/interactions"
            ))
            .header("Content-Type", "application/json")
            .header("x-goog-api-key", apiKey)
            .timeout(Duration.ofSeconds(60))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                "Gemini API error (HTTP "
                + response.statusCode() + "). "
                + "Please try again later."
            );
        }

        return extractResponseText(response.body());
    }

    private String escapeJson(String text) {

        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String extractResponseText(String json)
            throws IOException {

        Pattern pattern = Pattern.compile(
            "\"type\"\\s*:\\s*\"text\"\\s*,\\s*\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\""
        );

        Matcher matcher = pattern.matcher(json);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            if (result.length() > 0) {
                result.append("\n");
            }

            result.append(unescapeJson(matcher.group(1)));
        }

        if (result.length() == 0) {
            throw new IOException(
                "Gemini returned no readable text."
            );
        }

        return result.toString();
    }

    private String unescapeJson(String text) {

        return text
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }
}
