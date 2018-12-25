import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
class ProjectApi {
    /**
     * Get request for listing all projects
     */
    static Route getProjects = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        return Main.db.listProjects();
    };

    /**
     * Get request for a specific Project
     */
    static Route getProject = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", request.params("id"));
        }

        Project project = Main.db.getProject(id);
        if (project != null) return project;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", id.toString());
    };

    /**
     * Post request for creating an project
     */
    static Route createProject = (Request request, Response response) -> {
        Project project = Main.db.addProject(request.queryParams("title"), request.queryParams("description"),
                request.queryParams("author"), request.queryParams("authorId"), Boolean.valueOf(request.queryParams("public")),
                Boolean.valueOf(request.queryParams("commissioned")));
        if (project != null) return project;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "expected query parameters 'title' and 'description', got title='%s' and description='%s'", request.queryParams("title"), request.queryParams("description"));
    };

    /**
     * Put request for updating an project
     */
    static Route updateProject = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", request.params("id"));
        }

        Project project = Main.db.updateProject(id, request.queryParams("title"), request.queryParams("description"),
                request.queryParams("status"), Integer.valueOf(request.queryParams("priority")), 
                Boolean.valueOf(request.queryParams("public")), Boolean.valueOf(request.queryParams("deleted")),
                Boolean.valueOf(request.queryParams("commissioned")), Boolean.valueOf(request.queryParams("commissionAccepted")),
                request.queryParams("commissionNotes"), Double.parseDouble(request.queryParams("commissionCost")),
                request.queryParams("commissionStart"), request.queryParams("commissionEnd"));
        if (project != null) return project;

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", id.toString());
    };

    /**
     * Delete request for deleting an project
     */
    static Route deleteProject = (Request request, Response response) -> {
        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", request.params("id"));
        }
        if (Main.db.deleteProject(id)) return new ResponseSuccess();

        response.status(HttpStatus.BAD_REQUEST_400);
        return new ResponseError(HttpStatus.BAD_REQUEST_400, "project with id '%s' does not exist", id.toString());
    };
}
