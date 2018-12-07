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
class IdeaApiTest {
    /**
     * Test listing ideas with normal request
     */
    @Test
    void getIdeas_NormalRequestGiven_ShouldReturnArrayOf3Ideas() {
        for (int i = 0; i < 5; i++) Main.db.addIdea("test idea " + i, "test idea description");

        TestResponse res = TestResponse.request("GET", "/api/ideas");
        assertNotNull(res);
        assertEquals(200, res.status);

        int i = 0;
        for (Map<String, String> j: res.jsonArray()) {
            assertEquals("test idea " + i, j.get("title"));
            assertEquals("test idea description", j.get("description"));
            i++;
        }
    }

    /**
     * Test getting a specific idea with a valid id
     */
    @Test
    void getIdea_ValidIdeaIDGiven_ShouldReturnIdea() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("GET", "/api/ideas/" + i.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test idea", json.get("title"));
        assertEquals("test idea description", json.get("description"));
    }

    /**
     * Test getting a specific idea with an invalid id
     */
    @Test
    void getIdea_InvalidIdeaIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("GET", "/api/ideas/1");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '1' does not exist", json.get("reason"));
    }

    /**
     * Test creating idea with test and description
     */
    @Test
    void postIdea_TitleAndDescriptionGiven_ShouldReturnCreated() {
        TestResponse res = TestResponse.request("POST", "/api/ideas?title=test%20title&description=test%20description");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test title", json.get("title"));
        assertEquals("test description", json.get("description"));
    }

    /**
     * Test creating idea with only title
     */
    @Test
    void postIdea_OnlyTitleGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/ideas?title=test%20title");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("expected query parameters 'title' and 'description', got title='test title' and description='null'", json.get("reason"));
    }

    /**
     * Test creating idea with only description
     */
    @Test
    void postIdea_OnlyDescriptionGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/ideas?description=test%20description");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("expected query parameters 'title' and 'description', got title='null' and description='test description'", json.get("reason"));
    }

    /**
     * Test creating idea with no parameters
     */
    @Test
    void postIdea_NoParametersGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/ideas");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("expected query parameters 'title' and 'description', got title='null' and description='null'", json.get("reason"));
    }

    /**
     * Test updating idea with valid id, title and description
     */
    @Test
    void updateIdea_ValidIDTitleAndDescriptionGiven_ShouldReturnIdea() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("PUT", "/api/ideas/" + i.getId() + "?title=new%20title&description=new%20description");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("new title", json.get("title"));
        assertEquals("new description", json.get("description"));
    }

    /**
     * Test updating idea with valid id and title
     */
    @Test
    void updateIdea_ValidIDAndTitleGiven_ShouldReturnIdea() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("PUT", "/api/ideas/" + i.getId() + "?title=new%20title");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("new title", json.get("title"));
        assertEquals("test idea description", json.get("description"));
    }

    /**
     * Test updating idea with valid id and description
     */
    @Test
    void updateIdea_ValidIDAndDescriptionGiven_ShouldReturnIdea() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("PUT", "/api/ideas/" + i.getId() + "?description=new%20description");
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test idea", json.get("title"));
        assertEquals("new description", json.get("description"));
    }

    /**
     * Test updating idea with valid id
     */
    @Test
    void updateIdea_ValidIDGiven_ShouldReturnIdea() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("PUT", "/api/ideas/" + i.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("test idea", json.get("title"));
        assertEquals("test idea description", json.get("description"));
    }

    /**
     * Test updating idea with invalid id, title and description
     */
    @Test
    void updateIdea_InvalidIDTitleAndDescriptionGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/ideas/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '0' does not exist", json.get("reason"));
    }

    /**
     * Test updating idea with invalid id and title
     */
    @Test
    void updateIdea_InvalidIDAndTitleGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/ideas/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '0' does not exist", json.get("reason"));
    }

    /**
     * Test updating idea with invalid id and description
     */
    @Test
    void updateIdea_InvalidIDAndDescriptionGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/ideas/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '0' does not exist", json.get("reason"));
    }

    /**
     * Test updating idea with invalid id
     */
    @Test
    void updateIdea_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/ideas/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '0' does not exist", json.get("reason"));
    }

    /**
     * Test deleting idea with valid id
     */
    @Test
    void deleteIdea_ValidIDGiven_ShouldReturnSuccess() {
        Idea i = Main.db.addIdea("test idea", "test idea description");

        TestResponse res = TestResponse.request("DELETE", "/api/ideas/" + i.getId());
        assertNotNull(res);
        assertEquals(200, res.status);

        Map<String, String> json = res.json();
        assertEquals("success", json.get("status"));
    }

    /**
     * Test deleting idea with invalid id
     */
    @Test
    void deleteIdea_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("DELETE", "/api/ideas/0");
        assertNotNull(res);
        assertEquals(400, res.status);

        Map<String, String> json = res.json();
        assertEquals("error", json.get("status"));
        assertEquals("idea with id '0' does not exist", json.get("reason"));
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
            session.createQuery("DELETE FROM Idea").executeUpdate();
            session.createSQLQuery("ALTER TABLE ideas AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
