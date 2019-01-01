import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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
        response.type("application/json");

        JsonElement element = Main.gson.toJsonTree(Main.db.listProjects(), new TypeToken<List<Project>>(){}.getType());

        if (!element.isJsonArray()) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "could not convert to json array"));
        }

        return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS, element.getAsJsonArray()));
    };

    /**
     * Get request for a specific Project
     */
    static Route getProject = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        response.type("application/json");

        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project id '%s' is invalid", request.params("id"))));
        }

        Project project = Main.db.getProject(id);
        if (project != null) return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS, project.toJSON()));

        response.status(HttpStatus.NOT_FOUND_404);
        return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project with id '%s' does not exist", id)));
    };

    /**
     * Post request for creating an project
     */
    static Route createProject = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        response.type("application/json");

        Project project;
        try {
            project = Main.gson.fromJson(request.body(), Project.class);
        } catch (JsonParseException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "invalid JSON format"));
        }


        project = Main.db.addProject(project);
        if (project != null) return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS, project.toJSON()));

        response.status(HttpStatus.BAD_REQUEST_400);
        return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "something went wrong, please contact the administrator"));
    };

    /**
     * Put request for updating an project
     */
    static Route updateProject = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        response.type("application/json");

        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project id '%s' is invalid", request.params("id"))));
        }
        Map<String, Object> json;
        try {
            Type map = new TypeToken<Map<String, Object>>() {
            }.getType();
            json = Main.gson.fromJson(request.body(), map);
            if (json == null) {
                Project project = Main.db.getProject(id);
                if (project == null) {
                    response.status(HttpStatus.NOT_FOUND_404);
                    return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project with id '%s' does not exist", id)));
                }
                return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS, project.toJSON()));
            }
        } catch (JsonParseException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "invalid JSON format"));
        }

        Boolean publicReq, deleted, commissioned, commissionAccepted;
        try {
            if (json.get("public") == null) publicReq = null;
            else publicReq = (Boolean) json.get("public");
            if (json.get("deleted") == null) deleted = null;
            else deleted = (Boolean) json.get("deleted");
            if (json.get("commissioned") == null) commissioned = null;
            else commissioned = (Boolean) json.get("commissioned");
            if (json.get("commissionAccepted") == null) commissionAccepted = null;
            else commissionAccepted = (Boolean) json.get("commissionAccepted");
        } catch (ClassCastException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "unable to parse boolean for " +
                    "'deleted', 'commissioned', or 'commissionAccepted'"));
        }

        Integer priority;
        try {
            if (json.get("priority") == null) priority = null;
            else priority = ((Double)json.get("priority")).intValue();
        } catch (ClassCastException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "unable to parse integer " +
                    "for 'priority'"));
        }

        Double commissionCost;
        try {
            if (json.get("commissionCost") == null) commissionCost = null;
            else commissionCost = (Double) json.get("commissionCost");
        } catch (ClassCastException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "unable to parse double for " +
                    "'commissionCost'"));
        }

        String title, description, status, commissionNotes, commissionStart, commissionEnd;
        try {
            if (json.get("title") == null) title = null;
            else title = (String) json.get("title");
            if (json.get("description") == null) description = null;
            else description = (String) json.get("description");
            if (json.get("status") == null) status = null;
            else status = (String) json.get("status");
            if (json.get("commissionNotes") == null) commissionNotes = null;
            else commissionNotes = (String) json.get("commissionNotes");
            if (json.get("commissionStart") == null) commissionStart = null;
            else commissionStart = (String) json.get("commissionStart");
            if (json.get("commissionEnd") == null) commissionEnd = null;
            else commissionEnd = (String) json.get("commissionEnd");
        } catch (ClassCastException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, "unable to parse string for 'title'," +
                    " 'description', 'status', 'commissionNotes', 'commissionStart', or 'commissionEnd'"));
        }

        Project project = Main.db.updateProject(id, title, description,
                status, priority, publicReq, deleted, commissioned,
                commissionAccepted, commissionNotes, commissionCost,
                commissionStart, commissionEnd);
        if (project != null) return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS, project.toJSON()));

        response.status(HttpStatus.NOT_FOUND_404);
        return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project with id '%s' does not exist", id)));
    };

    /**
     * Delete request for deleting an project
     */
    static Route deleteProject = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        response.type("application/json");

        Integer id;
        try {
            id = Integer.parseInt(request.params("id"));
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST_400);
            return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project id '%s' is invalid", request.params("id"))));
        }
        if (Main.db.deleteProject(id)) return Main.gson.toJson(new StandardResponse(StatusResponse.SUCCESS));

        response.status(HttpStatus.NOT_FOUND_404);
        return Main.gson.toJson(new StandardResponse(StatusResponse.ERROR, String.format("project with id '%s' does not exist", id)));
    };
}
