package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.communication.ClientRequest;
import bg.sofia.uni.fmi.mjt.communication.ServerResponse;
import bg.sofia.uni.fmi.mjt.exception.CommunicationException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NetworkManagerTest {
    private static NetworkManager networkManager;

    @Test
    void testSendRequestToServerSuccessfully() {
        Socket mockSocket = mock();
        ClientRequest mockRequest = mock();
        when(mockRequest.getCommand()).thenReturn("login");

        ServerResponse response = new ServerResponse("response", true);

        try {
            ByteArrayOutputStream responseByteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream responseObjectOutputStream = new ObjectOutputStream(responseByteArrayOutputStream);
            responseObjectOutputStream.writeObject(response);
            responseObjectOutputStream.flush();
            byte[] responseBytes = responseByteArrayOutputStream.toByteArray();

            InputStream inputStream = new ByteArrayInputStream(responseBytes);
            OutputStream outputStream = new ByteArrayOutputStream();

            when(mockSocket.getOutputStream()).thenReturn(outputStream);
            when(mockSocket.getInputStream()).thenReturn(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while testing sendRequestToServer method of NetworkManager", e);
        }

        ServerResponse actualResponse;

        try {
            networkManager = new NetworkManager(mockSocket);
            actualResponse = networkManager.sendRequestToServer(mockRequest);
        } catch (CommunicationException e) {
            throw new RuntimeException("CommunicationException occurred while testing sendRequestToServer method of NetworkManager", e);
        }

        assertEquals(response, actualResponse, "The response should be the same as the one from the server");
    }

    @Test
    void testSendRequestToServerWithNullRequestThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> networkManager.sendRequestToServer(null), "Sending a null request should throw an IllegalArgumentException");
    }

}
