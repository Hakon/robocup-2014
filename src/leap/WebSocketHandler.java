package leap; /**
 * Created by robocup on 27/02/14.
 */

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebSocketHandler extends WebSocketServer {

    PrintWriter out;
    private String lastSentCommand;

    public WebSocketHandler(int port, PrintWriter out ) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.out = out;
        System.out.println("Setting up websocket on port: " + this.getPort());
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        sendCommand(s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    private void sendCommand(String command) {
        if (!command.equals(lastSentCommand)) {
            out.println(command);
            System.out.println("Sending command from WS: " + command);
            lastSentCommand = command;
        }
    }
}
