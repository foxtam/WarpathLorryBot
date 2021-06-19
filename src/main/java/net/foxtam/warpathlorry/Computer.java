package net.foxtam.warpathlorry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Computer {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static ComputerID getID() {
        return new ComputerID(getCurrentMachineID());
    }

    private static String getCurrentMachineID() {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(getUUID().getBytes(StandardCharsets.UTF_8));
            String hexString = bytesToHex(instance.digest());
            return hexString.substring(hexString.length() / 2);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getUUID() {
        String output = executeCmd("wmic csproduct get UUID");
        return output.substring(output.indexOf("\n")).trim();
    }

    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static String executeCmd(String command) {
        try {
            return tryExecuteCmd(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String tryExecuteCmd(String command) throws IOException {
        StringBuilder output = new StringBuilder();
        Process serNumProcess = Runtime.getRuntime().exec(command);
        BufferedReader sNumReader = new BufferedReader(new InputStreamReader(serNumProcess.getInputStream()));

        String line;
        while ((line = sNumReader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
