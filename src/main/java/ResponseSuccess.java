/**
 * @author Alex Krantz <alex@alexkrantz.com>
 * @version 1.0
 */
public class ResponseSuccess {
    private String status;
    private int code;

    /**
     * Respond with success
     */
    ResponseSuccess() {
        this.status = "success";
        this.code = 200;
    }

    /**
     * Get the current status
     * @return current status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Get the status code
     * @return status code
     */
    public int getCode() {
        return this.code;
    }
}
