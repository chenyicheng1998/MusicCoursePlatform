# Localization Framework Documentation

## Overview

The Music Course Platform implements a comprehensive multilingual localization system supporting English, Chinese, and Arabic languages. The system is built on the JavaFX ResourceBundle framework and managed through a singleton LocalizationManager class.

## Core Components

### LocalizationManager

**Location**: `src/main/java/util/LocalizationManager.java`

LocalizationManager is the core component of the system, implemented using the singleton pattern. It is responsible for:
- Managing application language switching
- Loading and caching ResourceBundles
- Providing real-time language update notifications
- Handling RTL (Right-to-Left) layout support
- Maintaining language state persistence

### Design Patterns

1. **Singleton Pattern**
   ```java
   public class LocalizationManager {
       private static LocalizationManager instance;

       public static LocalizationManager getInstance() {
           if (instance == null) {
               instance = new LocalizationManager();
           }
           return instance;
       }
   }
   ```

2. **Observer Pattern**
   ```java
   private final List<Runnable> languageChangeListeners = new ArrayList<>();

   public void addLanguageChangeListener(Runnable listener) {
       languageChangeListeners.add(listener);
   }

   private void notifyLanguageChange() {
       languageChangeListeners.forEach(Runnable::run);
   }
   ```

## API Reference

### Core Methods

#### `getInstance()`
Get the singleton instance of LocalizationManager
```java
LocalizationManager localizationManager = LocalizationManager.getInstance();
```

#### `getString(String key)`
Get the localized string for the current language
```java
String title = localizationManager.getString("app.title");
```

#### `setLocale(Locale locale)`
Switch the application language
```java
localizationManager.setLocale(new Locale("ar")); // Switch to Arabic
```

#### `getCurrentLocale()`
Get the currently active locale
```java
Locale currentLocale = localizationManager.getCurrentLocale();
```

#### `isRTL()`
Check if the current language uses RTL (Right-to-Left) layout
```java
if (localizationManager.isRTL()) {
    // Apply RTL layout logic
    node.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
}
```

#### `addLanguageChangeListener(Runnable listener)`
Add a language change listener for real-time updates
```java
localizationManager.addLanguageChangeListener(() -> {
    updateUITexts();
});
```

### Language State Management

#### `getCurrentLanguageDisplayName()`
Get the display name of the current language
```java
String displayName = localizationManager.getCurrentLanguageDisplayName();
languageSelector.setValue(displayName);
```

#### `getLocaleFromDisplayName(String displayName)`
Get the corresponding Locale from a display name
```java
Locale locale = localizationManager.getLocaleFromDisplayName("中文");
```

## Resource Files Structure

Resource files are located in the `src/main/resources/i18n/` directory:

```
src/main/resources/i18n/
├── messages.properties          # Default language (English)
├── messages_zh.properties       # Chinese resources
└── messages_ar.properties       # Arabic resources
```

### Resource File Format

**English** (`messages.properties`):
```properties
app.name=Music Course Platform
nav.home=Home
nav.profile=Profile
nav.logout=Logout
action.login=Login
```

**Chinese** (`messages_zh.properties`):
```properties
app.name=音乐课程平台
nav.home=主页
nav.profile=个人资料
nav.logout=登出
action.login=登录
```

**Arabic** (`messages_ar.properties`):
```properties
app.name=منصة دورة الموسيقى
nav.home=الصفحة الرئيسية
nav.profile=الملف الشخصي
nav.logout=تسجيل الخروج
action.login=تسجيل الدخول
```

## Usage in Controllers

### Basic Setup
```java
public class ExampleController implements Initializable {
    private LocalizationManager localizationManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localizationManager = LocalizationManager.getInstance();

        // Add language change listener
        localizationManager.addLanguageChangeListener(this::updateTexts);

        // Initialize UI texts
        updateTexts();

        // Setup RTL layout support
        if (localizationManager.isRTL()) {
            rootPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }
    }

    private void updateTexts() {
        // Update all UI texts
        titleLabel.setText(localizationManager.getString("page.title"));
        submitButton.setText(localizationManager.getString("action.submit"));
    }
}
```

### Language Selector Implementation
```java
@FXML
private ComboBox<String> languageSelector;

private void initializeLanguageSelector() {
    languageSelector.getItems().addAll("English", "中文", "العربية");
    languageSelector.setValue(localizationManager.getCurrentLanguageDisplayName());

    languageSelector.setOnAction(event -> {
        String selectedLanguage = languageSelector.getValue();
        Locale selectedLocale = localizationManager.getLocaleFromDisplayName(selectedLanguage);
        localizationManager.setLocale(selectedLocale);
    });
}
```

### Dynamic Content Localization
```java
// Instrument names localization
private void updateInstrumentCombo() {
    List<String> localizedInstruments = Arrays.asList("Piano", "Guitar", "Violin", "Drums")
        .stream()
        .map(instrument -> localizationManager.getString("instrument." + instrument.toLowerCase()))
        .collect(Collectors.toList());

    instrumentComboBox.getItems().setAll(localizedInstruments);
}

// Date formatting localization
private String formatDate(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy",
                                                             localizationManager.getCurrentLocale());
    return date.format(formatter);
}
```

## Adding New Language Support

### 1. Create Resource File
Create a new properties file in the `src/main/resources/i18n/` directory:
```
messages_[language_code].properties
```
For example: `messages_de.properties` (German)

### 2. Translate All Key-Value Pairs
Copy all keys from existing resource files and translate their values:
```properties
app.name=Musik-Kurs-Plattform
nav.home=Startseite
nav.profile=Profil
# ... other translations
```

### 3. Update Language Selector
Add the new language option to all controllers' language selectors:
```java
languageSelector.getItems().addAll("English", "中文", "العربية", "Deutsch");
```

### 4. Update LocalizationManager (if needed)
If the new language requires special handling (like RTL support), update relevant methods:
```java
public boolean isRTL() {
    String language = currentLocale.getLanguage();
    return "ar".equals(language) || "he".equals(language); // Add Hebrew support
}
```

## Adding New Localizable Strings

### 1. Define Key Naming Conventions
Use meaningful hierarchical key names:
```properties
# Page titles
page.login.title=Login
page.dashboard.title=Dashboard

# Action buttons
action.save=Save
action.cancel=Cancel
action.delete=Delete

# Messages
message.success.login=Login successful
message.error.invalid.credentials=Invalid credentials

# Navigation
nav.home=Home
nav.profile=Profile

# Form labels
form.username=Username
form.password=Password
```

### 2. Add to All Resource Files
Ensure the same key is added to all language resource files:

**messages.properties**:
```properties
form.email=Email Address
```

**messages_zh.properties**:
```properties
form.email=电子邮件地址
```

**messages_ar.properties**:
```properties
form.email=عنوان البريد الإلكتروني
```

### 3. Use in Code
```java
emailLabel.setText(localizationManager.getString("form.email"));
```

## Best Practices

### 1. Character Encoding
- Use UTF-8 encoding for all properties files
- Avoid Unicode escape sequences, use native characters directly

### 2. Key Naming Conventions
- Use dot-separated hierarchical structure
- Use lowercase letters and underscores
- Keep keys descriptive and consistent

### 3. RTL Layout Handling
```java
// Set up RTL support in initialize method
if (localizationManager.isRTL()) {
    rootPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

    // Special handling for navigation buttons
    prevButton.setText("›");
    nextButton.setText("‹");
}
```

### 4. Dynamic Content Updates
```java
// Add language change listeners to ensure real-time updates
localizationManager.addLanguageChangeListener(() -> {
    updateTexts();
    updateDynamicContent();
});
```

### 5. Error Handling
```java
try {
    String text = localizationManager.getString(key);
    return text;
} catch (MissingResourceException e) {
    System.err.println("Missing localization key: " + key);
    return key; // Return key as fallback
}
```

## Architecture Diagram

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│ LocalizationMgr  │───▶│ ResourceBundles │
│                 │    │                  │    │                 │
│ - Dashboard     │    │ - Singleton      │    │ - messages.properties    │
│ - Login         │    │ - Observer       │    │ - messages_zh.properties │
│ - Profile       │    │ - Locale Mgmt    │    │ - messages_ar.properties │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │
         │              ┌────────▼────────┐
         └─────────────▶│ Language Change │
                        │   Listeners     │
                        └─────────────────┘
```

## Testing Recommendations

### Unit Testing
```java
@Test
public void testLocalizationManager() {
    LocalizationManager manager = LocalizationManager.getInstance();

    // Test default language
    assertEquals("Music Course Platform", manager.getString("app.name"));

    // Test language switching
    manager.setLocale(new Locale("zh"));
    assertEquals("音乐课程平台", manager.getString("app.name"));

    // Test RTL detection
    manager.setLocale(new Locale("ar"));
    assertTrue(manager.isRTL());
}
```

### Integration Testing
- Test language switching functionality across all pages
- Verify RTL layout displays correctly in Arabic
- Confirm language selector state persists between pages

## Troubleshooting

### Common Issues

1. **Resource files not loading**
   - Check file path is correct (`src/main/resources/i18n/`)
   - Confirm file name format: `messages_[language_code].properties`

2. **Character display issues**
   - Ensure files are saved with UTF-8 encoding
   - Avoid duplicate key definitions

3. **RTL layout problems**
   - Check the `isRTL()` method language detection logic
   - Ensure proper `NodeOrientation.RIGHT_TO_LEFT` setting

4. **Some text not updating after language switch**
   - Check if language change listeners are added
   - Confirm `updateTexts()` method covers all UI elements

## Maintenance and Extension

### Regular Tasks
- Check if new features have added corresponding localization support
- Verify consistency of key-value pairs across all resource files
- Test display effects of new languages

### Extension Suggestions
- Consider adding more language support
- Implement user language preference persistence storage
- Add plural form handling
- Support parameterized message formats

---

**Version**: 1.0
**Last Updated**: March 29, 2026
**Maintainer**: Music Course Platform Development Team