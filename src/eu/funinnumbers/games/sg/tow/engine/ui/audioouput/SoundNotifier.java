package eu.funinnumbers.games.sg.tow.engine.ui.audioouput;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import eu.funinnumbers.engine.playertracking.PlayersTracker;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class SoundNotifier implements Observer { //NOPMD

    private int numberOfPlayers = 0;
    private int curNumberOfPlayers = 0;
    private InetAddress host;

    public SoundNotifier(final PlayersTracker ptracker) {

        ptracker.addObserver(this);

        try {
            host = InetAddress.getByName("192.168.1.13");
        } catch (final UnknownHostException e) {
            Logger.getInstance().debug(e.getMessage());
        }


    }

    public void update(final Observable obs, final Object arg) { //NOPMD
        Logger.getInstance().debug("***Updating sound notifier");
        if (!(obs instanceof PlayersTracker)) {
            return;
        }
        // Only interested for updates
        if (!(arg instanceof Integer)) {
            return;
        }


        curNumberOfPlayers = (Integer) arg;
        if (curNumberOfPlayers > numberOfPlayers) {
            Logger.getInstance().debug("***Sending to OSC ++");
            sendMessage("/increaseNumberOfPlayers");

        } else if (curNumberOfPlayers < numberOfPlayers) {
            Logger.getInstance().debug("***Sending to OSC --");
            sendMessage("/decreaseNumberOfPlayers");
        }

        // Update number of players.
        numberOfPlayers = curNumberOfPlayers;

    }

    private void sendMessage(final String msg) {
        try {
            final OSCPortOut sender = new OSCPortOut(host, 57120);
            Logger.getInstance().debug("Sending to george " + msg);
            final OSCMessage oscmsg = new OSCMessage(msg);
            sender.send(oscmsg);


        } catch (Exception e) {
            Logger.getInstance().debug("Cannot pop datagram from Queue", e);
        }


    }
}
