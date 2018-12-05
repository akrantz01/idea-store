/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class ResponseError {
    private String status;
    private String reason;
    private int code;

    /**
     * Create error message from formatted string
     * @param code status code
     * @param reason reason for failure
     * @param args formatting arguments
     */
    ResponseError(int code, String reason, String... args) {
        this.status = "error";
        this.code = code;
        this.reason = String.format(reason, args);
    }

    /**
     * Create error message from exception
     * @param code status code
     * @param e exception thrown
     */
    ResponseError(int code, Exception e) {
        this.status = "error";
        this.code = code;
        this.reason = e.getMessage();
    }

    /**
     * Get the reason for failure
     * @return reason for failure
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Get the status code
     * @return status code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Get the current status
     * @return current status
     */
    public String getStatus() {
        return this.status;
    }
}
