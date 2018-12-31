import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import spark.Spark;

import java.text.SimpleDateFormat;
import java.util.Date;

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
                "test author", "google-oauth2|0", true, false);

        TestResponse res = TestResponse.request("GET", "/api/projects", null);
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());
        JsonArray json = stdres.getData().getAsJsonArray();

        for (int i = 0; i < 5; i++) {
            JsonObject j = json.get(i).getAsJsonObject();
            assertEquals("test project " + i, j.get("title").getAsString());
            assertEquals("test project description", j.get("description").getAsString());
            assertEquals("test author", j.get("author").getAsString());
            assertEquals("google-oauth2|0", j.get("authorId").getAsString());
            assertEquals("queued", j.get("status").getAsString());
            assertEquals(-1, j.get("priority").getAsInt());
            assertTrue(j.get("publicReq").getAsBoolean());
            assertFalse(j.get("deleted").getAsBoolean());
            assertFalse(j.get("commissioned").getAsBoolean());
            assertFalse(j.get("commissionAccepted").getAsBoolean());
            assertEquals("", j.get("commissionNotes").getAsString());
            assertEquals(0.0, j.get("commissionCost").getAsDouble());
            assertEquals("", j.get("commissionStart").getAsString());
            assertEquals("", j.get("commissionEnd").getAsString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
            assertEquals(dateFormat.format(new Date()), j.get("addedDate").getAsString());
            assertEquals("", j.get("editedDate").getAsString());
        }
    }

    /**
     * Test getting a specific project with a valid id
     */
    @Test
    @DisplayName("GET /api/projects/%id: valid id")
    void getProject_ValidProjectIDGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth2|0", true, false);

        TestResponse res = TestResponse.request("GET", "/api/projects/" + p.getId(), null);
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());
        JsonObject json = stdres.getData().getAsJsonObject();

        assertEquals("test project", json.get("title").getAsString());
        assertEquals("test project description", json.get("description").getAsString());
        assertEquals("test author", json.get("author").getAsString());
        assertEquals("google-oauth2|0", json.get("authorId").getAsString());
        assertEquals("queued", json.get("status").getAsString());
        assertEquals(-1, json.get("priority").getAsInt());
        assertTrue(json.get("publicReq").getAsBoolean());
        assertFalse(json.get("deleted").getAsBoolean());
        assertFalse(json.get("commissioned").getAsBoolean());
        assertFalse(json.get("commissionAccepted").getAsBoolean());
        assertEquals("", json.get("commissionNotes").getAsString());
        assertEquals(0.0, json.get("commissionCost").getAsDouble());
        assertEquals("", json.get("commissionStart").getAsString());
        assertEquals("", json.get("commissionEnd").getAsString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        assertEquals(dateFormat.format(new Date()), json.get("addedDate").getAsString());
        assertEquals("", json.get("editedDate").getAsString());
    }

    /**
     * Test getting a specific project with a non-existent id
     */
    @Test
    @DisplayName("GET /api/projects/%id: non-existent id")
    void getProject_NonExistentIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("GET", "/api/projects/0", null);
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("project with id '0' does not exist", stdres.getMessage());
    }

    /**
     * Test getting a specific project with an invalid id
     */
    @Test
    @DisplayName("GET /api/projects/%id: invalid id")
    void getProject_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("GET", "/api/projects/abc", null);
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("project id 'abc' is invalid", stdres.getMessage());
    }

    /**
     * Test creating project with proper arguments
     */
    @Test
    @DisplayName("POST /api/projects: proper arguments")
    void postProject_TitleAndDescriptionGiven_ShouldReturnCreated() {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", -1);
        jo.addProperty("title", "test project");
        jo.addProperty("description", "test description");
        jo.addProperty("author", "test user");
        jo.addProperty("authorId", "google-oauth2|0");
        jo.addProperty("status", "working");
        jo.addProperty("priority", -1);
        jo.addProperty("publicReq", true);
        jo.addProperty("deleted", false);
        jo.addProperty("commissioned", false);
        jo.addProperty("commissionAccepted", false);
        jo.addProperty("commissionNotes", "");
        jo.addProperty("commissionCost", 0.0);
        jo.addProperty("commissionStart", "");
        jo.addProperty("commissionEnd", "");
        jo.addProperty("addedDate", "12/12/12");
        jo.addProperty("editedDate", "");

        TestResponse res = TestResponse.request("POST", "/api/projects", jo.toString());
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());
        JsonObject json = stdres.getData().getAsJsonObject();

        assertEquals("test project", json.get("title").getAsString());
        assertEquals("test description", json.get("description").getAsString());
        assertEquals("test user", json.get("author").getAsString());
        assertEquals("google-oauth2|0", json.get("authorId").getAsString());
        assertEquals("working", json.get("status").getAsString());
        assertEquals(-1, json.get("priority").getAsInt());
        assertTrue(json.get("publicReq").getAsBoolean());
        assertFalse(json.get("deleted").getAsBoolean());
        assertFalse(json.get("commissioned").getAsBoolean());
        assertFalse(json.get("commissionAccepted").getAsBoolean());
        assertEquals("", json.get("commissionNotes").getAsString());
        assertEquals(0.0, json.get("commissionCost").getAsDouble());
        assertEquals("", json.get("commissionStart").getAsString());
        assertEquals("", json.get("commissionEnd").getAsString());
        assertEquals("12/12/12", json.get("addedDate").getAsString());
        assertEquals("", json.get("editedDate").getAsString());
    }

    /**
     * Test creating project with no parameters
     */
    @Test
    @DisplayName("POST /api/projects: no parameters")
    void postProject_NoParametersGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/projects", "{}");
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();

        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("invalid JSON format", stdres.getMessage());
    }

    /**
     * Test creating project with invalid JSON
     */
    @Test
    @DisplayName("POST /api/projects: invalid json")
    void postProject_InvalidJSONGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("POST", "/api/projects", "{");
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();

        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("invalid JSON format", stdres.getMessage());
    }

    /**
     * Test updating project with valid id and proper arguments
     */
    @Test
    @DisplayName("PUT /api/projects/%id: valid id with proper arguments")
    void putProject_ValidIDAndProperArgumentsGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        JsonObject jo = new JsonObject();
        jo.addProperty("title", "new title");
        jo.addProperty("description", "new description");
        jo.addProperty("status", "in progress");
        jo.addProperty("priority", 2);
        jo.addProperty("publicReq", false);
        jo.addProperty("deleted", true);
        jo.addProperty("commissioned", true);
        jo.addProperty("commissionAccepted", true);
        jo.addProperty("commissionNotes", "some notes");
        jo.addProperty("commissionCost", 4.6);
        jo.addProperty("commissionStart", "start date");
        jo.addProperty("commissionEnd", "end date");

        TestResponse res = TestResponse.request("PUT", "/api/projects/" + p.getId(), jo.toString());
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());

        JsonObject json = stdres.getData().getAsJsonObject();
        assertEquals("new title", json.get("title").getAsString());
        assertEquals("new description", json.get("description").getAsString());
        assertEquals("in progress", json.get("status").getAsString());
        assertEquals(2, json.get("priority").getAsInt());
        assertFalse(json.get("publicReq").getAsBoolean());
        assertTrue(json.get("deleted").getAsBoolean());
        assertTrue(json.get("commissioned").getAsBoolean());
        assertTrue(json.get("commissionAccepted").getAsBoolean());
        assertEquals("some notes", json.get("commissionNotes").getAsString());
        assertEquals(4.6, json.get("commissionCost").getAsDouble());
        assertEquals("start date", json.get("commissionStart").getAsString());
        assertEquals("end date", json.get("commissionEnd").getAsString());
    }

    /**
     * Test updating project with valid id
     */
    @Test
    @DisplayName("PUT /api/projects/%id: valid id only")
    void putProject_ValidIDGiven_ShouldReturnProject() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth2|0", true, false);

        TestResponse res = TestResponse.request("PUT", "/api/projects/" + p.getId(), null);
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());

        JsonObject json = stdres.getData().getAsJsonObject();
        assertEquals("test project", json.get("title").getAsString());
        assertEquals("test project description", json.get("description").getAsString());
        assertEquals("test author", json.get("author").getAsString());
        assertEquals("google-oauth2|0", json.get("authorId").getAsString());
        assertEquals("queued", json.get("status").getAsString());
        assertEquals(-1, json.get("priority").getAsInt());
        assertTrue(json.get("publicReq").getAsBoolean());
        assertFalse(json.get("deleted").getAsBoolean());
        assertFalse(json.get("commissioned").getAsBoolean());
        assertFalse(json.get("commissionAccepted").getAsBoolean());
        assertEquals("", json.get("commissionNotes").getAsString());
        assertEquals(0.0, json.get("commissionCost").getAsDouble());
        assertEquals("", json.get("commissionStart").getAsString());
        assertEquals("", json.get("commissionEnd").getAsString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        assertEquals(dateFormat.format(new Date()), json.get("addedDate").getAsString());
        assertEquals("", json.get("editedDate").getAsString());
    }

    /**
     * Test updating project with invalid id
     */
    @Test
    @DisplayName("PUT /api/projects/%id: invalid id")
    void putProject_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("PUT", "/api/projects/0", null);
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("project with id '0' does not exist", stdres.getMessage());
    }

    /**
     * Test updating project with valid id and invalid json
     */
    @Test
    @DisplayName("PUT /api/projects/%id: valid id & invalid json")
    void putProject_ValidIDAndInvalidJSON_ShouldReturnErrorMessage() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth2|0", true, false);

        TestResponse res = TestResponse.request("PUT", "/api/projects/" + p.getId(), "{");
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("invalid JSON format", stdres.getMessage());
    }

    /**
     * Test deleting project with valid id
     */
    @Test
    @DisplayName("DELETE /api/projects/%id: valid id")
    void deleteProject_ValidIDGiven_ShouldReturnSuccess() {
        Project p = Main.db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);

        TestResponse res = TestResponse.request("DELETE", "/api/projects/" + p.getId(), null);
        assertNotNull(res);
        assertEquals(200, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.SUCCESS, stdres.getStatus());
    }

    /**
     * Test deleting project with non-existent id
     */
    @Test
    @DisplayName("DELETE /api/projects/%id: non-existent id")
    void deleteProject_NonExistentIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("DELETE", "/api/projects/0", null);
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("project with id '0' does not exist", stdres.getMessage());
    }

    /**
     * Test deleting project with invalid id
     */
    @Test
    @DisplayName("DELETE /api/projects/%id: invalid id")
    void deleteProject_InvalidIDGiven_ShouldReturnErrorMessage() {
        TestResponse res = TestResponse.request("DELETE", "/api/projects/abc", null);
        assertNotNull(res);
        assertEquals(400, res.status);

        StandardResponse stdres = res.standardResponse();
        assertEquals(StatusResponse.ERROR, stdres.getStatus());
        assertEquals("project id 'abc' is invalid", stdres.getMessage());
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
