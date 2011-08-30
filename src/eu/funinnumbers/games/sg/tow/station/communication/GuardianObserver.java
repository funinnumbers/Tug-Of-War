package eu.funinnumbers.games.sg.tow.station.communication;

import com.sun.spot.peripheral.Spot;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.db.model.Station;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.engine.event.EventForwarder;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

/**
 * Sends new events related to echo.
 */
public class GuardianObserver implements Observer { //NOPMD


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

        if (obs instanceof EchoProtocolManager && arg instanceof Guardian) {
            final Guardian guardian = (Guardian) arg;


            if (guardian.getID() == -1 && guardian.isAlive()) {
                Logger.getInstance().debug("init him");
                final Event newGuardiaEvent = new Event();


                //Add eu.funinnumbers.station's information to event
                final Station station = new Station();
                station.setIpAddr(System.getProperty("java.rmi.server.hostname"));
                station.setStationId((int) Spot.getInstance().getRadioPolicyManager().getIEEEAddress());
                newGuardiaEvent.setGuardianID(guardian.getID());
                newGuardiaEvent.setStation(station);
                newGuardiaEvent.setDescription(guardian.getAddress().toString());
                newGuardiaEvent.setType("TOWInit");

                // Send event to Coordinator
                EventForwarder.getInstance().sendEvent(newGuardiaEvent);

                Logger.getInstance().debug(newGuardiaEvent.getDebugInfo());
            }

            if (!guardian.isAlive()) {
                Logger.getInstance().debug("Engine, we lost him " + guardian.getID());

                final Event lostGuardiaEvent = new Event();
                //Add eu.funinnumbers.station's information to event
                final Station station = new Station();
                station.setIpAddr(System.getProperty("java.rmi.server.hostname"));
                station.setStationId((int) Spot.getInstance().getRadioPolicyManager().getIEEEAddress());
                lostGuardiaEvent.setGuardianID(guardian.getID());
                lostGuardiaEvent.setStation(station);
                lostGuardiaEvent.setDescription(guardian.getAddress().toString());
                lostGuardiaEvent.setType("TOWdisinit");

                // Send event to Coordinator
                EventForwarder.getInstance().sendEvent(lostGuardiaEvent);

            }


        }
    }
}
