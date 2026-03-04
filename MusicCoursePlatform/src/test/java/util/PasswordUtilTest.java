package util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class.
 * Tests password hashing and verification functionality.
 *
 * @version 1.0 (Sprint 2)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordUtilTest {

    // ==================== Password Hashing Tests ====================

    @Test
    @Order(1)
    @DisplayName("Test: Hash password successfully")
    void testHashPassword_Success() {
        String plainPassword = "MySecurePassword123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(plainPassword, hashedPassword, "Hashed password should not equal plain password");
        assertTrue(hashedPassword.length() > 0, "Hashed password should not be empty");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Hash password produces unique hashes")
    void testHashPassword_UniqueHashes() {
        String plainPassword = "SamePassword123";
        String hash1 = PasswordUtil.hashPassword(plainPassword);
        String hash2 = PasswordUtil.hashPassword(plainPassword);

        // BCrypt generates unique salts, so hashes should be different
        assertNotEquals(hash1, hash2, "Same password should produce different hashes due to unique salts");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Hash password with null throws exception")
    void testHashPassword_NullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        }, "Should throw exception for null password");
    }

    @Test
    @Order(4)
    @DisplayName("Test: Hash password with empty string throws exception")
    void testHashPassword_EmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword("");
        }, "Should throw exception for empty password");
    }

    // ==================== Password Verification Tests ====================

    @Test
    @Order(5)
    @DisplayName("Test: Verify correct password")
    void testVerifyPassword_Correct() {
        String plainPassword = "CorrectPassword123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.verifyPassword(plainPassword, hashedPassword);

        assertTrue(result, "Verification should succeed with correct password");
    }

    @Test
    @Order(6)
    @DisplayName("Test: Verify incorrect password")
    void testVerifyPassword_Incorrect() {
        String plainPassword = "CorrectPassword123";
        String wrongPassword = "WrongPassword123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.verifyPassword(wrongPassword, hashedPassword);

        assertFalse(result, "Verification should fail with incorrect password");
    }

    @Test
    @Order(7)
    @DisplayName("Test: Verify password with null plain password")
    void testVerifyPassword_NullPlainPassword() {
        String hashedPassword = PasswordUtil.hashPassword("SomePassword123");

        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verifyPassword(null, hashedPassword);
        }, "Should throw exception for null plain password");
    }

    @Test
    @Order(8)
    @DisplayName("Test: Verify password with null hashed password")
    void testVerifyPassword_NullHashedPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verifyPassword("SomePassword123", null);
        }, "Should throw exception for null hashed password");
    }

    @Test
    @Order(9)
    @DisplayName("Test: Verify password with empty plain password")
    void testVerifyPassword_EmptyPlainPassword() {
        String hashedPassword = PasswordUtil.hashPassword("SomePassword123");

        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verifyPassword("", hashedPassword);
        }, "Should throw exception for empty plain password");
    }

    @Test
    @Order(10)
    @DisplayName("Test: Verify password with empty hashed password")
    void testVerifyPassword_EmptyHashedPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.verifyPassword("SomePassword123", "");
        }, "Should throw exception for empty hashed password");
    }

    // ==================== Password Strength Tests ====================

    @Test
    @Order(11)
    @DisplayName("Test: Check strong password (8+ characters)")
    void testIsPasswordStrong_Valid() {
        assertTrue(PasswordUtil.isPasswordStrong("12345678"), "8 characters should be strong");
        assertTrue(PasswordUtil.isPasswordStrong("MyPassword123"), "Long password should be strong");
        assertTrue(PasswordUtil.isPasswordStrong("VeryLongPasswordWithManyCharacters"), "Very long password should be strong");
    }

    @Test
    @Order(12)
    @DisplayName("Test: Check weak password (less than 8 characters)")
    void testIsPasswordStrong_Weak() {
        assertFalse(PasswordUtil.isPasswordStrong("short"), "Short password should be weak");
        assertFalse(PasswordUtil.isPasswordStrong("1234567"), "7 characters should be weak");
        assertFalse(PasswordUtil.isPasswordStrong("abc"), "Very short password should be weak");
    }

    @Test
    @Order(13)
    @DisplayName("Test: Check password strength with null")
    void testIsPasswordStrong_Null() {
        assertFalse(PasswordUtil.isPasswordStrong(null), "Null password should be weak");
    }

    @Test
    @Order(14)
    @DisplayName("Test: Check password strength with empty string")
    void testIsPasswordStrong_Empty() {
        assertFalse(PasswordUtil.isPasswordStrong(""), "Empty password should be weak");
    }

    @Test
    @Order(15)
    @DisplayName("Test: Check password strength boundary (exactly 8 characters)")
    void testIsPasswordStrong_Boundary() {
        assertTrue(PasswordUtil.isPasswordStrong("12345678"), "Exactly 8 characters should be strong");
    }

    // ==================== Integration Tests ====================

    @Test
    @Order(16)
    @DisplayName("Test: Complete hash-verify cycle")
    void testCompleteHashVerifyCycle() {
        String plainPassword = "IntegrationTest123";

        // Hash the password
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        // Verify with correct password
        assertTrue(PasswordUtil.verifyPassword(plainPassword, hashedPassword),
            "Should verify successfully with correct password");

        // Verify with wrong password
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hashedPassword),
            "Should fail with wrong password");
    }

    @Test
    @Order(17)
    @DisplayName("Test: Multiple users with same password get different hashes")
    void testMultipleUsersWithSamePassword() {
        String sharedPassword = "SharedPassword123";

        String hash1 = PasswordUtil.hashPassword(sharedPassword);
        String hash2 = PasswordUtil.hashPassword(sharedPassword);
        String hash3 = PasswordUtil.hashPassword(sharedPassword);

        // All hashes should be different (due to unique salts)
        assertNotEquals(hash1, hash2, "Hash 1 and Hash 2 should be different");
        assertNotEquals(hash2, hash3, "Hash 2 and Hash 3 should be different");
        assertNotEquals(hash1, hash3, "Hash 1 and Hash 3 should be different");

        // But all should verify correctly
        assertTrue(PasswordUtil.verifyPassword(sharedPassword, hash1));
        assertTrue(PasswordUtil.verifyPassword(sharedPassword, hash2));
        assertTrue(PasswordUtil.verifyPassword(sharedPassword, hash3));
    }

    @Test
    @Order(18)
    @DisplayName("Test: Password with special characters")
    void testPasswordWithSpecialCharacters() {
        String complexPassword = "P@ssw0rd!#$%";
        String hashedPassword = PasswordUtil.hashPassword(complexPassword);

        assertTrue(PasswordUtil.verifyPassword(complexPassword, hashedPassword),
            "Should handle special characters correctly");
    }

    @Test
    @Order(19)
    @DisplayName("Test: Password with spaces")
    void testPasswordWithSpaces() {
        String passwordWithSpaces = "My Password 123";
        String hashedPassword = PasswordUtil.hashPassword(passwordWithSpaces);

        assertTrue(PasswordUtil.verifyPassword(passwordWithSpaces, hashedPassword),
            "Should handle spaces correctly");
    }

    @Test
    @Order(20)
    @DisplayName("Test: Unicode characters in password")
    void testPasswordWithUnicode() {
        String unicodePassword = "密码123测试";
        String hashedPassword = PasswordUtil.hashPassword(unicodePassword);

        assertTrue(PasswordUtil.verifyPassword(unicodePassword, hashedPassword),
            "Should handle Unicode characters correctly");
    }
}

