package dansplugins.examplemfexpansion.utils;

import dansplugins.examplemfexpansion.ExampleMFExpansion;

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
        if (ExampleMFExpansion.getInstance().isDebugEnabled()) {
            System.out.println("[ExamplePonderPlugin] " + message);
        }
    }

}
