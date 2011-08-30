package eu.funinnumbers.games.sg.tow.station.communication;

import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.eventconsumer.EventConsumer;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.db.model.Guardian;
import eu.funinnumbers.games.sg.tow.guardian.actions.InitAction;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionProtocolManager;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: May 13, 2010
 * Time: 3:54:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TOWInit implements Observer { //NOPMD

    public void update(final Observable obs, final Object arg) {
        if (!(obs instanceof EventConsumer)) {
            return;
        }

        if (!(arg instanceof Event)) {
            return;
        }

        final Event event = (Event) arg;

        Logger.getInstance().debug(event.getDescription());


        //Print events Description
        Logger.getInstance().debug("Event received from eu.funinnumbers.engine : " + event.getType());

        if (event.getType().equals("TOWinit")) {
            final InitAction iact = new InitAction(); //NOPMD
            ActionProtocolManager.getInstance().setType(iact);
            Logger.getInstance().debug("Init from Station");
            final int endOfMAC = event.getDescription().indexOf("$");
            final int endOfColor = event.getDescription().indexOf("#");
            final int endOfId = event.getDescription().length();


            final Guardian tar = new Guardian();  //NOPMD
            final String mac = event.getDescription().substring(0, endOfMAC);

            final String identity = event.getDescription().substring(endOfMAC + 1, endOfId);

            final String color = event.getDescription().substring(endOfMAC + 1, endOfColor);

            Logger.getInstance().debug(mac + " - " + identity + " - " + color);
            tar.setAddress(mac);
            iact.addTarget(tar);
            iact.setParams(identity + "#" + color);
            iact.actionPerformed();
        }

    }
}