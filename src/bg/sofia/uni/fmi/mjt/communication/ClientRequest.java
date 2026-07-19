package bg.sofia.uni.fmi.mjt.communication;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private final String command;
    private final String username;
    private final String password;
    private final String website;
    private final String websiteUsername;
    private final String websitePassword;

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getWebsite() {
        return website;
    }

    public String getWebsiteUsername() {
        return websiteUsername;
    }

    public String getWebsitePassword() {
        return websitePassword;
    }

    private ClientRequest(ClientRequestBuilder builder) {

        if (builder == null) {
            throw new IllegalArgumentException("builder cannot be null");
        }

        this.command = builder.command;
        this.username = builder.username;
        this.password = builder.password;
        this.website = builder.website;
        this.websiteUsername = builder.websiteUsername;
        this.websitePassword = builder.websitePassword;
    }

    public static ClientRequestBuilder builder() {
        return new ClientRequestBuilder();
    }

    public static class ClientRequestBuilder {
        private String command;
        private String username;
        private String password;
        private String website;
        private String websiteUsername;
        private String websitePassword;

        public ClientRequestBuilder setCommand(String command) {

            if (command == null) {
                throw new IllegalArgumentException("command cannot be null");
            }

            this.command = command;
            return this;
        }

        public ClientRequestBuilder setUsername(String username) {

            if (username == null) {
                throw new IllegalArgumentException("username cannot be null");
            }

            this.username = username;
            return this;
        }

        public ClientRequestBuilder setPassword(String password) {

            if (password == null) {
                throw new IllegalArgumentException("password cannot be null");
            }

            this.password = password;
            return this;
        }

        public ClientRequestBuilder setWebsite(String website) {

            if (website == null) {
                throw new IllegalArgumentException("website cannot be null");
            }

            this.website = website;
            return this;
        }

        public ClientRequestBuilder setWebsiteUsername(String websiteUsername) {

            if (websiteUsername == null) {
                throw new IllegalArgumentException("websiteUsername cannot be null");
            }

            this.websiteUsername = websiteUsername;
            return this;
        }

        public ClientRequestBuilder setWebsitePassword(String websitePassword) {

            if (websitePassword == null) {
                throw new IllegalArgumentException("websitePassword cannot be null");
            }

            this.websitePassword = websitePassword;
            return this;
        }

        public ClientRequest build() {
            return new ClientRequest(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClientRequest)) {
            return false;
        }
        ClientRequest other = (ClientRequest) obj;
        return java.util.Objects.equals(command, other.command)
            && java.util.Objects.equals(username, other.username)
            && java.util.Objects.equals(password, other.password)
            && java.util.Objects.equals(website, other.website)
            && java.util.Objects.equals(websiteUsername, other.websiteUsername)
            && java.util.Objects.equals(websitePassword, other.websitePassword);

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
