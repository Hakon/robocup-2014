package leap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;

import java.io.PrintWriter;

public class LeapListener extends Listener {
    private static final long CHANGE_INTERVAL = 50;

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

        int handsCount = frame.hands().count();

        System.out.println("handsCount = " + handsCount);

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
