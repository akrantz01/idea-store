import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class DatabaseTest {
    private static Database db;

    /**
     * Test adding an idea with title and description
     */
    @Test
    void addIdea_TitleAndDescriptionGiven_ShouldReturnIdea() {
        Idea idea = db.addIdea("test idea", "test idea description");
        assertNotNull(idea);
    }

    /**
     * Test retrieving an idea with an existing id
     */
    @Test
    void getIdea_ExistingIDGiven_ShouldReturnIdea() {
        Idea origIdea = db.addIdea("test idea", "test idea description");
        Idea gotIdea = db.getIdea(origIdea.getId());

        assertEquals(origIdea, gotIdea);
    }

    /**
     * Test updating an idea with an existing id
     */
    @Test
    void updateIdea_ExistingIDGiven_ShouldReturnIdea() {
        Idea idea = db.addIdea("test idea", "test idea description");
        String title = idea.getTitle();
        String description = idea.getDescription();

        idea = db.updateIdea(idea.getId(), "title", null);
        assertNotEquals(title, idea.getTitle());
        title = idea.getTitle();

        idea = db.updateIdea(idea.getId(), null, "description");
        assertNotEquals(description, idea.getDescription());
        description = idea.getDescription();

        idea = db.updateIdea(idea.getId(), "new title", "new description");
        assertNotEquals(title, idea.getTitle());
        assertNotEquals(description, idea.getDescription());
    }

    /**
     * Test deleting an idea with existing id
     */
    @Test
    void deleteIdea_ExistingIDGiven_ShouldReturnTrue() {
        Idea origIdea = db.addIdea("test idea", "test idea description");
        assertTrue(db.deleteIdea(origIdea.getId()));
    }

    /**
     * Test getting a list of ideas with ideas in the database
     */
    @Test
    void listIdeas_PopulatedDatabaseGiven_ShouldReturnArrayOf5Ideas() {
        List<Idea> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) expected.add(db.addIdea("idea" + i, "idea description" + i));
        List<Idea> ideas = db.listIdeas();

        assertEquals(expected.size(), ideas.size());
        assertEquals(expected, ideas);
    }

    /**
     * Test finding an idea by title with 3 ideas at the specified title
     */
    @Test
    void findIdea_ExistingItemsAtTitle_ShouldReturnArrayOf3Ideas() {
        List<Idea> expected = new ArrayList<>();
        for (int i = 0; i < 3; i++) expected.add(db.addIdea("filter" + i, "idea description"));
        for (int i = 0; i < 2; i++) db.addIdea("idea" + i, "idea description");
        List<Idea> found = db.findIdea("filter");

        assertEquals(expected.size(), found.size());
        assertEquals(expected, found);
    }

    /**
     * Test inclusively filtering by time with a valid time range
     * @throws InterruptedException from sleeping
     */
    @Test
    void filterRange_ValidTimeRangeGiven_ArrayOf3Ideas() throws InterruptedException {
        List<Idea> expected = new ArrayList<>();
        Timestamp from = null;
        Timestamp to = null;

        for (int i = 0; i < 5; i++) {
            Idea idea = db.addIdea("test idea", "test idea description");

            if (i < 3) {
                expected.add(idea);

                if (i == 0) from = idea.getCreated();
                if (i == 2) to = idea.getCreated();
            }
            TimeUnit.SECONDS.sleep(1);
        }
        List<Idea> found = db.filterRange(from, to, true);

        assertEquals(expected.size(), found.size());
        assertEquals(expected, found);

        expected.remove(0);
        expected.remove(1);

        found = db.filterRange(from, to, false);

        assertEquals(expected.size(), found.size());
        assertEquals(expected, found);
    }

    /**
     * Test exclusively filtering by time with a valid time range
     * @throws InterruptedException from sleeping
     */
    @Test
    void filterRange_ValidTimeRangeGiven_ArrayOf1Idea() throws InterruptedException {
        List<Idea> expected = new ArrayList<>();
        Timestamp from = null;
        Timestamp to = null;

        for (int i = 0; i < 5; i++) {
            Idea idea = db.addIdea("test idea", "test idea description");

            if (i == 0) from = idea.getCreated();
            if (i == 1) expected.add(idea);
            if (i == 2) to = idea.getCreated();

            TimeUnit.SECONDS.sleep(1);
        }
        List<Idea> found = db.filterRange(from, to, false);

        assertEquals(expected.size(), found.size());
        assertEquals(expected, found);
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
            session.createQuery("DELETE FROM Idea").executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Display all fields of an idea
     * @param idea idea to display
     */
    void describeIdeas(Idea idea) {
        System.out.println(idea + ":");
        System.out.println("\tID: " + idea.getId());
        System.out.println("\tCreated: " + idea.getCreated());
        System.out.println("\tTitle: " + idea.getTitle());
        System.out.println("\tDescription: " + idea.getDescription());
    }

    /**
     * Display all fields of a list of ideas
     * @param ideas list of ideas to display
     */
    void describeIdeas(List<Idea> ideas) {
        for (Idea idea: ideas) {
            System.out.println(idea + ":");
            System.out.println("\tID: " + idea.getId());
            System.out.println("\tCreated: " + idea.getCreated());
            System.out.println("\tTitle: " + idea.getTitle());
            System.out.println("\tDescription: " + idea.getDescription());
        }
    }
}
