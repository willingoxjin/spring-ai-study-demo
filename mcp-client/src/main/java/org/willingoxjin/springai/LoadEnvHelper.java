package org.willingoxjin.springai;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Jin.Nie
 */
public class LoadEnvHelper {

    private static final String ENV_FILE = ".env";

    private static final String ENV_SEPARATOR = "_";

    private static final String ENV_MODEL_PROP_NAME = "model";

    @Getter
    @AllArgsConstructor
    public enum Model {
        DEEPSEEK("deepseek"),
        QWEN("qwen"),
        ;
        private final String value;

        public static Model findModel(String value) {
            if (value == null) {
                return null;
            }
            for (Model model : Model.values()) {
                if (model.value.equals(value)) {
                    return model;
                }
            }
            return null;
        }
    }

    public static void loadEnv() {
        String envFile = ENV_FILE;
        String argEnvFile = System.getProperty(ENV_MODEL_PROP_NAME);
        if (argEnvFile != null && !argEnvFile.isEmpty()) {
            Model model = Model.findModel(argEnvFile);
            if (model != null) {
                throw new IllegalArgumentException("Unknown model: " + argEnvFile);
            }
            envFile = envFile + ENV_SEPARATOR + argEnvFile;
        }
        Dotenv dotenv = Dotenv.configure().filename(envFile).ignoreIfMissing().load();
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

}
