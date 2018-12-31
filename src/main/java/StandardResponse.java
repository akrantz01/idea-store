import com.google.gson.JsonElement;

public class StandardResponse {
    private StatusResponse status;
    private String message;
    private JsonElement data;

    StandardResponse(StatusResponse status) {
        this.status = status;
    }

    StandardResponse(StatusResponse status, String message) {
        this.status = status;
        this.message = message;
    }

    StandardResponse(StatusResponse status, JsonElement data) {
        this.status = status;
        this.data = data;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    String getMessage() {
        return message;
    }

    JsonElement getData() {
        return data;
    }
}
