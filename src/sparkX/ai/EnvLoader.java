
package sparkX.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EnvLoader {

    public static String get(String key) {

        Path envFile = Path.of(".env");

        if (!Files.exists(envFile)) {
            throw new IllegalStateException(
                ".env file not found in project root."
            );
        }

        try {
            List<String> lines = Files.readAllLines(envFile);

            for (String line : lines) {

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);

                if (parts.length == 2 &&
                    parts[0].trim().equals(key)) {

                    String value = parts[1].trim();

                    if (value.isEmpty()) {
                        throw new IllegalStateException(
                            key + " is empty in .env."
                        );
                    }

                    return value;
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not read .env file.", e
            );
        }

        throw new IllegalStateException(
            key + " not found in .env file."
        );
    }
}
