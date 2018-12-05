public class ResponseError {
    private String status;
    private String reason;
    private int code;

    ResponseError(int code, String reason, String... args) {
        this.status = "error";
        this.code = code;
        this.reason = String.format(reason, args);
    }

    ResponseError(int code, Exception e) {
        this.status = "error";
        this.code = code;
        this.reason = e.getMessage();
    }

    public String getReason() {
        return this.reason;
    }

    public int getCode() {
        return this.code;
    }

    public String getStatus() {
        return this.status;
    }
}
