package bg.sofia.uni.fmi.mjt.communication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientRequestTest {

    @Test
    void testBuildWithValidParameters() {
        ClientRequest request = ClientRequest.builder()
            .setCommand("login")
            .setUsername("user")
            .setPassword("password")
            .setWebsite("facebook.com")
            .setWebsiteUsername("user@mail.com")
            .setWebsitePassword("websitePassword")
            .build();

        assertEquals("login", request.getCommand(), "getCommand should return the right command");
        assertEquals("user", request.getUsername(), "getUsername should return the right username");
        assertEquals("password", request.getPassword(), "getPassword should return the right password");
        assertEquals("facebook.com", request.getWebsite(), "getWebsite should return the right website");
        assertEquals("user@mail.com", request.getWebsiteUsername(), "getWebsiteUsername should return the right websiteUsername");
        assertEquals("websitePassword", request.getWebsitePassword(), "getWebsitePassword should return the right websitePassword");
    }

    @Test
    void testBuildWithNullCommandThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> ClientRequest.builder()
                .setCommand(null)
                .setUsername("user")
                .setPassword("password")
                .setWebsite("facebook.com")
                .setWebsiteUsername("user@mail.com")
                .setWebsitePassword("websitePassword")
                .build(), 
            "builder should throw IllegalArgumentException when command is null");
    }

    @Test
    void testBuildWithNullUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> ClientRequest.builder()
                .setCommand("login")
                .setUsername(null)
                .setPassword("password")
                .setWebsite("facebook.com")
                .setWebsiteUsername("user@mail.com")
                .setWebsitePassword("websitePassword")
                .build(),
            "builder should throw IllegalArgumentException when username is null");
    }

    @Test
    void testBuildWithNullPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> ClientRequest.builder()
                .setCommand("login")
                .setUsername("user")
                .setPassword(null)
                .setWebsite("facebook.com")
                .setWebsiteUsername("user@mail.com")
                .setWebsitePassword("websitePassword")
                .build(),
            "builder should throw IllegalArgumentException when password is null");
    }

    @Test
    void testBuildWithNullWebsiteThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> ClientRequest.builder()
                .setCommand("login")
                .setUsername("user")
                .setPassword("password")
                .setWebsite(null)
                .setWebsiteUsername("user@mail.com")
                .setWebsitePassword("websitePassword")
                .build(),
            "builder should throw IllegalArgumentException when website is null");
    }

    @Test
    void testBuildWithNullWebsiteUsernameThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> ClientRequest.builder()
                .setCommand("login")
                .setUsername("user")
                .setPassword("password")
                .setWebsite("facebook.com")
                .setWebsiteUsername(null)
                .setWebsitePassword("websitePassword")
                .build(),
            "builder should throw IllegalArgumentException when websiteUsername is null");
    }

    @Test
    void testBuildWithNullWebsitePasswordThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> ClientRequest.builder()
                .setCommand("login")
                .setUsername("user")
                .setPassword("password")
                .setWebsite("facebook.com")
                .setWebsiteUsername("user@mail.com")
                .setWebsitePassword(null)
                .build(),
            "builder should throw IllegalArgumentException when websitePassword is null");
    }
    
    @Test
    void testBuildWithSomeParameters() {
        ClientRequest request = ClientRequest.builder()
            .setCommand("login")
            .setUsername("user")
            .setPassword("password")
            .build();

        assertEquals("login", request.getCommand(), "getCommand should return the right command");
        assertEquals("user", request.getUsername(), "getUsername should return the right username");
        assertEquals("password", request.getPassword(), "getPassword should return the right password");
        assertNull(request.getWebsite(), "getWebsite should return null when not set");
        assertNull(request.getWebsiteUsername(), "getWebsiteUsername should return null when not set");
        assertNull(request.getWebsitePassword(), "getWebsitePassword should return null when not set");
    }
}
