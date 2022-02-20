package dansplugins.democracy.utils;

import dansplugins.democracy.Democracy;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {

    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (Democracy.getInstance().isDebugEnabled()) {
            System.out.println("[ExamplePonderPlugin] " + message);
        }
    }

}
