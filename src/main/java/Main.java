import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    static Database db;

    public static void main(String[] args) {
        // Initialize database
        db = new Database(System.getenv("CONNECTION_URL"), System.getenv("CONNECTION_USERNAME"), System.getenv("CONNECTION_PASSWORD"));

        // Configure Spark
        port(Integer.parseInt(System.getenv("PORT")));
        staticFiles.location("/public");
        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(HttpStatus.BAD_REQUEST_400);
            response.body(new JsonTransformer().render(new ResponseError(HttpStatus.BAD_REQUEST_400, e)));
        });

        // Setup routes
        get("/", (request, response) -> "Hello World!");

        path("/api", () -> {
            get("/ideas", IdeaApi.getIdeas, json());
            post("/ideas", IdeaApi.createIdea, json());

            path("/ideas", () -> {
                get("/", IdeaApi.getIdeas, json());
                post("/", IdeaApi.createIdea, json());
                get("/:id", IdeaApi.getIdea, json());
                put("/:id", IdeaApi.updateIdea, json());
                delete("/:id", IdeaApi.deleteIdea, json());
            });
        });

        get("*", (request, response) -> {
            response.status(HttpStatus.NOT_FOUND_404);
            return "Page does not exist";
        });
    }

    public static Map<String, String> newError(String reason) {
        HashMap<String, String> err = new HashMap<>();
        err.put("status", "error");
        err.put("reason", reason);
        return err;
    }

    public static JsonTransformer json() {
        return new JsonTransformer();
    }
}
