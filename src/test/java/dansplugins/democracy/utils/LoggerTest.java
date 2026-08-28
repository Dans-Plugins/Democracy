package dansplugins.democracy.utils;

import dansplugins.democracy.Democracy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoggerTest {
    private Democracy democracy;
    private ByteArrayOutputStream captured;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        democracy = mock(Democracy.class);

        captured = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void messageIsPrefixedWithThePluginName() {
        when(democracy.isDebugEnabled()).thenReturn(true);

        new Logger(democracy).log("something happened");

        assertEquals("[Democracy] something happened", captured.toString().trim());
    }

    @Test
    void nothingIsPrintedWhenDebugModeIsDisabled() {
        when(democracy.isDebugEnabled()).thenReturn(false);

        new Logger(democracy).log("something happened");

        assertTrue(captured.toString().isEmpty());
    }
}
