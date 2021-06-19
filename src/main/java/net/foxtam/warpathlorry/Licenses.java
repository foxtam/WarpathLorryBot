package net.foxtam.warpathlorry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class Licenses {

    private static final String clientsFile = "https://garantmarket.net/warpath/warpath_clients.json";
    private static final boolean LOCAL_TEST = false;

    public static Optional<LocalDate> getExpirationDateOf(ComputerID id) throws IOException {
        return tryGetClientExpirationDate(id);
    }

    private static Optional<LocalDate> tryGetClientExpirationDate(ComputerID id) throws IOException {
        Gson gson = new Gson();

        java.lang.reflect.Type type =
              new TypeToken<Map<String, String>>() {
              }.getType();

        var jsonReader = new InputStreamReader(getClientsFileInputStream(), StandardCharsets.UTF_8);
        Map<String, String> map = gson.fromJson(jsonReader, type);

        String key = id.toString();
        return Optional.ofNullable(map.get(key)).map(LocalDate::parse);
    }

    private static InputStream getClientsFileInputStream() throws IOException {
        if (LOCAL_TEST) {
            String clientsFile = "warpath_clients.json";
            return new BufferedInputStream(new FileInputStream(clientsFile));
        } else {
            URL url = new URL(clientsFile);
            return new BufferedInputStream(url.openStream());
        }
    }
}
