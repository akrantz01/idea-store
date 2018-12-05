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

class DatabaseTests {
    private static Database db;

    @Test
    void testAddIdea() {
        Idea idea = db.addIdea("test idea", "test idea description");
        assertNotNull(idea);
    }

    @Test
    void testGetIdea() {
        Idea origIdea = db.addIdea("test idea", "test idea description");
        Idea gotIdea = db.getIdea(origIdea.getId());

        assertEquals(origIdea, gotIdea);
    }

    @Test
    void testUpdateIdea() {
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

    @Test
    void testDeleteIdea() {
        Idea origIdea = db.addIdea("test idea", "test idea description");
        db.deleteIdea(origIdea.getId());

        Idea gotIdea = db.getIdea(origIdea.getId());
        assertNull(gotIdea);
    }

    @Test
    void testListIdeas() {
        List<Idea> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) expected.add(db.addIdea("idea" + i, "idea description" + i));
        List<Idea> ideas = db.listIdeas();

        assertEquals(expected.size(), ideas.size());
        assertEquals(expected, ideas);
    }

    @Test
    void testFindIdea() {
        List<Idea> expected = new ArrayList<>();
        for (int i = 0; i < 3; i++) expected.add(db.addIdea("filter" + i, "idea description"));
        for (int i = 0; i < 2; i++) db.addIdea("idea" + i, "idea description");
        List<Idea> found = db.findIdea("filter");

        assertEquals(expected.size(), found.size());
        assertEquals(expected, found);
    }

    @Test
    void testFilterRangeInclusive() throws InterruptedException {
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

    @Test
    void testFilterRangeExclusive() throws InterruptedException {
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

    @BeforeAll
    static void setUp() {
        db = new Database();
    }

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

    void describeIdeas(Idea idea) {
        System.out.println(idea + ":");
        System.out.println("\tID: " + idea.getId());
        System.out.println("\tCreated: " + idea.getCreated());
        System.out.println("\tTitle: " + idea.getTitle());
        System.out.println("\tDescription: " + idea.getDescription());
    }

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
