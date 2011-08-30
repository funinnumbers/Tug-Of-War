package eu.funinnumbers.games.sg.tow.station;

import eu.funinnumbers.engine.event.EventForwarder;
import eu.funinnumbers.engine.rmi.SGEngineInterface;
import eu.funinnumbers.games.sg.tow.station.communication.GuardianObserver;
import eu.funinnumbers.guardian.communication.echoprotocol.EchoProtocolManager;
import eu.funinnumbers.station.AbstractStationApp;
import eu.funinnumbers.station.communication.dtservice.DTSManager;
import eu.funinnumbers.station.communication.dtservice.SGEventProcessor;
import eu.funinnumbers.station.rmi.StationRMIImpl;
import eu.funinnumbers.util.Logger;

import java.rmi.Naming;


/**
 * Tug of War Station Application.
 */
public class TOWStationApp extends AbstractStationApp {

    /**
     * The eu.funinnumbers.engine interface.
     */
    protected SGEngineInterface engine;

    /**
     * Game Specific application code.
     */
    public void stationApp() {
        /* Set the TOW Channel
        Logger.getInstance().debug("Changing Transmission channel for TOW");
        final IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setChannelNumber(15);*/

        try {
            //Lookup Engines Interface
            engine = (SGEngineInterface) Naming.lookup("rmi://" + getEngineIP() + "/" + SGEngineInterface.RMI_NAME);            

            //Register Station to Engine
            engine.registerStation(getMyIP(), new StationRMIImpl());

            // Add new observer to DTS Manager
            DTSManager.getInstance().addObserver(new SGEventProcessor(engine));

            //Set Echo to Station mode
            EchoProtocolManager.getInstance().setMode(true);
            EchoProtocolManager.getInstance().setStationID(5);

            // Create Gurardian Observer object as observer to EchoProtocol
            EchoProtocolManager.getInstance().addObserver(new GuardianObserver());

            // Send event to Coordinator
            EventForwarder.getInstance().setEngineInterface(engine);

            //EventConsumer.getInstance().addObserver(new TOWInit());

        } catch (Exception e) {
            Logger.getInstance().debug(e.toString());

        }
    }

    /**
     * Starts the execution of the Station.
     *
     * @param argc command line arguments
     */
    public static void main(final String[] argc) {
        //Create TOW eu.funinnumbers.station's Object
        new TOWStationApp();
    }
}
