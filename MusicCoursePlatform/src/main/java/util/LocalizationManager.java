package util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton class for managing application localization.
 * Supports dynamic language switching and resource bundle management.
 *
 * Supported languages:
 * - English (en)
 * - Chinese (zh)
 * - Arabic (ar)
 */
public class LocalizationManager {

    private static LocalizationManager instance;

    private final ObjectProperty<Locale> currentLocale;
    private ResourceBundle resourceBundle;

    // Base name for resource bundles
    private static final String BUNDLE_BASE_NAME = "i18n.messages";

    // Supported locales
    public static final Locale ENGLISH = new Locale("en");
    public static final Locale CHINESE = new Locale("zh");
    public static final Locale ARABIC = new Locale("ar");

    private LocalizationManager() {
        currentLocale = new SimpleObjectProperty<>(ENGLISH);
        loadResourceBundle(ENGLISH);
    }

    /**
     * Get singleton instance of LocalizationManager
     */
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    /**
     * Load resource bundle for given locale
     */
    private void loadResourceBundle(Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
        } catch (Exception e) {
            System.err.println("Failed to load resource bundle for locale: " + locale);
            e.printStackTrace();
            // Fallback to English
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, ENGLISH);
        }
    }

    /**
     * Set current locale and reload resource bundle
     */
    public void setLocale(Locale locale) {
        loadResourceBundle(locale);
        currentLocale.set(locale);
    }

    /**
     * Get current locale
     */
    public Locale getCurrentLocale() {
        return currentLocale.get();
    }

    /**
     * Get current locale property (for binding)
     */
    public ObjectProperty<Locale> localeProperty() {
        return currentLocale;
    }

    /**
     * Get localized string for given key
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            System.err.println("Missing translation key: " + key);
            return "!" + key + "!";
        }
    }

    /**
     * Check if current language is RTL (Right-to-Left)
     */
    public boolean isRTL() {
        return currentLocale.get().equals(ARABIC);
    }

    /**
     * Apply RTL/LTR direction to a node
     */
    public void applyDirection(Node node) {
        if (isRTL()) {
            node.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        } else {
            node.setNodeOrientation(javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
        }
    }

    /**
     * Get current language display name for locale
     */
    public String getCurrentLanguageDisplayName() {
        return getLanguageDisplayName(getCurrentLocale());
    }

    /**
     * Get locale from display name
     */
    public static Locale getLocaleFromDisplayName(String displayName) {
        switch (displayName) {
            case "中文":
                return CHINESE;
            case "العربية":
                return ARABIC;
            default:
                return ENGLISH;
        }
    }

    /**
     * Get language display name for locale
     */
    public static String getLanguageDisplayName(Locale locale) {
        if (locale.equals(ENGLISH)) {
            return "English";
        } else if (locale.equals(CHINESE)) {
            return "中文";
        } else if (locale.equals(ARABIC)) {
            return "العربية";
        }
        return locale.getDisplayLanguage();
    }
}
