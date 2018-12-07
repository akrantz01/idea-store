import com.google.gson.Gson;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class TestResponse {
    private final String body;
    final int status;

    /**
     * Create test response from request
     * @param status HTTP status code
     * @param body returned body
     */
    private TestResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    /**
     * Convert the body to JSON object
     * @return json representation of the body
     */
    Map<String, String> json() {
        return new Gson().fromJson(body, HashMap.class);
    }

    /**
     * Convert the body to JSON array
     * @return json representation of the body
     */
    List<Map<String, String>> jsonArray() {
        return new Gson().fromJson(body, List.class);
    }

    static TestResponse request(String method, String path) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://localhost:" + System.getenv("PORT") + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.connect();
            String body = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            if (connection == null) return null;

            try {
                String body = IOUtils.toString(connection.getErrorStream());
                return new TestResponse(connection.getResponseCode(), body);
            } catch (IOException ex) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
