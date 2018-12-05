public class ResponseSuccess {
    private String status;
    private int code;

    ResponseSuccess() {
        this.status = "success";
        this.code = 200;
    }

    public String getStatus() {
        return this.status;
    }

    public int getCode() {
        return this.code;
    }
}
