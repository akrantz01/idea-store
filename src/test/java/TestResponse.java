import com.google.gson.Gson;
import spark.utils.IOUtils;

import java.io.DataOutputStream;
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
     * Get standard response from body
     * @return standard response object
     */
    StandardResponse standardResponse() {
        return new Gson().fromJson(body, StandardResponse.class);
    }

    static TestResponse request(String method, String path, String data) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://localhost:" + System.getenv("PORT") + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            // Post body
            if (data != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.write(data.getBytes());
            }

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
