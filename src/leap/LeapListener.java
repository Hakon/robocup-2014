package leap;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

import java.io.PrintWriter;

public class LeapListener extends Listener {
    private static final long CHANGE_INTERVAL = 50;
    int firstHandId = -1;
    int secondHandId = -1;

    boolean droppedLast = true;

    private long startTime;
    private PrintWriter printWriter;
    private String lastSentCommand;

    public LeapListener(PrintWriter printWriter) {
        this.printWriter = printWriter;
        startTime = System.currentTimeMillis();
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
        HandList hands = frame.hands();

        Hand rightHand = hands.rightmost();
        Hand leftHand = hands.leftmost();

        if(hands.count() > 1) {
            float rightZ = rightHand.palmPosition().getZ();
            float leftZ = leftHand.palmPosition().getZ();

            rightZ = normalise(rightZ);
            leftZ = normalise(leftZ);


            //System.out.println("Z: " + rightZ);
            if(rightZ < 40 && rightZ > -40)
                rightZ = 0;

            if(leftZ < 40 && leftZ > -40)
                leftZ = 0;

            sendCommand("speed " + ((int)-leftZ) + " " + ((int)-rightZ));
        }

        if(firstHandId != -1) {
            Hand firstHand = frame.hand(firstHandId);
            System.out.println("First hand: " + firstHand);
        }
    }

    private boolean changeIntervalPassed() {
        return (System.currentTimeMillis() - startTime) < CHANGE_INTERVAL;
    }

    private void sendCommand(String command) {
        if (!command.equals(lastSentCommand) && !changeIntervalPassed()) {
            System.out.println("Sending command: " + command);
            printWriter.println(command);
            lastSentCommand = command;
            startTime = System.currentTimeMillis();
        }
    }

    private float normalise(float num){
        int sign = (num >= 0) ? (num == 0) ? 0 : 1 : -1;
        float normalisedValue = Math.min(150, Math.abs(num));

        return sign*normalisedValue;
    }
}
