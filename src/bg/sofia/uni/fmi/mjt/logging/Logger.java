package bg.sofia.uni.fmi.mjt.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Logger {
    private static PrintWriter printWriter;

    static {
        try {
            printWriter = new PrintWriter(new FileOutputStream("log.txt", true));
        } catch (FileNotFoundException e) {
            logMessageToConsole("Cannot open the file for logging");
        }
    }

    public static synchronized void logMessageToConsole(String message) {
        System.out.println(message);
    }

    public static synchronized void logMessage(String message) {

        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        System.out.println(message);

        if (printWriter == null) {
            logMessageToConsole("Cannot log the message to the file");
            return;
        }
        printWriter.println(message);
    }

    public static synchronized void logException(String message, Exception e) {

        if (message == null || e == null) {
            throw new IllegalArgumentException("message and exception cannot be null");
        }

        System.out.println(message);
        if (e.getMessage() != null) {
            System.out.println(e.getMessage());
        }

        if (printWriter == null) {
            logMessageToConsole("Cannot log the exception to the file");
            return;
        }

        printWriter.println(message);
        if (e.getMessage() != null) {
            printWriter.println(e.getMessage());
        }
        e.printStackTrace(printWriter);
        printWriter.flush();
    }

    public static synchronized void logException(Exception e) {

        if (e == null) {
            throw new IllegalArgumentException("exception cannot be null");
        }

        if (e.getMessage() != null) {
            System.out.println(e.getMessage());
        }

        if (printWriter == null) {
            logMessageToConsole("Cannot log the exception to the file");
            return;
        }

        if (e.getMessage() != null) {
            printWriter.println(e.getMessage());
        }
        e.printStackTrace(printWriter);
        printWriter.flush();
    }
}
