package net.foxtam.warpathlorry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Time {
    public static LocalDate getGlobalTime() {
        try {
            URL url = new URL("https://currentmillis.com/time/minutes-since-unix-epoch.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            long minutes = Long.parseLong(in.readLine());
            in.close();
            con.disconnect();
            Instant instant = Instant.ofEpochSecond(minutes * 60);
            return LocalDate.ofInstant(instant, ZoneId.of("UTC+0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
