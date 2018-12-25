import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
@Entity
@Table(name="projects")
public class Project {
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="author")
    private String author;

    @Column(name="authorId")
    private String authorId;

    @Column(name="status")
    private String status;

    @Column(name="priority")
    private int priority;

    @Column(name="public")
    private boolean publicReq;

    @Column(name="deleted")
    private boolean deleted;

    @Column(name="commissioned")
    private boolean commissioned;

    @Column(name="commission_accepted")
    private boolean commissionAccepted;

    @Column(name="commission_notes")
    private String commissionNotes;

    @Column(name="commission_cost")
    private double commissionCost;

    @Column(name="commission_start")
    private String commissionStart;

    @Column(name="commission_end")
    private String commissionEnd;

    @Column(name="added_date")
    private String addedDate;

    @Column(name="edited_date")
    private String editedDate;

    /**
     * Default constructor
     */
    public Project() {}

    /**
     * Initialize all of the variables w/o defaults
     * @param title idea title
     * @param description idea description
     * @param author author's name
     * @param authorId author's id
     * @param publicReq whether the project is public or not
     * @param commissioned whether the project is a commission or not
     */
    public Project(String title, String description, String author,
                   String authorId, Boolean publicReq, Boolean commissioned) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorId = authorId;
        this.status = "queued";
        this.priority = -1;
        this.publicReq = publicReq;
        this.deleted = false;
        this.commissioned = commissioned;
        this.commissionAccepted = false;
        this.commissionNotes = "";
        this.commissionStart = "";
        this.commissionEnd = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        this.addedDate = dateFormat.format(new Date());
        this.editedDate = "";
    }

    /**
     * Get the idea id
     * @return id of the idea
     */
    public int getId() {
        return id;
    }

    /**
     * Set the idea's id
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the title of the idea
     * @return title of the idea
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the idea
     * @param t new title
     */
    public void setTitle(String t) {
        this.title = t;
    }

    /**
     * Get the description of the idea
     * @return description of the idea
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the idea
     * @param d new description
     */
    public void setDescription(String d) {
        this.description = d;
    }

    /**
     * Get the author's name
     * @return author's name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author's name
     * @param a new name
     */
    public void setAuthor(String a) {
        this.author = a;
    }

    /**
     * Get the author's Google ID
     * @return format: google-oauth2|<number>
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Set the author's Google ID
     * @param a format: google-oauth2|<number>
     */
    public void setAuthorId(String a) {
        this.authorId = a;
    }

    /**
     * Get the status of the request
     * @return (ignored, queued, working, completed)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status of the request
     * @param s (ignored, queued, working, completed)
     */
    public void setStatus(String s) {
        this.status = s;
    }

    /**
     * Get the priority of the request
     * @return -1 to 3 (lowest to highest)
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set the priority of the request
     * @param p -1 to 3 (lowest to highest)
     */
    public void setPriority(int p) {
        this.priority = p;
    }

    /**
     * Get the visibility of the request
     * @return true: public, false: private
     */
    public boolean getPublicReq() {
        return publicReq;
    }

    /**
     * Set the visibility of the request
     * @param p true: public, false: private
     */
    public void setPublicReq(boolean p) {
        this.publicReq = p;
    }

    /**
     * Get whether the request is deleted
     * @return true: deleted, false: not
     */
    public boolean getDeleted() {
        return deleted;
    }

    /**
     * Set the the deletion status
     * @param d true: deleted, false: not
     */
    public void setDeleted(boolean d) {
        this.deleted = d;
    }

    /**
     * Get whether the project is a commission
     * @return true: commission, false: request
     */
    public boolean getCommissioned() {
        return commissioned;
    }

    /**
     * Set the commissioned status
     * @param c true: commission, false: request
     */
    public void setCommissioned(boolean c) {
        this.commissioned = c;
    }

    /**
     * Get whether the commission is accepted
     * @return true: yes, false: no
     */
    public boolean getCommissionAccepted() {
        return commissionAccepted;
    }

    /**
     * Set whether the commission is accepted
     * @param a true: yes, false: no
     */
    public void setCommissionAccepted(boolean a) {
        this.commissionAccepted = a;
    }

    /**
     * Get the notes about the commission
     * @return commission notes
     */
    public String getCommissionNotes() {
        return commissionNotes;
    }

    /**
     * Set the notes about the commission
     * @param n new commission notes
     */
    public void setCommissionNotes(String n) {
        this.commissionNotes = n;
    }

    /**
     * Get the cost to make the commission
     * @return cost to make
     */
    public double getCommissionCost() {
        return commissionCost;
    }

    /**
     * Set the cost to make the commission
     * @param c new cost
     */
    public void setCommissionCost(double c) {
        this.commissionCost = c;
    }

    /**
     * Get the start date of the commission
     * @return start date
     */
    public String getCommissionStart() {
        return commissionStart;
    }

    /**
     * Set the start date of the commission
     * @param s new start date
     */
    public void setCommissionStart(String s) {
        this.commissionStart = s;
    }

    /**
     * Get the end date of the commission
     * @return end date
     */
    public String getCommissionEnd() {
        return commissionEnd;
    }

    /**
     * Set the end date of the commission
     * @param e new end date
     */
    public void setCommissionEnd(String e) {
        this.commissionEnd = e;
    }

    /**
     * Get the time the idea was created
     * @return time the idea was created
     */
    public String getAddedDate() {
        return addedDate;
    }

    /**
     * Set the time the idea was created
     * @param a new creation time
     */
    public void setAddedDate(String a) {
        this.addedDate = a;
    }

    /**
     * Get the most recent date the request was edited
     * @return date edited
     */
    public String getEditedDate() {
        return editedDate;
    }

    /**
     * Set the new edited date
     * @param e edited date
     */
    public void setEditedDate(String e) {
        this.editedDate = e;
    }

    /**
     * Check whether the idea object equals another object
     * @param a the other object
     * @return whether the objects are equal
     */
    @Override
    public boolean equals(Object a) {
        if (a instanceof Project) {
            Project i = (Project) a;
            // This should be good enough because database
            // Maybe ;)
            return i.getId() == this.id;
        }
        return false;
    }
}
