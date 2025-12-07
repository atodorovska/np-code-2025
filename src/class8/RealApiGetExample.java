package class8;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RealApiGetExample {

    public static void main(String[] args) {
        String url = "https://jsonplaceholder.typicode.com/posts/1";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body:");
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
