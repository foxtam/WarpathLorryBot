package net.foxtam.warpathlorry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Licenses {

    private static final URL clientsFileFromServer = toURL("https://garantmarket.net/warpath/warpath_clients.json");
    private static final URL localClientsFile = toURL(Path.of("warpath_clients.json"));

    private static final java.lang.reflect.Type type =
            new TypeToken<Map<String, String>>() {
            }.getType();

    private static URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<LocalDate> getExpirationDateOf(ComputerID id) {
        var gson = new Gson();

        String jsonString = readStringFromURL(localClientsFile);
        Map<String, String> map = gson.fromJson(jsonString, type);

        String key = id.toString();
        return Optional.ofNullable(map.get(key)).map(LocalDate::parse);
    }

    public static String readStringFromURL(URL url) {
        try (Scanner scanner = new Scanner(url.openStream(), StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL toURL(String string) {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
