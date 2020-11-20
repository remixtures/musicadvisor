package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Scanner;

public class RecommendationEngine {

    private static final Scanner input = new Scanner(System.in);
    private static String authorizationServerUrl = "https://accounts.spotify.com";
    private static String apiUrl = "https://api.spotify.com";
    private static String category;
    private static int page = 5;
    private static boolean isAuthorized = false;

    public void starter(String[] args) {
        parseArguments(args);

        while (true) {
            String input = RecommendationEngine.input.nextLine();
            if (input.equals("exit")) {
                System.out.println("---GOODBYE!---");
                return;
            } else if (input.equals("auth") || isAuthorized) {
                if (input.contains("playlists")) {
                    category = input.substring(10);
                    input = input.substring(0, 9);
                }
                menuOptions(input);
            } else {
                System.out.println("Please, provide access for application.");
            }
        }
    }

    private static void parseArguments(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-access")) {
                authorizationServerUrl = args[1];
            }
            if (args[2].equals("-resource")) {
                apiUrl = args[3];
            }
            if (args[4].equals("-page")) {
                page = Integer.parseInt(args[5]);
            }
        } else {
            authorizationServerUrl = "https://accounts.spotify.com";
            apiUrl = "https://api.spotify.com";
            page = 5;
        }
    }

    private static void menuOptions(String input) {
        try {
            switch (input) {
                case "new":
                    new Viewer(page).printNew(Authorizer.getNewReleases(apiUrl));
                    break;
                case "featured":
                    new Viewer(page).printFeatured(Authorizer.getFeatured(apiUrl));
                    break;
                case "categories":
                    new Viewer(page).printCategories(Authorizer.getCategories(apiUrl));
                    break;
                case "playlists":
                    getPlaylists(category);
                    break;
                case "auth":
                    getAuthorized();
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred.");
        }
    }

    private static void getAuthorized() throws IOException, InterruptedException {
        isAuthorized = Authorizer.authorize(authorizationServerUrl);
        if (!isAuthorized) {
            System.out.println("---FAILED TO AUTHORIZE---");
        }
    }

    private static void getPlaylists(String name) throws IOException, InterruptedException {
        String id = "";
        JsonObject jsonObject = Authorizer.getCategories(apiUrl);
        for (JsonElement category : jsonObject.get("categories").getAsJsonObject().get("items").getAsJsonArray()) {
            if (name.equals(category.getAsJsonObject().get("name").getAsString())) {
                id = category.getAsJsonObject().get("id").getAsString();
            }
        }
        new Viewer(page).printPlaylists(Authorizer.getPlaylists(apiUrl, id));
    }
}