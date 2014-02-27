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

    public static boolean pause = false;

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


            while ((inputLine = in.readLine()) != null) {
                System.out.println("kommando mottatt: " + inputLine);
                out.println("kommando mottatt: " + inputLine);
                LCD.clear();
                LCD.drawString("cmd: " + inputLine, 0, 5);

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
        if(pause && inputLine != "continue")
            return;
        switch(inputLine) {
            case "forward" :
                Motor.A.forward();
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
            case "abort" :
                Motor.A.stop();
                Motor.B.stop();
                pause = true;
            case "continue" :
                pause = false;
            case "fire" :
                Motor.C.setAcceleration(10000);
                Motor.C.setSpeed(10000);
                Motor.C.rotate(-75);
                Motor.C.rotate(75);
        }
    }
}
