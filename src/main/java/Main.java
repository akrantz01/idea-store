import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.*;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class Main {
    static Database db;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Project.class, new ProjectDeserializer())
            .registerTypeAdapter(Project.class, new ProjectSerializer())
            .create();

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
            response.body(Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, e.getMessage())));
        });

        // Setup routes
        get("/", (request, response) -> {
            response.redirect("index.html");
            return null;
        });

        path("/api", () -> {
            get("/projects", ProjectApi.getProjects);
            post("/projects", ProjectApi.createProject);
            options("/projects", ((request, response) -> {
                response.header("Allow", "GET, POST, OPTIONS");
                response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Headers", "Content-Type");
                response.status(HttpStatus.OK_200);
                return "";
            }));

            path("/projects", () -> {
                get("/:id", ProjectApi.getProject);
                put("/:id", ProjectApi.updateProject);
                delete("/:id", ProjectApi.deleteProject);
                options("/:id", ((request, response) -> {
                    response.header("Allow", "GET, PUT, DELETE, OPTIONS");
                    response.header("Access-Control-Allow-Methods", "GET, PUT, DELETE, OPTIONS");
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Headers", "Content-Type");
                    response.status(HttpStatus.OK_200);
                    return "";
                }));
            });
        });

        // Redirect to SPA
        get("*", (request, response) -> {
            response.redirect("index.html");
            return null;
        });

        options("*", ((request, response) -> {
            response.header("Allow", "GET, OPTIONS");
            response.header("Access-Control-Allow-Methods", "GET, OPTIONS");
            response.header("Access-Control-Allow-Origin", "*");
            response.status(HttpStatus.OK_200);
            return "";
        }));
    }

    /**
     * Simplified conversion to json
     * @return json converter
     */
    private static JsonTransformer json() {
        return new JsonTransformer();
    }
}
