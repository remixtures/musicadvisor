package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Authorizer {

    private static final String NEW = "/v1/browse/new-releases?";
    private static final String FEATURED = "/v1/browse/featured-playlists?";
    private static final String CATEGORIES = "/v1/browse/categories";
    private static final String PLAYLISTS = "/v1/browse/categories/%s/playlists";
    private static final String CLIENT_ID = "409ad95019b34181ad22ed97e9eddf47";
    private static final String CLIENT_SECRET = "b65db44a485d49159a86247095e31ee4";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    private static String authorizationCode;
    private static HttpResponse<String> response;
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static String accessToken;

    static boolean authorize(String authorizationServerUrl) throws IOException, InterruptedException {
        System.out.println("use this link to request the access code:");
        System.out.println(authorizationServerUrl
                + "/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=" + RESPONSE_TYPE);
        System.out.println("waiting for code...");
        getRequest(authorizationServerUrl);
        return response.statusCode() == 200;
    }

    private static void getRequest(String authorizationServerUrl) throws InterruptedException, IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.start();
        server.createContext("/",
                exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String responseBody;
                    if (query != null && query.contains("code")) {
                        authorizationCode = query.substring(5);
                        responseBody = "Got the code. Return back to your program.";
                        System.out.println("code received");
                        System.out.println(authorizationCode);
                        System.out.println("Making http request for access_token...");
                        try {
                            response = getAccessToken(authorizationServerUrl);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                        accessToken = json.get("access_token").getAsString();
                        System.out.println("Success!");
                    } else {
                        System.out.println(query);
                        responseBody = "Authorization code not found. Try again.";
                    }
                    exchange.sendResponseHeaders(200, responseBody.length());
                    exchange.getResponseBody().write(responseBody.getBytes());
                    exchange.getResponseBody().close();
                }
        );

        while (authorizationCode == null) {
            Thread.sleep(100);
        }
        server.stop(5);
    }

    private static HttpResponse<String> getAccessToken(String authorizationServerUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(authorizationServerUrl + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=" + GRANT_TYPE
                                + "&code=" + authorizationCode
                                + "&client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&redirect_uri=" + REDIRECT_URI))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static JsonObject getNewReleases(String apiUrl) throws IOException, InterruptedException {
        response = sendRequest(apiUrl + NEW);
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getCategories(String apiUrl) throws IOException, InterruptedException {
        response = sendRequest(apiUrl + CATEGORIES);
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getPlaylists(String apiUrl, String id) throws IOException, InterruptedException {
        response = sendRequest(apiUrl + String.format(PLAYLISTS, id));
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public static JsonObject getFeatured(String apiUrl) throws IOException, InterruptedException {
        response = sendRequest(apiUrl + FEATURED);
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static HttpResponse<String> sendRequest(String url) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(url))
                .GET()
                .build();
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
}