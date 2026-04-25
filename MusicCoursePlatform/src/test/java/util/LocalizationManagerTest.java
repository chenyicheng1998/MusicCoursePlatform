package util;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class LocalizationManagerTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await();
    }

    @AfterEach
    void resetToEnglish() {
        LocalizationManager.getInstance().setLocale(LocalizationManager.ENGLISH);
    }

    @Test
    void testGetInstance_ReturnsSingleton() {
        assertSame(LocalizationManager.getInstance(), LocalizationManager.getInstance());
    }

    @Test
    void testSetLocale_Chinese_ChangesCurrentLocale() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.CHINESE);
        assertEquals(LocalizationManager.CHINESE, lm.getCurrentLocale());
    }

    @Test
    void testSetLocale_Arabic_ChangesCurrentLocale() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ARABIC);
        assertEquals(LocalizationManager.ARABIC, lm.getCurrentLocale());
    }

    @Test
    void testGetString_ValidKey_ReturnsTranslation() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        String result = lm.getString("login.title");
        assertNotNull(result);
        assertFalse(result.startsWith("!"));
    }

    @Test
    void testGetString_MissingKey_ReturnsMarkedPlaceholder() {
        String result = LocalizationManager.getInstance().getString("nonexistent.key.xyz.abc");
        assertEquals("!nonexistent.key.xyz.abc!", result);
    }

    @Test
    void testIsRTL_Arabic_ReturnsTrue() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ARABIC);
        assertTrue(lm.isRTL());
    }

    @Test
    void testIsRTL_English_ReturnsFalse() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        assertFalse(lm.isRTL());
    }

    @Test
    void testIsRTL_Chinese_ReturnsFalse() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.CHINESE);
        assertFalse(lm.isRTL());
    }

    @Test
    void testApplyDirection_LTR_English() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final NodeOrientation[] result = { null };
        Platform.runLater(() -> {
            LocalizationManager lm = LocalizationManager.getInstance();
            lm.setLocale(LocalizationManager.ENGLISH);
            Label node = new Label();
            lm.applyDirection(node);
            result[0] = node.getNodeOrientation();
            latch.countDown();
        });
        latch.await();
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, result[0]);
    }

    @Test
    void testApplyDirection_RTL_Arabic() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final NodeOrientation[] result = { null };
        Platform.runLater(() -> {
            LocalizationManager lm = LocalizationManager.getInstance();
            lm.setLocale(LocalizationManager.ARABIC);
            Label node = new Label();
            lm.applyDirection(node);
            result[0] = node.getNodeOrientation();
            latch.countDown();
        });
        latch.await();
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, result[0]);
    }

    @Test
    void testGetLocaleFromDisplayName_Chinese() {
        assertEquals(LocalizationManager.CHINESE, LocalizationManager.getLocaleFromDisplayName("中文"));
    }

    @Test
    void testGetLocaleFromDisplayName_Arabic() {
        assertEquals(LocalizationManager.ARABIC, LocalizationManager.getLocaleFromDisplayName("العربية"));
    }

    @Test
    void testGetLocaleFromDisplayName_English() {
        assertEquals(LocalizationManager.ENGLISH, LocalizationManager.getLocaleFromDisplayName("English"));
    }

    @Test
    void testGetLocaleFromDisplayName_Unknown_DefaultsToEnglish() {
        assertEquals(LocalizationManager.ENGLISH, LocalizationManager.getLocaleFromDisplayName("Français"));
    }

    @Test
    void testGetLanguageDisplayName_English() {
        assertEquals("English", LocalizationManager.getLanguageDisplayName(LocalizationManager.ENGLISH));
    }

    @Test
    void testGetLanguageDisplayName_Chinese() {
        assertEquals("中文", LocalizationManager.getLanguageDisplayName(LocalizationManager.CHINESE));
    }

    @Test
    void testGetLanguageDisplayName_Arabic() {
        assertEquals("العربية", LocalizationManager.getLanguageDisplayName(LocalizationManager.ARABIC));
    }

    @Test
    void testGetLanguageDisplayName_Other_ReturnDisplayLanguage() {
        Locale french = Locale.forLanguageTag("fr");
        String result = LocalizationManager.getLanguageDisplayName(french);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetCurrentLanguageDisplayName_English() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        assertEquals("English", lm.getCurrentLanguageDisplayName());
    }

    @Test
    void testGetCurrentLanguageDisplayName_Chinese() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.CHINESE);
        assertEquals("中文", lm.getCurrentLanguageDisplayName());
    }

    @Test
    void testLocaleProperty_NotNull() {
        assertNotNull(LocalizationManager.getInstance().localeProperty());
    }

    @Test
    void testGetInstrumentKey_NullInput_ReturnsNull() {
        assertNull(LocalizationManager.getInstance().getInstrumentKey(null));
    }

    @Test
    void testGetInstrumentKey_CanonicalKey_ReturnsSelf() {
        assertEquals("piano", LocalizationManager.getInstance().getInstrumentKey("piano"));
    }

    @Test
    void testGetInstrumentKey_CanonicalKeyUppercase_Returns() {
        assertEquals("guitar", LocalizationManager.getInstance().getInstrumentKey("GUITAR"));
    }

    @Test
    void testGetInstrumentKey_LocalizedEnglishName_ReturnsKey() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        String englishName = lm.getString("instrument.violin");
        String key = lm.getInstrumentKey(englishName);
        assertEquals("violin", key);
    }

    @Test
    void testGetInstrumentKey_UnknownName_ReturnsNull() {
        assertNull(LocalizationManager.getInstance().getInstrumentKey("theremin"));
    }

    @Test
    void testGetLocalizedInstrumentName_Null_ReturnsUnknownMessage() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        String result = lm.getLocalizedInstrumentName(null);
        assertNotNull(result);
    }

    @Test
    void testGetLocalizedInstrumentName_KnownKey_ReturnsLocalized() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lm.setLocale(LocalizationManager.ENGLISH);
        String result = lm.getLocalizedInstrumentName("piano");
        assertEquals(lm.getString("instrument.piano"), result);
    }

    @Test
    void testGetLocalizedInstrumentName_UnknownKey_ReturnsFallback() {
        String result = LocalizationManager.getInstance().getLocalizedInstrumentName("theremin");
        assertEquals("theremin", result);
    }

    @Test
    void testGetAllInstrumentVariants_Null_ReturnsEmptyList() {
        List<String> variants = LocalizationManager.getInstance().getAllInstrumentVariants(null);
        assertTrue(variants.isEmpty());
    }

    @Test
    void testGetAllInstrumentVariants_ValidKey_IncludesCanonical() {
        List<String> variants = LocalizationManager.getInstance().getAllInstrumentVariants("piano");
        assertFalse(variants.isEmpty());
        assertTrue(variants.contains("piano"));
    }

    @Test
    void testGetAllInstrumentVariants_ValidKey_IncludesMultipleLocales() {
        List<String> variants = LocalizationManager.getInstance().getAllInstrumentVariants("guitar");
        assertTrue(variants.size() > 1);
    }
}
