package net.foxtam.warpathlorry;

import com.google.gson.JsonSyntaxException;

import java.net.URL;
import java.nio.file.Path;

import static net.foxtam.warpathlorry.UrlUtil.readStringByURL;
import static net.foxtam.warpathlorry.UrlUtil.toURL;

public class WarpathServer {

    private static final URL remoteVersionFile = toURL("https://garantmarket.net/warpathbot/warpath_last_version.json");
    private static final URL localVersionFile = toURL(Path.of("warpath_last_version.json"));

    public static Registration getRegistrationInfoFor(ComputerID computerID) {
        return new Registration(computerID);
    }

    public static Version getBotLastVersion() {
        try {
            JsonMap jsonMap = new JsonMap(readStringByURL(remoteVersionFile));
            return new Version(jsonMap.get("version"));
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Unable to read: " + remoteVersionFile);
        }
    }
}
