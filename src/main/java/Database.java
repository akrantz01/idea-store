import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
     * Add an project object to the database by single parameters (for testing only)
     * @param title title of the project
     * @param description description of the project
     * @param author who wrote the project
     * @param authorId id of the account
     * @param publicReq whether the request is public/private
     * @param commissioned whether the request is a commission
     * @return the created project object
     */
    Project addProject(String title, String description, String author, String authorId, Boolean publicReq, Boolean commissioned) {
        if (title == null || description == null || author == null ||
                authorId == null || publicReq == null || commissioned == null) return null;

        Session session = factory.openSession();
        Transaction tx = null;
        Project project = null;

        try {
            tx = session.beginTransaction();
            project = new Project(title, description, author, authorId, publicReq, commissioned);
            session.save(project);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return project;
    }

    /**
     * Add a project to the database
     * @param project project object
     * @return the project info from the database
     */
    Project addProject(Project project) {
        if (project == null) return null;

        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(project);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return project;
    }

    /**
     * Get an project object by id
     * @param id id number of the project
     * @return project object at the specified id
     */
    Project getProject(Integer id) {
        Session session = factory.openSession();
        Transaction tx = null;
        Project project = null;

        try {
            tx = session.beginTransaction();
            project = session.get(Project.class, id);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return project;
    }

    /**
     * Update an project object's title and/or description
     * @param id id number of the project
     * @param title new title of the project
     * @param description new description of the project
     * @param status new status (ignored, queued, working, completed)
     * @param priority new priority (-1 to 3)
     * @param publicReq new public status (true: public)
     * @param deleted new deleted status (true: deleted)
     * @param commissioned new commissioned status (true: commissioned)
     * @param c_accepted new accepted status for commission (true: accepted)
     * @param c_notes new notes for commission
     * @param c_cost new cost for commission
     * @param c_start new start date for commission
     * @param c_end new end date for commission
     * @return updated project object at the id
     */
    Project updateProject(Integer id, String title, String description,
                          String status, Integer priority, Boolean publicReq,
                          Boolean deleted, Boolean commissioned, Boolean c_accepted,
                          String c_notes, Double c_cost, String c_start, String c_end) {
        Session session = factory.openSession();
        Transaction tx = null;
        Project project = null;

        try {
            tx = session.beginTransaction();
            project = session.get(Project.class, id);
            if (project == null) return null;

            if (title != null) project.setTitle(title);
            if (description != null) project.setDescription(description);
            if (status != null) project.setStatus(status);
            if (priority != null) project.setPriority(priority);
            if (publicReq != null) project.setPublicReq(publicReq);
            if (deleted != null) project.setDeleted(deleted);
            if (commissioned != null) project.setCommissioned(commissioned);
            if (c_accepted != null) project.setCommissionAccepted(c_accepted);
            if (c_notes != null) project.setCommissionNotes(c_notes);
            if (c_cost != null) project.setCommissionCost(c_cost);
            if (c_start != null) project.setCommissionStart(c_start);
            if (c_end != null) project.setCommissionEnd(c_end);

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY");
            project.setEditedDate(df.format(new Date()));

            session.update(project);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return project;
    }

    /**
     * Delete an idea object
     * @param id id number of the idea
     * @return boolean for success or not
     */
    boolean deleteProject(Integer id) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Project project = session.get(Project.class, id);
            if (project == null) return false;

            session.delete(project);
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
    List<Project> listProjects() {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Project> projects = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            List is = session.createQuery("FROM Project").list();
            for (Object i: is) projects.add((Project)i);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return projects;
    }

    /**
     * Check if a user exists
     * @return boolean (true: exists, false: not)
     */
    boolean projectExists(Integer id) {
        Session session = factory.openSession();
        Transaction tx = null;
        boolean exists = false;

        try {
            tx = session.beginTransaction();
            Project p = session.get(Project.class, id);
            if (p != null) exists = true;
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return exists;
    }
}
