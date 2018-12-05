import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Initialize database
        Database db = new Database(System.getenv("CONNECTION_URL"), System.getenv("CONNECTION_USERNAME"), System.getenv("CONNECTION_PASSWORD"));

        // Configure Spark
        port(Integer.parseInt(System.getenv("PORT")));
        staticFiles.location("/public");

        // Setup routes
        get("/", (request, response) -> "Hello World!");
        get("/api/*", (request, response) -> {
            response.status(HttpStatus.NOT_FOUND_404);
            return toJSON(newError("route does not exist"));
        });
        get("*", (request, response) -> {
            response.status(HttpStatus.NOT_FOUND_404);
            return "Page does not exist";
        });
    }

    public static String toJSON(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException while mapping object (" + data + ") to JSON");
        }
    }

    public static Map<String, String> newError(String reason) {
        HashMap<String, String> err = new HashMap<>();
        err.put("status", "error");
        err.put("reason", reason);
        return err;
    }
}
