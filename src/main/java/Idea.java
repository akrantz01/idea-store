import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
@Entity
@Table(name="ideas")
public class Idea {
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="created")
    private Timestamp created;

    /**
     * Default constructor
     */
    public Idea() {}

    /**
     * Initialize all of the non-automatic variables
     * @param t idea title
     * @param d idea description
     * @param c time the idea was created
     */
    public Idea(String t, String d, Timestamp c) {
        this.title = t;
        this.description = d;
        this.created = c;
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
     * Get the time the idea was created
     * @return time the idea was created
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Set the time the idea was created
     * @param c new creation time
     */
    public void setCreated(Timestamp c) {
        this.created = c;
    }

    /**
     * Check whether the idea object equals another object
     * @param a the other object
     * @return whether the objects are equal
     */
    @Override
    public boolean equals(Object a) {
        if (a instanceof Idea) {
            Idea i = (Idea) a;
            return i.getId() == this.id && i.getTitle().equals(this.title) &&
                    i.getDescription().equals(this.description) &&
                    i.getCreated().equals(this.created);
        }
        return false;
    }
}
