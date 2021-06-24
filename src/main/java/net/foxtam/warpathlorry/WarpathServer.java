package net.foxtam.warpathlorry;

import java.net.URL;
import java.nio.file.Path;

import static net.foxtam.warpathlorry.UrlUtil.readStringByURL;
import static net.foxtam.warpathlorry.UrlUtil.toURL;

public class WarpathServer {

    private static final URL remoteVersionFile = toURL("https://garantmarket.net/warpath/warpath_last_version.json");
    private static final URL localVersionFile = toURL(Path.of("warpath_last_version.json"));

    public static Registration getRegistrationInfoFor(ComputerID computerID) {
        return new Registration(computerID);
    }

    public static Version getBotLastVersion() {
        JsonMap jsonMap = new JsonMap(readStringByURL(localVersionFile));
        return new Version(jsonMap.get("version"));
    }
}
