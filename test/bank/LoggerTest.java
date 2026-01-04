package bank;

import org.junit.jupiter.api.*;

import bank.infrastructure.Logger;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    private static final Path LOG_FILE = Path.of("bank.log");
    private Logger logger;

    @BeforeEach
    void setUp() throws IOException {
        // Delete log file before each test
        Files.deleteIfExists(LOG_FILE);
        logger = new Logger();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(LOG_FILE);
    }

    @Test
    void testLogInfo() throws IOException {
        logger.logInfo("Test INFO message");
        assertTrue(Files.exists(LOG_FILE));
        List<String> lines = Files.readAllLines(LOG_FILE);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("INFO"));
        assertTrue(lines.get(0).contains("Test INFO message"));
    }

    @Test
    void testLogError() throws IOException {
        logger.logError("Test ERROR message");
        assertTrue(Files.exists(LOG_FILE));
        List<String> lines = Files.readAllLines(LOG_FILE);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("ERROR"));
        assertTrue(lines.get(0).contains("Test ERROR message"));
    }

    @Test
    void testMultipleLogs() throws IOException {
        logger.logInfo("First INFO");
        logger.logError("Error message");
        logger.logInfo("Second INFO");

        List<String> lines = Files.readAllLines(LOG_FILE);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("INFO") && lines.get(0).contains("First INFO"));
        assertTrue(lines.get(1).contains("ERROR") && lines.get(1).contains("Error message"));
        assertTrue(lines.get(2).contains("INFO") && lines.get(2).contains("Second INFO"));
    }

    @Test
    void testLogContainsTimestamp() throws IOException {
        logger.logInfo("Timestamp test");
        List<String> lines = Files.readAllLines(LOG_FILE);
        assertTrue(lines.get(0).matches("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\].*"));
    }
}
