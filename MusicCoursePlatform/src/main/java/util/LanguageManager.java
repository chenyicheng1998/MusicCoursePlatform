package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton class to manage application language settings.
 * Supports English and Finnish languages with real-time switching.
 */
public class LanguageManager {
    
    private static LanguageManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private final List<LanguageChangeListener> listeners;
    
    public static final Locale ENGLISH = Locale.ENGLISH;
    @SuppressWarnings("deprecation")
    public static final Locale FINNISH = new Locale("fi", "FI");
    
    private LanguageManager() {
        listeners = new ArrayList<>();
        setLocale(ENGLISH);
    }
    
    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("i18n.messages", locale);
        notifyListeners();
    }
    
    public void setLanguage(String languageCode) {
        if ("FI".equalsIgnoreCase(languageCode) || "Finnish".equalsIgnoreCase(languageCode)) {
            setLocale(FINNISH);
        } else {
            setLocale(ENGLISH);
        }
    }
    
    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }
    
    public String getString(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return java.text.MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return key;
        }
    }
    
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    public boolean isEnglish() {
        return ENGLISH.equals(currentLocale);
    }
    
    public boolean isFinnish() {
        return FINNISH.getLanguage().equals(currentLocale.getLanguage());
    }
    
    public void addLanguageChangeListener(LanguageChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeLanguageChangeListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChanged(currentLocale);
        }
    }
    
    /**
     * Interface for components that need to respond to language changes.
     */
    public interface LanguageChangeListener {
        void onLanguageChanged(Locale newLocale);
    }
}
