import com.google.gson.*;

import java.lang.reflect.Type;

public class ProjectDeserializer implements JsonDeserializer<Project> {
    @Override
    public Project deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jo = json.getAsJsonObject();

        if (jo.get("id") == null || jo.get("title") == null || jo.get("description") == null || jo.get("author") == null 
                || jo.get("authorId") == null || jo.get("status") == null || jo.get("priority") == null || jo.get("public") == null
                || jo.get("deleted") == null || jo.get("commissioned") == null || jo.get("commissionAccepted") == null 
                || jo.get("commissionNotes") == null || jo.get("commissionCost") == null || jo.get("commissionStart") == null 
                || jo.get("commissionEnd") == null || jo.get("addedDate") == null || jo.get("editedDate") == null) 
            throw new JsonParseException("Invalid project JSON");

        Project p = new Project();
        p.setId(jo.get("id").getAsInt());
        p.setTitle(jo.get("title").getAsString());
        p.setDescription(jo.get("description").getAsString());
        p.setAuthor(jo.get("author").getAsString());
        p.setAuthorId(jo.get("authorId").getAsString());
        p.setStatus(jo.get("status").getAsString());
        p.setPriority(jo.get("priority").getAsInt());
        p.setPublicReq(jo.get("public").getAsBoolean());
        p.setDeleted(jo.get("deleted").getAsBoolean());
        p.setCommissioned(jo.get("commissioned").getAsBoolean());
        p.setCommissionAccepted(jo.get("commissionAccepted").getAsBoolean());
        p.setCommissionNotes(jo.get("commissionNotes").getAsString());
        p.setCommissionCost(jo.get("commissionCost").getAsDouble());
        p.setCommissionStart(jo.get("commissionStart").getAsString());
        p.setCommissionEnd(jo.get("commissionEnd").getAsString());
        p.setAddedDate(jo.get("addedDate").getAsString());
        p.setEditedDate(jo.get("editedDate").getAsString());

        return p;
    }
}
