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
        staticFileLocation("/public");
        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(HttpStatus.BAD_REQUEST_400);
            response.body(new JsonTransformer().render(new ResponseError(HttpStatus.BAD_REQUEST_400, e)));
        });

        // Setup routes
        get("/", (request, response) -> {
            response.redirect("index.html");
            return null;
        });

        path("/api", () -> {
            get("/projects", ProjectApi.getProjects, json());
            post("/projects", ProjectApi.createProject, json());

            path("/projects", () -> {
                get("/", ProjectApi.getProjects, json());
                post("/", ProjectApi.createProject, json());
                get("/:id", ProjectApi.getProject, json());
                put("/:id", ProjectApi.updateProject, json());
                delete("/:id", ProjectApi.deleteProject, json());
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
