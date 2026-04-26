package util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
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

    private static final Logger logger = LoggerFactory.getLogger(LocalizationManager.class);

    private static LocalizationManager instance;

    private final ObjectProperty<Locale> currentLocale;
    private ResourceBundle resourceBundle;

    // Base name for resource bundles
    private static final String BUNDLE_BASE_NAME = "i18n.messages";

    // Prefix for instrument resource-bundle keys
    private static final String INSTRUMENT_PREFIX = "instrument.";

    // Supported locales
    public static final Locale ENGLISH = Locale.forLanguageTag("en");
    public static final Locale CHINESE = Locale.forLanguageTag("zh");
    public static final Locale ARABIC = Locale.forLanguageTag("ar");

    /**
     * Canonical lowercase instrument keys — these are what get stored in the
     * database
     * and used as resource-bundle keys (e.g. "instrument.piano").
     */
    private static final String[] INSTRUMENT_KEYS = {
            "piano", "guitar", "violin", "drums", "flute", "saxophone", "cello", "voice"
    };

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
            logger.error("Failed to load resource bundle for locale: {}", locale, e);
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
            logger.warn("Missing translation key: {} for locale: {}", key, getCurrentLocale());
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

    /**
     * Create a locale-aware {@link DateTimeFormatter} for lesson/booking date display.
     *
     * <p>Centralises the locale-check pattern that was previously duplicated in
     * {@code TeacherProfileViewController} and {@code BookingViewController}.</p>
     *
     * @return a formatter appropriate for the current locale
     */
    public DateTimeFormatter createDateFormatter() {
        Locale locale = getCurrentLocale();
        if (ARABIC.equals(locale)) {
            return DateTimeFormatter.ofPattern("EEEE، d MMMM", locale);
        } else if (CHINESE.equals(locale)) {
            return DateTimeFormatter.ofPattern("M月d日 EEEE", locale);
        } else {
            return DateTimeFormatter.ofPattern("EEEE, MMMM d", locale);
        }
    }

    // -----------------------------------------------------------------------
    // Instrument localization helpers (Sprint 6 – Database Localization fix)
    // -----------------------------------------------------------------------

    /**
     * Reverse-lookup: given any localised display name (in any supported locale),
     * return the canonical lowercase instrument key stored in the database.
     * Returns {@code null} when the name cannot be matched.
     */
    public String getInstrumentKey(String localizedName) {
        if (localizedName == null)
            return null;
        // Check against every supported locale's resource bundle
        for (Locale locale : new Locale[] { ENGLISH, CHINESE, ARABIC }) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
                for (String key : INSTRUMENT_KEYS) {
                    try {
                        if (localizedName.equalsIgnoreCase(bundle.getString(INSTRUMENT_PREFIX + key))) {
                            return key;
                        }
                    } catch (MissingResourceException ignored) {
                        // key absent in this bundle, continue
                    }
                }
            } catch (Exception ignored) {
                // bundle unavailable, skip locale
            }
        }
        // Direct canonical-key match (e.g. already stored as "piano")
        String lower = localizedName.trim().toLowerCase();
        for (String key : INSTRUMENT_KEYS) {
            if (lower.equals(key))
                return key;
        }
        return null;
    }

    /**
     * Convert a stored instrument value (canonical key or legacy localised name)
     * into the display name for the current locale.
     */
    public String getLocalizedInstrumentName(String stored) {
        if (stored == null)
            return getString("message.unknown");
        String key = getInstrumentKey(stored);
        if (key != null) {
            return getString(INSTRUMENT_PREFIX + key);
        }
        return stored; // fallback: return as-is
    }

    /**
     * Return all locale representations of an instrument key so that a DB query
     * can find teachers who saved their profile in any language (backward compat).
     */
    public List<String> getAllInstrumentVariants(String key) {
        List<String> variants = new ArrayList<>();
        if (key == null)
            return variants;
        variants.add(key); // canonical lowercase key
        for (Locale locale : new Locale[] { ENGLISH, CHINESE, ARABIC }) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
                String localized = bundle.getString(INSTRUMENT_PREFIX + key);
                if (!variants.contains(localized)) {
                    variants.add(localized);
                }
            } catch (Exception ignored) {
                // skip unavailable locale
            }
        }
        return variants;
    }
}
