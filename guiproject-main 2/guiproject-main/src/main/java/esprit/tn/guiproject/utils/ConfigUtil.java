package esprit.tn.guiproject.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties properties = new Properties();
    private static boolean initialized = false;

    private static void initialize() {
        if (!initialized) {
            try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.err.println("Unable to find config.properties, will use environment variables instead");
                } else {
                    properties.load(input);
                }
                initialized = true;
            } catch (IOException ex) {
                System.err.println("Error loading config.properties, will use environment variables instead: " + ex.getMessage());
            }
        }
    }

    public static String getProperty(String key) {
        initialize();
        // Check environment variables first, then fall back to properties file
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return properties.getProperty(key);
    }

    public static String getStripeSecretKey() {
        // First check for the STRIPE_SECRET_KEY environment variable
        String envKey = System.getenv("STRIPE_SECRET_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return getProperty("stripe.secret.key");
    }

    public static String getStripePublicKey() {
        // First check for the STRIPE_PUBLIC_KEY environment variable
        String envKey = System.getenv("STRIPE_PUBLIC_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return getProperty("stripe.public.key");
    }
}