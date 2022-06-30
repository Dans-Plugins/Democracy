package dansplugins.democracy.utils;

import dansplugins.democracy.Democracy;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private final Democracy democracy;

    public Logger(Democracy democracy) {
        this.democracy = democracy;
    }

    public void log(String message) {
        if (democracy.isDebugEnabled()) {
            System.out.println("[ExamplePonderPlugin] " + message);
        }
    }

}
