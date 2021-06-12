package net.foxtam.warpathlorry;

import java.io.IOException;

public class CantReadPropertiesException extends RuntimeException {
    public CantReadPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }
}
