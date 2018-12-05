import javax.persistence.*;
import java.sql.Timestamp;

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

    public Idea() {}
    public Idea(String t, String d, Timestamp c) {
        this.title = t;
        this.description = d;
        this.created = c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp c) {
        this.created = c;
    }

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
