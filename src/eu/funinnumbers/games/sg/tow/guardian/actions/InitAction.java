package eu.funinnumbers.games.sg.tow.guardian.actions;


import eu.funinnumbers.guardian.GuardianInfo;
import eu.funinnumbers.guardian.ui.misc.LEDManager;
import eu.funinnumbers.guardian.communication.actionprotocol.AbstractGuardianAction;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.util.Logger;

/**
 * Used to pass and receive a HotPotato.
 */
public class InitAction extends AbstractGuardianAction {

    /**
     * Action Type identifier.
     */
    public static final int ACTION_TYPE = 211;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getInstance();

    /**
     * Returns the type of the action -- a unique
     * identity for this action (type).
     *
     * @return an identity for the type of this action
     */
    public final int getType() {
        return ACTION_TYPE;
    }

    /**
     * Checks if this action will be performed or not based on local rules.
     *
     * @return True if the action must be performed
     */
    protected final boolean checkAction() {
        return true;
    }

    /**
     * Local results of the PotatoAction.
     * Upon success Potato passage, the local Potato set to be inactive
     */
    protected final void doPartA() {
        LOGGER.debug("part A of the action");
    }


    /**
     * Remote result of the PotatoAction.
     * Invoked when a Potato is received
     */
    protected final void doPartB() {
        //LEDManager.getInstance().blinkAllLED(LEDColor.ORANGE, 1000, 3);
        LOGGER.debug("part B of the dummy action " + this.getParams());

        final int endOfId = this.getParams().indexOf("#");

        final String guardianId = this.getParams().substring(0, endOfId);

        LOGGER.debug("Guardian ID = " + guardianId);

        TOWManager.getInstance().setTeam(Integer.parseInt(guardianId));

        LEDManager.getInstance().setOnLEDS(0, 7, TOWManager.getInstance().getColor());
        GuardianInfo.getInstance().getGuardian().setID(Integer.parseInt(guardianId));

        EchoProtocolManager.getInstance().updateBcast();
    }
}

