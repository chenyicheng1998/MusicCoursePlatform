# Localization Framework Documentation

## Overview

The Music Course Platform implements a multilingual localization system supporting **English**, **Chinese (中文)**, and **Arabic (العربية)**. The system uses Java’s `ResourceBundle` API and a singleton `LocalizationManager` class (`MusicCoursePlatform/src/main/java/util/LocalizationManager.java`).

**Session behavior:** The selected language lives in a process-wide singleton. It is **not** written to disk or the database; restarting the application resets to the default locale unless you add persistence later.

## Core Components

### LocalizationManager

LocalizationManager is responsible for:

- Loading the correct `ResourceBundle` for the active `Locale`
- Exposing the current locale as a **JavaFX `ObjectProperty<Locale>`** so controllers can listen for changes
- Providing localized strings via `getString(String key)`
- **RTL (right-to-left)** detection for Arabic and applying direction on nodes via `applyDirection(Node)`
- Resolving display names for the language selector (`English`, `中文`, `العربية`)

### Design patterns

1. **Singleton** — one `LocalizationManager` per application.

2. **JavaFX property + listener (not a custom observer list)** — Controllers register on `localeProperty()`:

```java
localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
    updateTexts();
    applyDirection(); // or localizationManager.applyDirection(rootPane);
});
```

When `setLocale` runs, it updates `currentLocale`, so all registered listeners refresh the UI.

## API reference

| Method | Description |
|--------|-------------|
| `getInstance()` | Returns the singleton. |
| `getString(String key)` | Looks up a key in the bundle for the current locale. On failure, logs and returns `"!key!"`. |
| `setLocale(Locale locale)` | Loads the bundle for that locale and sets `currentLocale` (triggers `localeProperty` listeners). |
| `getCurrentLocale()` | Current `Locale`. |
| `localeProperty()` | `ObjectProperty<Locale>` for bindings and listeners. |
| `isRTL()` | `true` when the current locale is Arabic. |
| `applyDirection(Node node)` | Sets `NodeOrientation` to RTL or LTR on that node. |
| `getCurrentLanguageDisplayName()` | e.g. `"English"`, `"中文"`, `"العربية"`. |
| `getLanguageDisplayName(Locale locale)` | **static** — display name for a locale. |
| `getLocaleFromDisplayName(String name)` | **static** — maps selector text to `Locale` (defaults to English for unknown names). |

### Example calls

```java
LocalizationManager lm = LocalizationManager.getInstance();
lm.setLocale(new Locale("ar"));
String t = lm.getString("login.title");
lm.applyDirection(rootPane);
```

Predefined locale constants: `LocalizationManager.ENGLISH`, `CHINESE`, `ARABIC`.

## Resource files

Location: `MusicCoursePlatform/src/main/resources/i18n/`

```
i18n/
├── messages_en.properties   # English
├── messages_zh.properties   # Chinese
├── messages_ar.properties   # Arabic (UTF-8)
└── (optional) en_keys.txt, ar_keys.txt — key lists for maintenance
```

The bundle base name in code is `i18n.messages` (see `BUNDLE_BASE_NAME`). Java resolves:

- `Locale.ENGLISH` / `en` → `messages_en.properties`
- Chinese → `messages_zh.properties`
- Arabic → `messages_ar.properties`

There is **no** root `messages.properties` in this project; English text is in `messages_en.properties`.

### Sample keys (from `messages_en.properties`)

```properties
app.name=MusicCoursePlatform
language.selector=Language
login.title=Log in
login.email=Email
nav.logout=Logout
instrument.piano=Piano
```

The same keys must exist in `messages_zh.properties` and `messages_ar.properties` (with translated values).

## Usage in controllers

### Pattern used in this project (e.g. `LoginController`)

```java
localizationManager = LocalizationManager.getInstance();
setupLanguageSelector(); // items: "English", "中文", "العربية"
updateTexts();

localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
    updateTexts();
    applyDirection();
});
applyDirection();
```

Language change handler:

```java
@FXML
private void handleLanguageChange(ActionEvent event) {
    String selected = languageCombo.getValue();
    Locale newLocale = LocalizationManager.getLocaleFromDisplayName(selected);
    localizationManager.setLocale(newLocale);
}
```

`updateTexts()` should set every visible string from keys, for example:

```java
titleLabel.setText(localizationManager.getString("login.title"));
languageLabel.setText(localizationManager.getString("language.selector"));
```

### RTL / LTR

Prefer `localizationManager.applyDirection(rootPane)` so LTR is restored when leaving Arabic. Some controllers branch on `LocalizationManager.ARABIC.equals(currentLocale)` for extra layout tweaks.

### Dynamic content (instruments, dates)

```java
// Keys: instrument.piano, instrument.guitar, ...
localizationManager.getString("instrument.piano");

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy",
        localizationManager.getCurrentLocale());
```

## Adding a new language

1. Add `messages_<lang>.properties` under `i18n/` with the **same keys** as `messages_en.properties`.
2. Extend `LocalizationManager`: new `Locale` constant, `getLocaleFromDisplayName`, `getLanguageDisplayName`, and `isRTL()` if the language is RTL.
3. Add the display name to every language `ComboBox` in the FXML controllers.

## Adding new strings

1. Add the key to **all** of `messages_en.properties`, `messages_zh.properties`, and `messages_ar.properties`.
2. Use dot-separated names (e.g. `student.filter.instrument`, `message.error.login`).
3. Call `localizationManager.getString("your.key")` in `updateTexts()` or equivalent.

## Best practices

- Save all `.properties` files as **UTF-8**.
- Keep keys synchronized across languages; missing keys show as `!key!` at runtime.
- Register a **`localeProperty`** listener on every screen that should react to language changes.
- For RTL, set orientation on a **root** container when possible.

### Error handling in `getString`

Implementation behavior (do not rely on catching `MissingResourceException` in controllers):

```java
public String getString(String key) {
    try {
        return resourceBundle.getString(key);
    } catch (Exception e) {
        System.err.println("Missing translation key: " + key + " for locale: " + getCurrentLocale());
        return "!" + key + "!";
    }
}
```

## Architecture (conceptual)

```
┌─────────────────────┐     ┌──────────────────────────┐     ┌─────────────────────────────┐
│  JavaFX Controllers │────▶│   LocalizationManager      │────▶│  ResourceBundle files       │
│  (Login, Dashboard, │     │   (singleton)            │     │  messages_en.properties     │
│   Booking, …)       │     │   localeProperty()       │     │  messages_zh.properties     │
│                     │◀────│   getString / applyDir   │     │  messages_ar.properties     │
└─────────────────────┘     └──────────────────────────┘     └─────────────────────────────┘
         ▲
         │  listeners on localeProperty() refresh labels when setLocale() runs
         └──────────────────────────────────────────────────────────────────────────────
```

## Testing

Example assertions aligned with current bundles:

```java
@Test
void localizationManagerBasics() {
    LocalizationManager m = LocalizationManager.getInstance();
    m.setLocale(LocalizationManager.ENGLISH);
    assertEquals("MusicCoursePlatform", m.getString("app.name"));

    m.setLocale(LocalizationManager.CHINESE);
    assertEquals("音乐课程平台", m.getString("app.name"));

    m.setLocale(LocalizationManager.ARABIC);
    assertTrue(m.isRTL());
}
```

Integration checks:

- Switch language on each major screen; all strings and calendar/month names should update.
- Arabic: layout direction and readability.

## Troubleshooting

| Issue | What to check |
|-------|----------------|
| Bundle not found | Path `src/main/resources/i18n/`, base name `i18n.messages`, file `messages_<lang>.properties`. |
| `!key!` on screen | Key missing in the bundle for the active locale. |
| Text not updating | `localeProperty().addListener` missing or `updateTexts()` incomplete. |
| RTL wrong | `applyDirection` on the correct root node; `isRTL()` only treats Arabic as RTL in current code. |

## Maintenance and extensions

- Keep the three property files in sync when adding features.
- **Possible future work:** persist preferred language (e.g. `Preferences` or DB), plural rules, `MessageFormat` for parameters.

---

**Version:** 1.1  
**Last updated:** March 30, 2026  
**Maintainer:** Music Course Platform development team
