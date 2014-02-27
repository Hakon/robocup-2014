package leap;

import com.leapmotion.leap.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandClient {
    PrintWriter outWriter;
    BufferedReader inReader;

    public static void main(String[] args) {

        new CommandClient().run();

    }

    public void run() {
        String hostName = "192.168.0.102";
        int portNumber = 69;

        try(
                Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))
        ) {
            outWriter = out;
            inReader = in;

            Controller controller = new Controller();
            LeapListener leapListener = new LeapListener(out, this);
            controller.addListener(leapListener);


            String fromServer = in.readLine();
            while (fromServer != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("quit")) {
                    System.out.println("Quitting...");
                    break;
                }
                fromServer = in.readLine();
            }
            controller.removeListener(leapListener);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            e.printStackTrace();
        }
    }

  /*  public void abort() {
        outWriter.println("abort");
    }
    public void carryon() {
        outWriter.println("continue");
    }

    public void driveForward() {
        outWriter.println("forward");
    }
    public void driveLeft() {
        outWriter.println("left");
    }
    public void driveRight() {
        outWriter.println("right");
    }*/
}
