import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String[] split = inputLine.split(" ");
        if(split[0].equals("speed")) {
            int speedL = Integer.parseInt(split[1]);
            int speedR = Integer.parseInt(split[2]);

            speedL = (int)-(speedL / 150.0 * Motor.A.getMaxSpeed());
            speedR = (int)-(speedR / 150.0 * Motor.B.getMaxSpeed());

            Motor.A.setSpeed(speedL);
            Motor.B.setSpeed(speedR);

            if(speedL == 0) {
                Motor.A.stop();
            }

            if(speedR == 0) {
                Motor.B.stop();
            }

            if(speedL > 0) {

                Motor.A.forward();
            }

            if(speedR > 0) {

                Motor.B.forward();
            }

            if(speedL < 0) {

                Motor.A.backward();
            }

            if(speedR < 0) {

                Motor.B.backward();
            }
        } else {
            switch(inputLine) {
                case "fire" :
                    Motor.A.forward();
                    //Motor.B.backward();
                    break;
                case "playsound":
                    break;
            }
        }
    }
}
