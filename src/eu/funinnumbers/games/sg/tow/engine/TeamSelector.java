package eu.funinnumbers.games.sg.tow.engine;

import com.sun.spot.util.IEEEAddress;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import java.rmi.RemoteException;
import java.util.HashMap;


/**
 * Tug of War game logic.
 * Receives updates with new Events from Event Consumer and process them.
 */
public class TeamSelector implements Observer { //NOPMD

    /**
     * TOWEngineApp Instance.
     */
    private final TOWEngineApp towEngineApp;

    private final HashMap<Integer, Integer> teams = new HashMap<Integer, Integer>();

    private int team = 0;

    /**
     * Default Constructor.
     *
     * @param engineApp CIEngineApp instance
     */
    public TeamSelector(final TOWEngineApp engineApp) {
        towEngineApp = engineApp;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param obs the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(final Observable obs, final Object arg) { //NOPMD

        if (obs instanceof EventConsumer && arg instanceof Event) {

            final Event event = (Event) arg;
            //Logger.getInstance().debug("--Event received from Station : " + event.getDebugInfo());
            if (event.getType().equals("TOWInit")) {
                final Event newEvent = new Event();
                newEvent.setType("TOWinit");
                final String mac = event.getDescription();
                final int guardianID = generateID(mac);
                final int color = chooseTeam();
                newEvent.setGuardianID(guardianID);
                newEvent.setDescription(mac + "$" + color + "#" + guardianID);
                Logger.getInstance().debug(newEvent.getDescription());

                try {
                    (towEngineApp.getStationInterface(event.getStation().getIpAddr())).addEvent(newEvent);

                } catch (RemoteException e) {
                    Logger.getInstance().debug(e.toString());
                }

            }

        }

    }


    /**
     * Chooses the team for this device.
     *
     * @return The generated color
     */

    private int chooseTeam() { //NOPMD
        if (team == 0) {
            team = 1;
        } else {
            team = 0;
        }
        return team;
    }


    /**
     * Generates a UID for the device.
     * TODO actual implementation
     *
     * @param mac MAC address as String
     * @return The generated ID
     */
    private int generateID(final String mac) { //NOPMD

        return (int) (IEEEAddress.toLong(mac));


    }

}
