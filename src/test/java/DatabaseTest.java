import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class DatabaseTest {
    private static Database db;

    /**
     * Test adding an project with all arguments
     */
    @Test
    @DisplayName("addProject: all arguments")
    void addProject_ProperArgumentsGiven_ShouldReturnProject() {
        Project project = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);
        assertNotNull(project);
    }

    /**
     * Test adding an project with only title null
     */
    @Test
    @DisplayName("addProject: only title null")
    void addProject_OnlyTitleNull_ShouldReturnNull() {
        Project project = db.addProject(null, "test project description",
                "test author", "google-oauth|0", true, false);
        assertNull(project);
    }

    /**
     * Test adding an project with only description null
     */
    @Test
    @DisplayName("addProject: only description null")
    void addProject_OnlyDescriptionNull_ShouldReturnNull() {
        Project project = db.addProject("test project", null,
                "test author", "google-oauth|0", true, false);
        assertNull(project);
    }

    /**
     * Test adding an project with only author null
     */
    @Test
    @DisplayName("addProject: only author null")
    void addProject_OnlyAuthorNull_ShouldReturnNull() {
        Project project = db.addProject("test project", "test project description",
                null, "google-oauth|0", true, false);
        assertNull(project);
    }

    /**
     * Test adding an project with only authorId null
     */
    @Test
    @DisplayName("addProject: only authorId null")
    void addProject_OnlyAuthorIdNull_ShouldReturnNull() {
        Project project = db.addProject("test project", "test project description",
                "test author", null, true, false);
        assertNull(project);
    }

    /**
     * Test adding an project with only publicReq null
     */
    @Test
    @DisplayName("addProject: only publicReq null")
    void addProject_OnlyPublicReqNull_ShouldReturnNull() {
        Project project = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", null, false);
        assertNull(project);
    }

    /**
     * Test adding an project with only commissioned null
     */
    @Test
    @DisplayName("addProject: only commissioned null")
    void addProject_OnlyCommissionedNull_ShouldReturnNull() {
        Project project = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, null);
        assertNull(project);
    }

    /**
     * Test adding an project with all arguments null
     */
    @Test
    @DisplayName("addProject: all arguments null")
    void addProject_AllArgumentsNull_ShouldReturnNull() {
        Project project = db.addProject(null, null, null,
                null,null,null);
        assertNull(project);
    }

    /**
     * Test retrieving an project with an existing id
     */
    @Test
    @DisplayName("getProject: valid id")
    void getProject_ExistingIDGiven_ShouldReturnProject() {
        Project origProject = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);
        Project gotProject = db.getProject(origProject.getId());

        assertEquals(origProject, gotProject);
    }

    /**
     * Test retrieving an project with a non-existent id
     */
    @Test
    @DisplayName("getProject: invalid id")
    void getProject_NonExistentID_ShouldReturnNull() {
        Project project = db.getProject(0);
        assertNull(project);
    }

    /**
     * Test updating an project with an existing id
     */
    @Test
    @DisplayName("updateProject: valid id with title and description")
    void updateProject_ExistingIDGiven_ShouldReturnProject() {
        Project project = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);
        String title = project.getTitle();
        String description = project.getDescription();
        String status = project.getStatus();
        Integer priority = project.getPriority();
        Boolean publicReq = project.getPublicReq();
        Boolean deleted = project.getDeleted();
        Boolean commissioned = project.getCommissioned();
        Boolean c_accepted = project.getCommissionAccepted();
        String c_notes = project.getCommissionNotes();
        Double c_cost = project.getCommissionCost();
        String c_start = project.getCommissionStart();
        String c_end = project.getCommissionEnd();

        project = db.updateProject(project.getId(), "new title", "new description", "new status",
                0, false, true, true, true, "new notes", 8.2,
                "new start", "new end");

        assertNotEquals(title, project.getTitle());
        assertNotEquals(description, project.getDescription());
        assertNotEquals(status, project.getStatus());
        assertNotEquals(priority, project.getPriority());
        assertNotEquals(publicReq, project.getPublicReq());
        assertNotEquals(deleted, project.getDeleted());
        assertNotEquals(commissioned, project.getCommissioned());
        assertNotEquals(c_accepted, project.getCommissionAccepted());
        assertNotEquals(c_notes, project.getCommissionNotes());
        assertNotEquals(c_cost, project.getCommissionCost());
        assertNotEquals(c_start, project.getCommissionStart());
        assertNotEquals(c_end, project.getCommissionEnd());
    }

    /**
     * Test updating a project with a non-existent id
     */
    @Test
    @DisplayName("updateProject: invalid id only")
    void updateProject_NonExistentIDGiven_ShouldReturnNull() {
        Project project = db.updateProject(0, null, null, null,
                null, null, null, null, null,
                null, null, null, null);
        assertNull(project);
    }

    /**
     * Test updating an project with a non-existent id and proper arguments
     */
    @Test
    @DisplayName("updateProject: invalid id with proper arguments")
    void updateProject_NonExistentIDAndProperArgumentsGiven_ShouldReturnNull() {
        Project project = db.updateProject(0, "new title", "new description", "new status",
                0, false, true, true, true, "new notes", 8.2,
                "new start", "new end");
        assertNull(project);
    }

    /**
     * Test deleting an project with existing id
     */
    @Test
    @DisplayName("deleteProject: valid id")
    void deleteProject_ExistingIDGiven_ShouldReturnTrue() {
        Project origProject = db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false);
        assertTrue(db.deleteProject(origProject.getId()));
    }

    /**
     * Test deleting a project with invalid id
     */
    @Test
    @DisplayName("deleteProject: invalid id")
    void deleteProject_InvalidIDGiven_ShouldReturnFalse() {
        assertFalse(db.deleteProject(0));
    }

    /**
     * Test getting a list of projects with projects in the database
     */
    @Test
    @DisplayName("listProjects: items in database")
    void listProjects_PopulatedDatabaseGiven_ShouldReturnArrayOf5Projects() {
        List<Project> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) expected.add(db.addProject("test project", "test project description",
                "test author", "google-oauth|0", true, false));
        List<Project> projects = db.listProjects();

        assertEquals(expected.size(), projects.size());
        assertEquals(expected, projects);
    }

    /**
     * Test getting a list of projects with no projects in the database
     */
    @Test
    @DisplayName("listProjects: empty database")
    void listProjects_EmptyDatabaseGiven_ShouldReturnArrayOf0Projects() {
        List<Project> expected = new ArrayList<>();
        List<Project> projects = db.listProjects();

        assertEquals(expected.size(), projects.size());
        assertEquals(expected, projects);
    }

    /**
     * Create the database object on setup
     */
    @BeforeAll
    static void setUp() {
        db = new Database();
    }

    /**
     * Purge the database after each test
     */
    @AfterEach
    void tearDown() {
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
