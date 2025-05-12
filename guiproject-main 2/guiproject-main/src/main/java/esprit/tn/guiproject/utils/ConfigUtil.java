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
                    System.err.println("Unable to find config.properties");
                    return;
                }
                properties.load(input);
                initialized = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getProperty(String key) {
        initialize();
        return properties.getProperty(key);
    }

    public static String getStripeSecretKey() {
        return getProperty("stripe.secret.key");
    }

    public static String getStripePublicKey() {
        return getProperty("stripe.public.key");
    }
}