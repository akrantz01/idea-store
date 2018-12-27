import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.awaitInitialization;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class ProjectApiTest {
    /**
     * Test listing projects with normal request
     */
    @Test
    @DisplayName("GET /api/projects: normal request")
    void getProjects_NormalRequestGiven_ShouldReturnArrayOf3Projects() {
        for (int i = 0; i < 5; i++) Main.db.addProject("test project " + i, "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("GET", "/api/projects");
        assertNotNull(res);
        assertEquals(200, res.status);

        int i = 0;
        for (Map<String, String> j: res.jsonArray()) {
            assertEquals("test project " + i, j.get("title"));
            assertEquals("test project description", j.get("description"));
            i++;
        }
    }

    /**
     * Test getting a specific project with a valid id
     */
    @Test
    @DisplayName("GET /api/projects/%id: valid id")
    void getProject_ValidProjectIDGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("GET", "/api/projects/" + p.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test project", json.get("title"));
        assertEquals("test project description", json.get("description"));
    }

    /**
     * Test getting a specific project with an invalid id
     */
    @Test
    @DisplayName("GET /api/projects/%id: invalid id")
    void getProject_InvalidProjectIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("GET", "/api/projects/1");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("project with id '1' does not exist", json.get("reason"));
    }

    /**
     * Test creating project with proper arguments
     */
    @Test
    @DisplayName("POST /api/projects: proper arguments")
    void postProject_TitleAndDescriptionGiven_ShouldReturnCreated() {
        TestResponse res = TestResponse.request("POST", "/api/projects?" +
                "title=test%20project&description=test%20project%20description&author=test%20user&" +
                "authorId=google-oauth2%7C0&public=true&commissioned=false");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test project", json.get("title"));
        assertEquals("test project description", json.get("description"));
        assertEquals("test user", json.get("author"));
        assertEquals("google-oauth2|0", json.get("authorId"));
        assertEquals(true, json.get("publicReq"));
        assertEquals(false, json.get("commissioned"));
    }

    /**
     * Test creating project with no parameters
     */
    @Test
    @DisplayName("POST /api/projects: no parameters")
    void postProject_NoParametersGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/projects");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("expected query parameters 'title' and 'description', got title='null' and description='null'", json.get("reason"));
    }

    /**
     * Test updating project with valid id and proper arguments
     */
    @Test
    @DisplayName("PUT /api/projects/%id: valid id with proper arguments")
    void putProject_ValidIDAndProperArgumentsGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("PUT", "/api/projects/" + p.getId() + "?title=new%20title&description=new%20description");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("new title", json.get("title"));
        assertEquals("new description", json.get("description"));
    }

    /**
     * Test updating project with valid id
     */
    @Test
    @DisplayName("PUT /api/projects/%id: valid id only")
    void putProject_ValidIDGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("PUT", "/api/projects/" + p.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test project", json.get("title"));
        assertEquals("test project description", json.get("description"));
    }

    /**
     * Test updating project with invalid id
     */
    @Test
    @DisplayName("PUT /api/projects/%id: invalid id")
    void putProject_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/projects/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("project with id '0' does not exist", json.get("reason"));
    }

    /**
     * Test deleting project with valid id
     */
    @Test
    @DisplayName("DELETE /api/projects/%id: valid id")
    void deleteProject_ValidIDGiven_ShouldReturnSuccess() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("DELETE", "/api/projects/" + p.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("success", json.get("status"));
    }

    /**
     * Test deleting project with invalid id
     */
    @Test
    @DisplayName("DELETE /api/projects/%id: invalid id")
    void deleteProject_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("DELETE", "/api/projects/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("project with id '0' does not exist", json.get("reason"));
    }

    /**
     * Start the webserver for testing
     */
    @BeforeAll
    static void setUp() {
        Main.main(null);
        awaitInitialization();
    }

    /**
     * Stop the webserver after testing is done
     */
    @AfterAll
    static void tearDownAfterFinished() {
        Spark.stop();
    }

    /**
     * Purge the database after each test
     */
    @AfterEach
    void tearDownAfterEach() {
        SessionFactory factory;
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object. " + ex);
            throw new ExceptionInInitializerError(ex);
        }

        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM Project").executeUpdate();
            session.createSQLQuery("ALTER TABLE projects AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
