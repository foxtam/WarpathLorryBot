package net.foxtam.warpathlorry;

import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static net.foxtam.warpathlorry.UrlUtil.readStringByURL;
import static net.foxtam.warpathlorry.UrlUtil.toURL;

public class Registration {

    private static final URL remoteClientsFile = toURL("https://garantmarket.net/warpath/warpath_clients.json");
    private static final URL localClientsFile = toURL(Path.of("warpath_clients.json"));
    private final LocalDate expirationDate;
    private final boolean hasRegistration;
    private LocalDate globalTime;

    public Registration(ComputerID computerID) {
        Optional<LocalDate> date = getExpirationDateOf(computerID);
        hasRegistration = date.isPresent();
        expirationDate = hasRegistration ? date.get() : null;
    }

    private static Optional<LocalDate> getExpirationDateOf(ComputerID id) {
        JsonMap jsonMap = new JsonMap(readStringByURL(remoteClientsFile));
        String key = id.toString();
        String value = jsonMap.get(key);
        return Optional.ofNullable(value).map(LocalDate::parse);
    }

    public LocalDate getExpirationLicenseDate() {
        if (expirationDate == null) throw new IllegalStateException("No expiration date");
        return expirationDate;
    }

    public boolean hasRegistration() {
        return hasRegistration;
    }

    public boolean isLicenseValid() {
        if (globalTime == null) globalTime = Time.getGlobalTime();
        return globalTime.isBefore(expirationDate);
    }
}
