package leap;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

import java.io.PrintWriter;

public class LeapListener extends Listener {
    private static final long CHANGE_INTERVAL = 50;

    int firstHandId = -1;
    int secondHandId = -1;

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

        if(hands.count() > 0) {
            float rightZ = rightHand.stabilizedPalmPosition().getZ();
            //System.out.println("Z: " + rightZ);
            if(rightZ > 40)
                sendCommand("backwards");
            if(rightZ < -40)
                sendCommand("forwards");
            if(rightZ < 40 && rightZ > -40)
                sendCommand("stop");

            if(hands.count() > 1) {
                float roll = leftHand.direction().yaw();

                System.out.println("roll: " + roll);

                //PointableList leftPointables = leftHand.pointables();
                //if(leftPointables.count() > 1)
                //   sendCommand("setAngle: " + pointables.leftmost().stabilizedTipPosition().yaw());
            }
        }

        if(firstHandId != -1) {
            Hand firstHand = frame.hand(firstHandId);
            System.out.println("First hand: " + firstHand);
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
