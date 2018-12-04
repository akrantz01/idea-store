import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class IdeaStoreDAO {
    private Sql2o sql2o;

    public IdeaStoreDAO() {
        this.sql2o = new Sql2o(System.getenv("CONNECTION_STRING"), System.getenv("CONNECTION_USERNAME"), System.getenv("CONNECTION_PASSWORD"));
    }

    public void createIdea(String title, String descr, Double created) {
        String sql = "INSERT INTO ideas(title, description, created) VALUES (:title, :description, :created)";

        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("title", title)
                    .addParameter("description", descr)
                    .addParameter("created", created)
                    .executeUpdate();
        }
    }

    public List<Idea> getAllIdeas() {
        String sql = "SELECT id, title, description, created FROM ideas";

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeAndFetch(Idea.class);
        }
    }

    public List<Idea> getIdeasBetween(Double fromDate, Double toDate) {
        String sql = "SELECT id, title, description, created FROM ideas WHERE expires >= :fromDate AND expires < :toDate";

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("fromDate", fromDate)
                    .addParameter("toDate", toDate)
                    .executeAndFetch(Idea.class);
        }
    }
}
