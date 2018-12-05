import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class Database {
    private static SessionFactory factory;

    /**
     * Create the default database object
     */
    public Database() {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Create a Database object while overriding configuration file parameters
     * @param url connection url for database
     * @param username database username
     * @param password database password
     */
    public Database(String url, String username, String password) {
        Properties p = new Properties();
        if (url != null) if (!url.equals("")) p.setProperty("hibernate.connection.url", url);
        if (username != null) if (!username.equals("")) p.setProperty("hibernate.connection.username", username);
        if (password != null) if (!password.equals("")) p.setProperty("hibernate.connection.password", password);

        try {
            factory = new Configuration().configure()
                    .setProperties(p)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Add an idea object to the database
     * @param title title of the idea
     * @param description description of the idea
     * @return the created idea object
     */
    Idea addIdea(String title, String description) {
        if (title == null || description == null) return null;

        Session session = factory.openSession();
        Transaction tx = null;
        Idea idea = null;

        try {
            tx = session.beginTransaction();
            idea = new Idea(title, description, new Timestamp((System.currentTimeMillis() / 1000L) * 1000));
            session.save(idea);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return idea;
    }

    /**
     * Get an idea object by id
     * @param id id number of the idea
     * @return idea object at the specified id
     */
    Idea getIdea(Integer id) {
        Session session = factory.openSession();
        Transaction tx = null;
        Idea idea = null;

        try {
            tx = session.beginTransaction();
            idea = session.get(Idea.class, id);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return idea;
    }

    /**
     * Update an idea object's title and/or description
     * @param id id number of the idea
     * @param title new title of the idea
     * @param description new description of the idea
     * @return updated idea object at the id
     */
    Idea updateIdea(Integer id, String title, String description) {
        Session session = factory.openSession();
        Transaction tx = null;
        Idea idea = null;

        try {
            tx = session.beginTransaction();
            idea = session.get(Idea.class, id);
            if (idea == null) return null;

            if (title != null) idea.setTitle(title);
            if (description != null) idea.setDescription(description);
            session.update(idea);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return idea;
    }

    /**
     * Delete an idea object
     * @param id id number of the idea
     * @return boolean for success or not
     */
    boolean deleteIdea(Integer id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Idea idea = session.get(Idea.class, id);
            if (idea == null) return false;

            session.delete(idea);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return true;
    }

    /**
     * List all of the ideas in the database
     * @return array of the idea objects
     */
    List<Idea> listIdeas() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Idea> ideas = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            List is = session.createQuery("FROM Idea").list();
            for (Object i: is) ideas.add((Idea)i);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return ideas;
    }

    /**
     * Find an idea by title
     * @param title title of the object to find
     * @return array of possibly matching ideas
     */
    List<Idea> findIdea(String title) {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Idea> ideas = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            List is = session.createQuery("FROM Idea I WHERE I.title LIKE :title")
                    .setParameter("title", title+"%")
                    .list();
            for (Object i: is) ideas.add((Idea)i);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return ideas;
    }

    /**
     * Find all ideas within a certain timespan
     * @param from starting time bound
     * @param to ending time bound
     * @param inclusive whether the search should include to and from
     * @return array of ideas within the timespan
     */
    List<Idea> filterRange(Timestamp from, Timestamp to, boolean inclusive) {
        if (from == null) from = new Timestamp(0);
        if (to == null) to = new Timestamp(1000 * ((System.currentTimeMillis() / 1000L) + (60 * 60 * 24 * 365)));

        Session session = factory.openSession();
        Transaction tx = null;
        List<Idea> ideas = new ArrayList<>();

        String query;
        if (inclusive) query = "FROM Idea I WHERE I.created >= :f AND I.created <= :t";
        else query = "FROM Idea I WHERE I.created > :f AND I.created < :t";

        try {
            tx = session.beginTransaction();
            List is = session.createQuery(query)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .list();
            for (Object i: is) ideas.add((Idea)i);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return ideas;
    }
}
