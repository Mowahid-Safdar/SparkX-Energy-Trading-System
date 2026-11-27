
package sparkX.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GroqService {

    private static final String MODEL = "openai/gpt-oss-20b";

    private static final String API_URL =
        "https://api.groq.com/openai/v1/chat/completions";

    private final HttpClient client;

    public GroqService() {

        client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    }

    public String generateResponse(String prompt)
            throws IOException, InterruptedException {

        String apiKey = EnvLoader.get("GROQ_API_KEY");

        String requestBody =
            "{"
            + "\"model\":\"" + MODEL + "\","
            + "\"messages\":["
            + "{"
            + "\"role\":\"user\","
            + "\"content\":\"" + escapeJson(prompt) + "\""
            + "}"
            + "],"
            + "\"temperature\":0.7,"
            + "\"max_completion_tokens\":1500"
            + "}";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .timeout(Duration.ofSeconds(90))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {

            String errorBody = response.body();

            if (errorBody.length() > 1000) {
                errorBody = errorBody.substring(0, 1000);
            }

            throw new IOException(
                "Groq API error (HTTP "
                + response.statusCode()
                + "): "
                + errorBody
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

    private String extractResponseText(String json) throws IOException {

        String key = "\"content\"";
        int keyIndex = json.indexOf(key);

        if (keyIndex == -1) {
            throw new IOException("Groq response does not contain content.");
        }

        int colonIndex = json.indexOf(':', keyIndex + key.length());

        if (colonIndex == -1) {
            throw new IOException("Invalid Groq response format.");
        }

        int start = colonIndex + 1;

        while (start < json.length()
                && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length() || json.charAt(start) != '"') {
            throw new IOException("Groq response content is not a string.");
        }

        start++;

        StringBuilder content = new StringBuilder();
        boolean escaped = false;
        boolean foundEnd = false;

        for (int i = start; i < json.length(); i++) {

            char c = json.charAt(i);

            if (escaped) {
                content.append('\\').append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                foundEnd = true;
                break;
            } else {
                content.append(c);
            }
        }

        if (!foundEnd) {
            throw new IOException("Groq response content was incomplete.");
        }

        String result = unescapeJson(content.toString());

        if (result.isBlank()) {
            throw new IOException("Groq returned an empty response.");
        }

        return result;
    }

    private String unescapeJson(String text) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c == '\\' && i + 1 < text.length()) {

                char next = text.charAt(++i);

                switch (next) {

                    case 'n':
                        result.append('\n');
                        break;

                    case 'r':
                        result.append('\r');
                        break;

                    case 't':
                        result.append('\t');
                        break;

                    case '"':
                        result.append('"');
                        break;

                    case '\\':
                        result.append('\\');
                        break;

                    case '/':
                        result.append('/');
                        break;

                    case 'b':
                        result.append('\b');
                        break;

                    case 'f':
                        result.append('\f');
                        break;

                    case 'u':

                        if (i + 4 < text.length()) {
                            String hex = text.substring(i + 1, i + 5);
                            result.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        }

                        break;

                    default:
                        result.append(next);
                }

            } else {

                result.append(c);
            }
        }

        return result.toString();
    }
}
