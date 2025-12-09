package bank;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = "bank.log";

    public void logInfo(String msg) {
        log("INFO", msg);
    }

    public void logError(String msg) {
        log("ERROR", msg);
    }

    private void log(String level, String msg) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(String.format("[%s] %s - %s%n", timestamp, level, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
