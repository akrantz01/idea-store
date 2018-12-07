import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.*;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class Main {
    static Database db;

    /**
     * Main method to run
     * @param args any arguments (not used)
     */
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
                get("/find", IdeaApi.getFindIdea, json());
                get("/filter", IdeaApi.getFilterIdea, json());
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

    /**
     * Simplified conversion to json
     * @return json converter
     */
    private static JsonTransformer json() {
        return new JsonTransformer();
    }
}
