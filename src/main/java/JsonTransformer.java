import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class JsonTransformer implements ResponseTransformer {
    private Gson gson = new Gson();

    /**
     * Convert an object into json
     * @param model object to convert
     * @return json string
     */
    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
