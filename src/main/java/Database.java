import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private static SessionFactory factory;

    public Database() {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

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

    Idea addIdea(String title, String description) {
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
