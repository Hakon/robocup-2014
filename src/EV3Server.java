import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Math.abs;

public class EV3Server {


    public static void main(String[] args) throws IOException {

        System.out.println("Running...");
        int portNumber = 69;

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()))
        ) {

            String inputLine;

            out.println("Connection established. Start sending commands madderfakker!");

            Motor.A.setSpeed();


            while ((inputLine = in.readLine()) != null) {
                System.out.println("kommando mottatt: " + inputLine);
                out.println("kommando mottatt: " + inputLine);

                processCommand(inputLine);


            }
        } catch (
                IOException e
                )

        {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Exiting...");
    }

    public static void processCommand(String inputLine) {
        switch(inputLine) {
            case "forwards" :
                Motor.A.forward();
                Motor.B.backward();
                break;
            case "backwards" :
                Motor.A.backward();
                Motor.B.forward();
                break;

            case "left" :

                Motor.A.stop();
                Motor.B.forward();
                break;

            case "right" :

                Motor.A.forward();
                Motor.B.stop();
                break;
            case "stop" :
                Motor.A.stop();
                Motor.B.stop();
                break;
        }
    }
}
