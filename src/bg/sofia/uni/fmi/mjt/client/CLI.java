package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.exception.CommandException;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import bg.sofia.uni.fmi.mjt.logging.Logger;

import java.util.Scanner;

public class CLI {

    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String HELP_COMMAND = "help";

    private final CommandHandler commandHandler;
    private final Scanner scanner;

    public CLI() throws CommunicationException {
        this.commandHandler = new CommandHandler();
        this.scanner = new Scanner(System.in);
    }

    public CLI(CommandHandler commandHandler) {

        if (commandHandler == null) {
            throw new IllegalArgumentException("commandHandler cannot be null");
        }

        this.commandHandler = commandHandler;
        this.scanner = new Scanner(System.in);
    }

    public CLI(CommandHandler commandHandler, Scanner scanner) {

        if (commandHandler == null || scanner == null) {
            throw new IllegalArgumentException("commandHandler and scanner cannot be null");
        }

        this.commandHandler = commandHandler;
        this.scanner = scanner;
    }

    public void start() throws CommunicationException {

        try {
            Logger.logMessageToConsole(commandHandler.handleCommand(HELP_COMMAND));
        } catch (CommandException e) {
            Logger.logMessageToConsole(e.getMessage());
        }

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();

            try {
                Logger.logMessageToConsole(commandHandler.handleCommand(command));
            } catch (CommandException e) {
                Logger.logMessageToConsole(e.getMessage());
            }

            if (command.equals(DISCONNECT_COMMAND)) {
                break;
            }
        }
    }
}
