package service;

import dao.UserDAO;
import model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserService class.
 * Tests user registration, authentication, and validation logic.
 *
 * @version 1.0 (Sprint 2)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    private static UserService userService;
    private static UserDAO userDAO;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        userService = new UserService(userDAO);
    }

    @AfterEach
    void tearDown() {
        // Clean up: delete test users by finding them by username patterns
        // This is a safety cleanup - individual tests should clean up their own data
    }

    // ==================== Registration Tests ====================

    @Test
    @Order(1)
    @DisplayName("Test: Register user successfully")
    void testRegisterUser_Success() {
        String username = "user" + (System.currentTimeMillis() % 100000);
        String password = "ValidPass123";
        String email = "test" + (System.currentTimeMillis() % 100000) + "@test.com";
        String userType = "LEARNER";

        User user = userService.registerUser(username, password, email, userType);

        assertNotNull(user, "User should be created");
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(userType, user.getUserType());
        assertTrue(user.getUserId() > 0, "User ID should be set");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Register user with duplicate username fails")
    void testRegisterUser_DuplicateUsername() {
        String username = "dupuser" + (System.currentTimeMillis() % 10000);
        String password = "ValidPass123";
        String email1 = "email1" + (System.currentTimeMillis() % 100000) + "@test.com";
        String email2 = "email2" + (System.currentTimeMillis() % 100000) + "@test.com";

        // Create first user
        userService.registerUser(username, password, email1, "LEARNER");

        // Try to create second user with same username
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(username, password, email2, "TEACHER");
        }, "Should throw exception for duplicate username");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Register user with duplicate email fails")
    void testRegisterUser_DuplicateEmail() {
        String username1 = "user1" + (System.currentTimeMillis() % 10000);
        String username2 = "user2" + (System.currentTimeMillis() % 10000);
        String password = "ValidPass123";
        String email = "same" + (System.currentTimeMillis() % 100000) + "@test.com";

        // Create first user
        userService.registerUser(username1, password, email, "LEARNER");

        // Try to create second user with same email
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(username2, password, email, "TEACHER");
        }, "Should throw exception for duplicate email");

        // Cleanup
        User user1 = userDAO.findByUsername(username1);
        if (user1 != null)
            userDAO.delete(user1.getUserId());
    }

    // ==================== Authentication Tests ====================

    @Test
    @Order(4)
    @DisplayName("Test: Authenticate user successfully")
    void testAuthenticateUser_Success() {
        String username = "authuser" + (System.currentTimeMillis() % 10000);
        String password = "ValidPass123";
        String email = "auth" + (System.currentTimeMillis() % 100000) + "@test.com";

        // Create user first
        userService.registerUser(username, password, email, "LEARNER");

        // Try to authenticate
        User authenticatedUser = userService.authenticateUser(username, password);

        assertNotNull(authenticatedUser);
        assertEquals(username, authenticatedUser.getUsername());
    }

    @Test
    @Order(5)
    @DisplayName("Test: Authenticate with wrong password fails")
    void testAuthenticateUser_WrongPassword() {
        String username = "pwduser" + (System.currentTimeMillis() % 10000);
        String password = "ValidPass123";
        String wrongPassword = "WrongPass123";
        String email = "pwd" + (System.currentTimeMillis() % 100000) + "@test.com";

        // Create user first
        userService.registerUser(username, password, email, "LEARNER");

        // Try to authenticate with wrong password
        assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser(username, wrongPassword);
        }, "Should throw exception for wrong password");
    }

    @Test
    @Order(6)
    @DisplayName("Test: Authenticate non-existent user fails")
    void testAuthenticateUser_NonExistent() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser("nonexistent_user_xyz", "password123");
        }, "Should throw exception for non-existent user");
    }

    @Test
    @Order(7)
    @DisplayName("Test: Authenticate by email successfully")
    void testAuthenticateByEmail_Success() {
        String username = "emluser" + (System.currentTimeMillis() % 10000);
        String password = "ValidPass123";
        String email = "eml" + (System.currentTimeMillis() % 100000) + "@test.com";

        // Create user first
        userService.registerUser(username, password, email, "LEARNER");

        // Try to authenticate by email
        User authenticatedUser = userService.authenticateByEmail(email, password);

        assertNotNull(authenticatedUser);
        assertEquals(email, authenticatedUser.getEmail());
    }

    // ==================== Validation Tests ====================

    @Test
    @Order(8)
    @DisplayName("Test: Validate username - valid")
    void testValidateUsername_Valid() {
        assertDoesNotThrow(() -> userService.validateUsername("validUser123"));
    }

    @Test
    @Order(9)
    @DisplayName("Test: Validate username - too short")
    void testValidateUsername_TooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUsername("ab");
        }, "Should throw exception for username too short");
    }

    @Test
    @Order(10)
    @DisplayName("Test: Validate username - too long")
    void testValidateUsername_TooLong() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUsername("verylongusernamethatexceedstwentycharacters");
        }, "Should throw exception for username too long");
    }

    @Test
    @Order(11)
    @DisplayName("Test: Validate username - invalid characters")
    void testValidateUsername_InvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUsername("user@name");
        }, "Should throw exception for invalid characters");
    }

    @Test
    @Order(12)
    @DisplayName("Test: Validate username - null or empty")
    void testValidateUsername_NullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.validateUsername(null));
        assertThrows(IllegalArgumentException.class, () -> userService.validateUsername(""));
    }

    @Test
    @Order(13)
    @DisplayName("Test: Validate email - valid")
    void testValidateEmail_Valid() {
        assertDoesNotThrow(() -> userService.validateEmail("valid@test.com"));
        assertDoesNotThrow(() -> userService.validateEmail("user.name@example.co.uk"));
    }

    @Test
    @Order(14)
    @DisplayName("Test: Validate email - invalid format")
    void testValidateEmail_InvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> userService.validateEmail("notanemail"));
        assertThrows(IllegalArgumentException.class, () -> userService.validateEmail("missing@domain"));
        assertThrows(IllegalArgumentException.class, () -> userService.validateEmail("@nodomain.com"));
    }

    @Test
    @Order(15)
    @DisplayName("Test: Validate email - null or empty")
    void testValidateEmail_NullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.validateEmail(null));
        assertThrows(IllegalArgumentException.class, () -> userService.validateEmail(""));
    }

    @Test
    @Order(16)
    @DisplayName("Test: Validate password - valid")
    void testValidatePassword_Valid() {
        assertDoesNotThrow(() -> userService.validatePassword("ValidPass123"));
        assertDoesNotThrow(() -> userService.validatePassword("12345678"));
    }

    @Test
    @Order(17)
    @DisplayName("Test: Validate password - too short")
    void testValidatePassword_TooShort() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validatePassword("short");
        }, "Should throw exception for password too short");
    }

    @Test
    @Order(18)
    @DisplayName("Test: Validate password - null or empty")
    void testValidatePassword_NullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.validatePassword(null));
        assertThrows(IllegalArgumentException.class, () -> userService.validatePassword(""));
    }

    @Test
    @Order(19)
    @DisplayName("Test: Validate user type - valid")
    void testValidateUserType_Valid() {
        assertDoesNotThrow(() -> userService.validateUserType("TEACHER"));
        assertDoesNotThrow(() -> userService.validateUserType("LEARNER"));
    }

    @Test
    @Order(20)
    @DisplayName("Test: Validate user type - invalid")
    void testValidateUserType_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUserType("ADMIN");
        }, "Should throw exception for invalid user type");
    }

    @Test
    @Order(21)
    @DisplayName("Test: Validate user type - null or empty")
    void testValidateUserType_NullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.validateUserType(null));
        assertThrows(IllegalArgumentException.class, () -> userService.validateUserType(""));
    }

    @Test
    @Order(22)
    @DisplayName("Test: Check username availability")
    void testIsUsernameAvailable() {
        String username = "avluser" + (System.currentTimeMillis() % 10000);

        // Should be available before creation
        assertTrue(userService.isUsernameAvailable(username));

        // Create user
        userService.registerUser(username, "ValidPass123", "avl" + (System.currentTimeMillis() % 100000) + "@test.com",
                "LEARNER");

        // Should not be available after creation
        assertFalse(userService.isUsernameAvailable(username));
    }

    @Test
    @Order(23)
    @DisplayName("Test: Check email availability")
    void testIsEmailAvailable() {
        String email = "avail" + System.currentTimeMillis() + "@test.com";

        // Should be available before creation
        assertTrue(userService.isEmailAvailable(email));

        // Create user
        userService.registerUser("ec" + (System.currentTimeMillis() % 100000000L), "ValidPass123", email, "LEARNER");

        // Should not be available after creation
        assertFalse(userService.isEmailAvailable(email));
    }
}
