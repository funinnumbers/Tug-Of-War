package eu.funinnumbers.games.sg.tow.guardian;

import eu.funinnumbers.guardian.GenericApp;
import eu.funinnumbers.guardian.GuardianInfo;
import eu.funinnumbers.guardian.ui.gestures.GestureRecogniser;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.games.sg.tow.guardian.actions.InitAction;
import eu.funinnumbers.games.sg.tow.guardian.ui.TOWGestureReporter;

import java.io.IOException;

import eu.funinnumbers.util.Logger;
import eu.funinnumbers.db.model.Guardian;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: May 12, 2010
 * Time: 11:05:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class TOWGN implements GenericApp {

    private GestureRecogniser recogniser;

    public void startGame() {
        Logger.getInstance().debug("TOW App");

        // Register action
        ActionProtocolManager.getInstance().setType(new InitAction());

        try {
            recogniser = new GestureRecogniser(new TOWGestureReporter());
            new Thread(recogniser).start();
        } catch (IOException e) {
            Logger.getInstance().debug("Unable to start GestureRecogniser", e);
        }

        GuardianInfo.getInstance().getGuardian().setID(-1);
        GuardianInfo.getInstance().getGuardian().setLedId(0);
        GuardianInfo.getInstance().getGuardian().setInitPhase(Guardian.INIT_COMPLETE);
        EchoProtocolManager.getInstance().updateBcast();
        EchoProtocolManager.getInstance().enable();
    }

    public void stopGame() {
        LEDManager.getInstance().setOffLEDS(LEDManager.FIRST_LED, LEDManager.LAST_LED);

        //Clear Action Protocol
        ActionProtocolManager.getInstance().removeActions();

        recogniser.killRecognizer();
    }
}
