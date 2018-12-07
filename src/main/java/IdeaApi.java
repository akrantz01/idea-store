import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Timestamp;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class IdeaApi {
    /**
     * Get request for listing all ideas
     */
    static Route getIdeas = (Request request, Response response) -> Main.db.listIdeas();

    /**
     * Get request for a specific Idea
     */
    static Route getIdea = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", request.params("id"));
        }

        Idea idea = Main.db.getIdea(id);
        if (idea != null) return idea;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", id.toString());
    };

    /**
     * Post request for creating an idea
     */
    static Route createIdea = (Request request, Response response) -> {
        Idea idea = Main.db.addIdea(request.queryParams("title"), request.queryParams("description"));
        if (idea != null) return idea;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "expected query parameters 'title' and 'description', got title='%s' and description='%s'", request.queryParams("title"), request.queryParams("description"));
    };

    /**
     * Put request for updating an idea
     */
    static Route updateIdea = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", request.params("id"));
        }

        Idea idea = Main.db.updateIdea(id, request.queryParams("title"), request.queryParams("description"));
        if (idea != null) return idea;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", id.toString());
    };

    /**
     * Delete request for deleting an idea
     */
    static Route deleteIdea = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", request.params("id"));
        }
        if (Main.db.deleteIdea(id)) return new ResponseSuccess();

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "idea with id '%s' does not exist", id.toString());
    };

    /**
     * Get request for ideas filtering by title
     */
    static Route getFindIdea = (Request request, Response response) -> Main.db.findIdea(request.queryParams("title"));

    /**
     * Get request for ideas filtering by timestamp
     */
    static Route getFilterIdea = (Request request, Response response) -> {
        boolean i = Boolean.parseBoolean(request.queryParams("inclusive"));
        Timestamp f = null;
        Timestamp t = null;
        try {
            t = new Timestamp(Long.parseLong(request.queryParams("to")));
        } catch (NumberFormatException ignored) {}

        try {
            f = new Timestamp(Long.parseLong(request.queryParams("from")));
        } catch (NumberFormatException ignored) {}

        return Main.db.filterRange(f, t, i);
    };
}
