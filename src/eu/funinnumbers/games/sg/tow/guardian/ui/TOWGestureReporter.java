package eu.funinnumbers.games.sg.tow.guardian.ui;

import com.sun.spot.util.Utils;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.games.sg.tow.guardian.TOWManager;
import eu.funinnumbers.guardian.GuardianInfo;
import eu.funinnumbers.guardian.communication.dtservice.DTSManager;
import eu.funinnumbers.guardian.ui.gestures.GestureListener;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.util.Date;


/**
 * Implementation of the GestureListener for TOW game.
 */
public class TOWGestureReporter implements GestureListener {
    /**
     * Invoked when a clockwise gesture is completed.
     */
    public void doClockwiseGesture() {
        LEDManager.getInstance().setOffLEDS(0, 7);
        Utils.sleep(200);
        Logger.getInstance().debug("Pull!");
        final Event event = new Event();
        event.setType("TOW");
        event.setTimeStamp(new Date());
        event.setDescription("TOWCIR");
        //TODO add actual id
        event.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        DTSManager.getInstance().sendEvent(event);
        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());
    }

    /**
     * Invoked when a counter clockwise gesture is completed.
     */
    public void doCounterClockwiseGesture() {
        //do nothig
    }

    /**
     * Invoked when a horizontal gesture is completed.
     */
    public void doHorizontalViolentMovement() {
        LEDManager.getInstance().setOffLEDS(0, 7);
        Logger.getInstance().debug("Pull!");
        final Event event = new Event();
        event.setType("TOW");
        event.setTimeStamp(new Date());
        event.setDescription("TOWhor");
        //TODO add actual id
        event.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        DTSManager.getInstance().sendEvent(event);
        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());

    }


    /**
     * Invoked when any movement is done.
     */
    public void doAnyMovement() {
        // do nothing
    }

    /**
     * Invoked when no movement is done.
     */
    public void noMovement() {
        // do nothing
    }

    /**
     * Invoked when a right tilt is recognized.
     */
    public void doForwardTilt() {
        // do nothig
    }

    /**
     * Invoked when a left tilt is recognized.
     */
    public void doBackTilt() {
        /*LEDManager.getInstance().setOffLEDS(0,7);
        Utils.sleep(200);
        Logger.getInstance().debug("Pull!");
        final Event event = new Event();
        event.setType("TOW");
        event.setTimeStamp(new Date());
        event.setDescription("TOWbtilt");
        //TODO add actual id
        event.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        DTSManager.getInstance().sendEvent(event);
        LEDManager.getInstance().setOnLEDS(0,7,TOWManager.getInstance().getColor());*/
    }

    /**
     * Reports Right Switch push.
     */
    public void doRightSwitch() {
        //Do nothing
    }

    /**
     * Reports Left Switch push.
     */
    public void doLeftSwitch() {
        //Do nothing
    }

    public void doVerticalViolentMovement() {
        LEDManager.getInstance().setOffLEDS(0, 7);
        Utils.sleep(200);
        Logger.getInstance().debug("Pull!");
        final Event event = new Event();
        event.setType("TOW");
        event.setTimeStamp(new Date());
        event.setDescription("TOWver");
        //TODO add actual id
        event.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        DTSManager.getInstance().sendEvent(event);
        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());

    }

    public void doForwardViolentMovement() {
        LEDManager.getInstance().setOffLEDS(0, 7);
        Utils.sleep(200);
        Logger.getInstance().debug("Pull!");
        final Event event = new Event();
        event.setType("TOW");
        event.setTimeStamp(new Date());
        event.setDescription("TOWfor");
        //TODO add actual id
        event.setGuardianID(GuardianInfo.getInstance().getGuardian().getID());
        DTSManager.getInstance().sendEvent(event);
        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());
    }
}
