package leap;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

import java.io.PrintWriter;

public class LeapListener extends Listener {
    private static final long CHANGE_INTERVAL = 50;



    private long startTime;
    private PrintWriter printWriter;
    private String lastSentCommand;
    private CommandClient commandClient;

    public LeapListener(PrintWriter printWriter, CommandClient client) {
        this.printWriter = printWriter;
        startTime = System.currentTimeMillis();
        commandClient = client;
    }

    @Override
    public void onConnect(Controller controller) {
        System.out.println("Controller has been connected");
        controller.enableGesture(Type.TYPE_CIRCLE);
        controller.enableGesture(Type.TYPE_SWIPE);
    }

    @Override
    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    @Override
    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    @Override
    public void onFrame(Controller controller) {
        if (changeIntervalPassed()) {
            return;     // don't do anything until changeInterval time has passed
        }

        Frame frame = controller.frame();

        // if no hand detected, give up
        if (frame.hands().isEmpty()) {
            return;
        }

        // if the screen isn't available give up
        Screen screen = controller.locatedScreens().get(0);
        if (screen == null) {
            System.out.println("No screen found");
            return;
        }
        if (!screen.isValid()) {
            System.out.println("Screen not valid");
            return;
        }

        
        //System.out.println("handsCount = " + handsCount);

        GestureList gestures = frame.gestures();
        for(int i = 0; i < gestures.count(); i++ ) {
            Gesture gesture = gestures.get(i);
            switch(gesture.type()) {
                case TYPE_SWIPE:
                    SwipeGesture swipe = new SwipeGesture(gesture);
                    System.out.println("Swipe distance: " + swipe.startPosition().distanceTo(swipe.position()));
                    if(swipe.startPosition().distanceTo(swipe.position()) > 10) {
                        if(swipe.direction().getX() > 0){
                            sendCommand("continue");
                            stopped = false;
                        }else{
                            sendCommand("abort");
                            System.out.println("STOP: " + swipe.direction().getY());
                            stopped = true;
                        }
                    }

                    break;
                case TYPE_KEY_TAP:
                    if(!stopped)
                        sendCommand("fire");
                break;
            }
        }

        if (!stopped){
            int handsCount = frame.hands().count();

            if(handsCount == 2) {
                sendCommand("forward");
            }
            if(handsCount == 3) {
                sendCommand("left");
            }
            if(handsCount == 4) {
                sendCommand("right");
            }
        }


        startTime = System.currentTimeMillis();
    }

    private boolean changeIntervalPassed() {
        return (System.currentTimeMillis() - startTime) < CHANGE_INTERVAL;
    }

    private void sendCommand(String command) {
        if (!command.equals(lastSentCommand)) {
            System.out.println("Sending command: " + command);
            printWriter.println(command);
            lastSentCommand = command;
        }
    }
}
