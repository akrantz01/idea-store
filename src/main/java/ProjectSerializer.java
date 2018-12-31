import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ProjectSerializer implements JsonSerializer<Project> {
    @Override
    public JsonElement serialize(Project project, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        object.addProperty("id", project.getId());
        object.addProperty("title", project.getTitle());
        object.addProperty("description", project.getDescription());
        object.addProperty("author", project.getAuthor());
        object.addProperty("authorId", project.getAuthorId());
        object.addProperty("status", project.getStatus());
        object.addProperty("priority", project.getPriority());
        object.addProperty("public", project.getPublicReq());
        object.addProperty("deleted", project.getDeleted());
        object.addProperty("commissioned", project.getCommissioned());
        object.addProperty("commissionAccepted", project.getCommissionAccepted());
        object.addProperty("commissionNotes", project.getCommissionNotes());
        object.addProperty("commissionCost", project.getCommissionCost());
        object.addProperty("commissionStart", project.getCommissionStart());
        object.addProperty("commissionEnd", project.getCommissionEnd());
        object.addProperty("addedDate", project.getAddedDate());
        object.addProperty("editedDate", project.getEditedDate());

        return object;
    }
}
