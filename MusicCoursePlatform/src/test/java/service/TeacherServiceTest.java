package service;

import dao.TeacherProfileDAO;
import dao.UserDAO;
import model.TeacherProfile;
import model.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TeacherService class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeacherServiceTest {

    private static TeacherService teacherService;
    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;
    private static User testTeacher;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        teacherService = new TeacherService(teacherProfileDAO, userDAO);

        // Create test teacher user
        testTeacher = new User("tstest" + System.currentTimeMillis(), "hash",
                              "ts@test.com", "TEACHER");
        userDAO.create(testTeacher);
    }

    @AfterAll
    static void tearDownClass() {
        // Cleanup
        if (testTeacher != null) {
            TeacherProfile profile = teacherProfileDAO.findByUserId(testTeacher.getUserId());
            if (profile != null) {
                teacherProfileDAO.delete(profile.getTeacherProfileId());
            }
            userDAO.delete(testTeacher.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test: Create teacher profile successfully")
    void testCreateProfile_Success() {
        TeacherProfile profile = teacherService.createProfile(
            testTeacher.getUserId(),
            "Experienced piano teacher",
            "Piano",
            5,
            50,
            "Helsinki"
        );

        assertNotNull(profile);
        assertTrue(profile.getTeacherProfileId() > 0);
        assertEquals("Piano", profile.getInstrumentsTaught());
    }

    @Test
    @Order(2)
    @DisplayName("Test: Create profile for non-existent user")
    void testCreateProfile_UserNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.createProfile(99999, "Bio", "Guitar", 5, 50, "Test");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Test: Create profile with invalid instruments")
    void testCreateProfile_InvalidInstruments() {
        User learner = new User("learner" + System.currentTimeMillis(), "hash",
                               "l@test.com", "LEARNER");
        userDAO.create(learner);

        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.createProfile(learner.getUserId(), "Bio", "", 5, 50, "Test");
        });

        userDAO.delete(learner.getUserId());
    }

    @Test
    @Order(4)
    @DisplayName("Test: Create profile with negative experience")
    void testCreateProfile_NegativeExperience() {
        User teacher2 = new User("teach2" + System.currentTimeMillis(), "hash",
                                "t2@test.com", "TEACHER");
        userDAO.create(teacher2);

        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.createProfile(teacher2.getUserId(), "Bio", "Guitar", -1, 50, "Test");
        });

        userDAO.delete(teacher2.getUserId());
    }

    @Test
    @Order(5)
    @DisplayName("Test: Update teacher profile")
    void testUpdateProfile_Success() {
        TeacherProfile profile = teacherProfileDAO.findByUserId(testTeacher.getUserId());

        TeacherProfile updated = teacherService.updateProfile(
            profile.getTeacherProfileId(),
            "Updated biography",
            "Piano, Guitar",
            10,
            75,
            "Espoo"
        );

        assertNotNull(updated);
        assertEquals("Piano, Guitar", updated.getInstrumentsTaught());
        assertEquals(10, updated.getYearsExperience());
    }

    @Test
    @Order(6)
    @DisplayName("Test: Update non-existent profile")
    void testUpdateProfile_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.updateProfile(99999, "Bio", "Piano", 5, 50, "Test");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test: Get profile by ID")
    void testGetProfileById() {
        TeacherProfile profile = teacherProfileDAO.findByUserId(testTeacher.getUserId());
        TeacherProfile found = teacherService.getProfileById(profile.getTeacherProfileId());

        assertNotNull(found);
        assertEquals(profile.getTeacherProfileId(), found.getTeacherProfileId());
    }

    @Test
    @Order(8)
    @DisplayName("Test: Get profile by user ID")
    void testGetProfileByUserId() {
        TeacherProfile found = teacherService.getProfileByUserId(testTeacher.getUserId());
        assertNotNull(found);
    }

    @Test
    @Order(9)
    @DisplayName("Test: Get all profiles")
    void testGetAllProfiles() {
        List<TeacherProfile> profiles = teacherService.getAllProfiles();
        assertNotNull(profiles);
        assertFalse(profiles.isEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("Test: Search by instrument")
    void testSearchByInstrument() {
        List<TeacherProfile> profiles = teacherService.searchByInstrument("Piano");
        assertNotNull(profiles);
    }

    @Test
    @Order(11)
    @DisplayName("Test: Search by instrument with empty value")
    void testSearchByInstrument_Empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.searchByInstrument("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.searchByInstrument(null);
        });
    }

    @Test
    @Order(12)
    @DisplayName("Test: Search by location")
    void testSearchByLocation() {
        List<TeacherProfile> profiles = teacherService.searchByLocation("Espoo");
        assertNotNull(profiles);
    }

    @Test
    @Order(13)
    @DisplayName("Test: Search by location with empty value")
    void testSearchByLocation_Empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.searchByLocation("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.searchByLocation(null);
        });
    }

    @Test
    @Order(14)
    @DisplayName("Test: Delete profile")
    void testDeleteProfile() {
        // Create a temporary profile for deletion
        User tempTeacher = new User("temp" + System.currentTimeMillis(), "hash",
                                   "temp@test.com", "TEACHER");
        userDAO.create(tempTeacher);

        TeacherProfile tempProfile = teacherService.createProfile(
            tempTeacher.getUserId(), "Temp", "Violin", 3, 40, "Vantaa"
        );

        boolean deleted = teacherService.deleteProfile(tempProfile.getTeacherProfileId());
        assertTrue(deleted);

        userDAO.delete(tempTeacher.getUserId());
    }

    @Test
    @Order(15)
    @DisplayName("Test: Delete non-existent profile")
    void testDeleteProfile_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            teacherService.deleteProfile(99999);
        });
    }
}

