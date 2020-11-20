package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Viewer {

    private static final List<StringBuilder> list = new ArrayList<>();
    private final int entriesPerPage;

    Viewer(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
    }

    public void printNew(JsonObject jsonObject) {
        list.clear();
        jsonObject.getAsJsonObject("albums").get("items").getAsJsonArray().forEach(item -> {
            String name = item.getAsJsonObject()
                    .get("name")
                    .getAsString();
            String url = item.getAsJsonObject()
                    .get("external_urls")
                    .getAsJsonObject()
                    .get("spotify")
                    .getAsString();

            List<String> artists = new ArrayList<>();
            for (JsonElement value : item.getAsJsonObject().get("artists").getAsJsonArray()) {
                artists.add(value.getAsJsonObject().get("name").getAsString());
            }
            StringBuilder entry = new StringBuilder();
            entry.append(name).append("\n")
                    .append(artists.toString()).append("\n")
                    .append(url).append("\n");
            list.add(entry);
        });
        pagination();
    }

    public void printCategories(JsonObject jsonObject) {
        list.clear();
        jsonObject.getAsJsonObject("categories").get("items").getAsJsonArray().forEach(category -> {
            list.add(new StringBuilder(category.getAsJsonObject().get("name").getAsString()));
        });
        pagination();
    }

    public void printPlaylists(JsonObject jsonObject) {
        List<String> jsonKeySet = new ArrayList<>(jsonObject.keySet());
        if (jsonKeySet.contains("error")) {
            System.out.println(jsonObject.get("error").getAsJsonObject().get("message").getAsString());
            return;
        }
        printFeatured(jsonObject);
    }

    public void printFeatured(JsonObject jsonObject) {
        list.clear();
        jsonObject.getAsJsonObject("playlists").get("items").getAsJsonArray().forEach(item -> {
            String name = item.getAsJsonObject()
                    .get("name")
                    .getAsString();
            String url = item.getAsJsonObject()
                    .get("external_urls")
                    .getAsJsonObject()
                    .get("spotify")
                    .getAsString();
            list.add(new StringBuilder(name + "\n" + url));
        });
        pagination();
    }

    private void pagination() {
        Scanner input = new Scanner(System.in);
        int start = 0;
        int end = entriesPerPage;
        int currentPage = 1;
        printPage(start, end, currentPage);
        while (true) {
            String command = input.nextLine();
            switch (command) {
                case "prev":
                    if (start - entriesPerPage < 0) {
                        System.out.println("No more pages");
                    } else {
                        start -= entriesPerPage;
                        end -= entriesPerPage;
                        printPage(start, end, --currentPage);
                    }
                    break;
                case "next":
                    if (end > list.size() - 1) {
                        System.out.println("No more pages");
                    } else {
                        start += entriesPerPage;
                        end += entriesPerPage;
                        printPage(start, end, ++currentPage);
                    }
                    break;
                case "exit":
                    return;
            }
        }
    }

    private void printPage(int start, int end, int page) {
        int totalPages = list.size() <= entriesPerPage ? 1 : Math.abs(list.size() / entriesPerPage);
        for (int i = start; i < end; i++) {
            if (i > list.size() - 1) {
                break;
            }
            System.out.println(list.get(i));
        }
        System.out.printf("---PAGE %d OF %d---\n", page, totalPages);
    }
}