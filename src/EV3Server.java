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
            int speed = Integer.parseInt(split[1]);
            int turn = Integer.parseInt(split[2]);

            float maxSpeed = (Motor.A.getMaxSpeed() + Motor.B.getMaxSpeed() / 2);
            double speedModifier = (speed / 150.0);

            turn += 150;
            int speedL = (int) (maxSpeed * (speedModifier*((300.0-turn)/300.0)));
            int speedR = (int) (maxSpeed * (speedModifier*(turn/300.0)));

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
                    Motor.C.forward();
                    playFiringSound();
                    //Motor.B.backward();
                    break;
                case "playsound":
                    playSong();
                    break;
            }
        }
    }

    public static void playFiringSound(){
        /*short[] note = {2349, 115, 0, 5, 1760, 165, 0, 35};
        for(int i=0; i<note.length; i+=2){
            short w = note[i+1];
            int n = note[i];
            if (n != 0)
                Sound.playTone(n, w * 10);

            try {
                Thread.sleep(w*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static void playSong(){
        return;
    }
}
