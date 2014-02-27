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

        HandList hands = frame.hands();
        Hand rightHand = hands.rightmost();

        if(rightHand != null) {
            float speed = rightHand.palmPosition().getZ();
            float turn = rightHand.palmPosition().getX();

            speed = normalise(speed);
            turn = normalise(turn);

            sendCommand("speed " + ((int)speed) + " " + ((int)turn));
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
        if (Math.abs(num) < 40)
            return 0;

        int sign = (num >= 0) ? (num == 0) ? 0 : 1 : -1;
        float normalisedValue = Math.min(150, Math.abs(num));
        return sign*normalisedValue;
    }
}
