package net.foxtam.warpathlorry;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class Registration {

    private final LocalDate expirationDate;
    private final boolean hasRegistration;
    private LocalDate globalTime;

    public Registration() throws IOException {
        Optional<LocalDate> date = Licenses.getExpirationDateOf(Computer.getID());
        hasRegistration = date.isPresent();
        expirationDate = hasRegistration ? date.get() : null;
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
