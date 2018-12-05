import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

class IdeaApi {
    static Route getIdeas = (Request request, Response response) -> Main.db.listIdeas();

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

    static Route createIdea = (Request request, Response response) -> Main.db.addIdea(request.queryParams("title"), request.queryParams("description"));

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
}
