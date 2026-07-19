package bg.sofia.uni.fmi.mjt.client.command;

import bg.sofia.uni.fmi.mjt.communication.ServerResponse;

public class HelpCommand implements Command {
    public static final String POSSIBLE_COMMANDS = """
                                                    
                                                    List of possible commands:
                                                        register <user> <password> <password-repeat>
                                                        login <user> <password>
                                                        logout
                                                        retrieve-credentials <website> <user>
                                                        generate-password <website> <user>
                                                        add-password <website> <user> <password>
                                                        remove-password <website> <user>
                                                        disconnect
                                                        help
                                                    """;

    @Override
    public ServerResponse execute(String[] arguments) {
        return new ServerResponse(POSSIBLE_COMMANDS, true);
    }
}
