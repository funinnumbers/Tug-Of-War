package eu.funinnumbers.games.sg.tow.engine;

import eu.funinnumbers.engine.AbstractEngineApp;
import eu.funinnumbers.engine.rmi.SGEngineRMIImpl;
import eu.funinnumbers.engine.util.GenericRMIServer;
import eu.funinnumbers.games.sg.tow.engine.ui.TOWFrame;
import eu.funinnumbers.util.Logger;

import java.rmi.RemoteException;

public class TOWEngineApp extends AbstractEngineApp {

    /**
     * Game Specific code.
     */
    public void startApp() {
        Logger.getInstance().debug("Tug Of War Engine Started");

        //Initialize Engine RMI implementation
        try {
            final SGEngineRMIImpl engineImpl = new SGEngineRMIImpl();
            engineImpl.setEngineApp(this);

            //Register RMI Interface
            GenericRMIServer.getInstance().registerInterface(engineImpl.RMI_NAME, engineImpl);

            // Look for HyperEngine and make it known to eu.funinnumbers.engine.
            searchHyperEngine();
            
        } catch (RemoteException e) {
            Logger.getInstance().debug("Unable to register RMI interface", e);
        }
        //EventConsumer.getInstance().addObserver(new TeamSelector(this));

        // Start UI
        new TOWFrame();

        //new SoundNotifier(ptracker);

    }

    /**
     * Main function.
     *
     * @param args String Argument
     */
    public static void main(final String[] args) {
        // Initialize TOW eu.funinnumbers.engine
        new TOWEngineApp();
    }
}
