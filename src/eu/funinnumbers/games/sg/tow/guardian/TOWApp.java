package eu.funinnumbers.games.sg.tow.guardian;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import eu.funinnumbers.games.sg.tow.guardian.actions.InitAction;
import eu.funinnumbers.games.sg.tow.guardian.actions.TOWManager;
import eu.funinnumbers.games.sg.tow.guardian.ui.TOWGestureReporter;
import eu.funinnumbers.guardian.AbstractGuardianApp;
import eu.funinnumbers.guardian.GuardianInfo;
import eu.funinnumbers.guardian.communication.actionprotocol.ActionProtocolManager;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.guardian.ui.gestures.GestureRecogniser;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.util.Logger;

import java.io.IOException;

/**
 * SUN SPOT Application for TOW game.
 */
public class TOWApp extends AbstractGuardianApp { //NOPMD
    /**
     * Sunspot's ISwitch 1.
     */
    private final ISwitch switch1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];

    /**
     * This function is executed when all data are loaded.
     * It does the actual startup of the game.
     */
    public void startGameApp() { //NOPMD

        // Set the TOW Channel
        /*Logger.getInstance().debug("Changing Transmission channel for TOW");
        final IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(15);*/

        // Initialize Guardian Info
        //StorageService.getInstance().clearAll();
        basicInit();

        EchoProtocolManager.getInstance().setMode(false);

        // Register action
        ActionProtocolManager.getInstance().setType(new InitAction());

        try {
            final GestureRecogniser recogniser = new GestureRecogniser(new TOWGestureReporter());
            new Thread(recogniser).start();
        } catch (IOException e) {
            Logger.getInstance().debug("Unable to start GestureRecogniser", e);
        }

        int id = 0; //blue;
        TOWManager.getInstance().setTeam(Integer.parseInt("" + id));

        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());
        GuardianInfo.getInstance().getGuardian().setID(Integer.parseInt("" + id));
        EchoProtocolManager.getInstance().updateBcast();


        while (true) {
            switch1.waitForChange();
            if (switch1.isOpen()) {
                if (id == 0) {
                    id = 1;

                } else if (id == 1) {
                    id = 0;
                }
                TOWManager.getInstance().setTeam(Integer.parseInt("" + id));

                LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());
                GuardianInfo.getInstance().getGuardian().setID(Integer.parseInt("" + id));

                EchoProtocolManager.getInstance().updateBcast();

            }


        }

    }
}
