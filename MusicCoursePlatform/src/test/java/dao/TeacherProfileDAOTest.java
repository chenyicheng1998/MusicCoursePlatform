package dao;

import model.TeacherProfile;
import model.User;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeacherProfileDAOTest {

    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;
    private static User testTeacher;
    private static TeacherProfile testProfile;
    private static final String TEST_PREFIX = "test_tp_" + System.currentTimeMillis() + "_";

    @BeforeAll
    static void setUpClass() {
        teacherProfileDAO = new TeacherProfileDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() {
        testTeacher = new User(
            TEST_PREFIX + "teacher",
            "hashed_password",
            TEST_PREFIX + "teacher@test.com",
            "TEACHER"
        );
        userDAO.create(testTeacher);

        testProfile = new TeacherProfile(
            testTeacher.getUserId(),
            "Experienced music teacher",
            "Piano,Guitar",
            5,
            new BigDecimal("50.00"),
            "Helsinki"
        );
    }

    @AfterEach
    void tearDown() {
        if (testProfile != null && testProfile.getProfileId() > 0) {
            teacherProfileDAO.delete(testProfile.getProfileId());
        }
        if (testTeacher != null && testTeacher.getUserId() > 0) {
            userDAO.delete(testTeacher.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create teacher profile successfully")
    void testCreate_Success() {
        boolean result = teacherProfileDAO.create(testProfile);

        assertTrue(result);
        assertTrue(testProfile.getProfileId() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Find profile by ID")
    void testFindById() {
        teacherProfileDAO.create(testProfile);

        TeacherProfile found = teacherProfileDAO.findById(testProfile.getProfileId());

        assertNotNull(found);
        assertEquals(testProfile.getUserId(), found.getUserId());
        assertEquals("Piano,Guitar", found.getInstrumentsTaught());
    }

    @Test
    @Order(3)
    @DisplayName("Find profile by user ID")
    void testFindByUserId() {
        teacherProfileDAO.create(testProfile);

        TeacherProfile found = teacherProfileDAO.findByUserId(testTeacher.getUserId());

        assertNotNull(found);
        assertEquals(testProfile.getProfileId(), found.getProfileId());
    }

    @Test
    @Order(4)
    @DisplayName("Find profiles by instrument")
    void testFindByInstrument() {
        teacherProfileDAO.create(testProfile);

        List<TeacherProfile> profiles = teacherProfileDAO.findByInstrument("Piano");

        assertNotNull(profiles);
        assertTrue(profiles.stream().anyMatch(p -> p.getProfileId() == testProfile.getProfileId()));
    }

    @Test
    @Order(5)
    @DisplayName("Find profiles by location")
    void testFindByLocation() {
        teacherProfileDAO.create(testProfile);

        List<TeacherProfile> profiles = teacherProfileDAO.findByLocation("Helsinki");

        assertNotNull(profiles);
        assertTrue(profiles.stream().anyMatch(p -> p.getProfileId() == testProfile.getProfileId()));
    }

    @Test
    @Order(6)
    @DisplayName("Find all profiles")
    void testFindAll() {
        teacherProfileDAO.create(testProfile);

        List<TeacherProfile> profiles = teacherProfileDAO.findAll();

        assertNotNull(profiles);
        assertFalse(profiles.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Update profile successfully")
    void testUpdate() {
        teacherProfileDAO.create(testProfile);

        testProfile.setInstrumentsTaught("Piano,Guitar,Violin");
        testProfile.setHourlyRate(new BigDecimal("60.00"));

        boolean result = teacherProfileDAO.update(testProfile);

        assertTrue(result);

        TeacherProfile updated = teacherProfileDAO.findById(testProfile.getProfileId());
        assertEquals("Piano,Guitar,Violin", updated.getInstrumentsTaught());
        assertEquals(new BigDecimal("60.00"), updated.getHourlyRate());
    }

    @Test
    @Order(8)
    @DisplayName("Delete profile successfully")
    void testDelete() {
        teacherProfileDAO.create(testProfile);
        int profileId = testProfile.getProfileId();

        boolean result = teacherProfileDAO.delete(profileId);

        assertTrue(result);
        assertNull(teacherProfileDAO.findById(profileId));

        testProfile.setProfileId(0);
    }

    @Test
    @Order(9)
    @DisplayName("Find non-existent profile returns null")
    void testFindById_NotFound() {
        TeacherProfile found = teacherProfileDAO.findById(999999);
        assertNull(found);
    }
}
