import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv("PORT")));

        get("/", ((request, response) -> "Hello World!"));
    }
}
