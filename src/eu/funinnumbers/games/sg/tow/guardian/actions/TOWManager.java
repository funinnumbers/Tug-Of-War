package eu.funinnumbers.games.sg.tow.guardian.actions;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import eu.funinnumbers.guardian.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: Oct 22, 2009
 * Time: 3:10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public final class TOWManager {

    /**
     * static instance (ourInstance) initialized as null.
     */
    private static TOWManager ourInstance = null;

    /**
     * Associate Colors(String) with LEDColors.
     */
    private final HashMap colors;

    private int teamColor;

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private TOWManager() {
        colors = new HashMap();
        colors.put("0", LEDColor.RED);
        colors.put("1", LEDColor.GREEN);        
        colors.put("2", LEDColor.BLUE);
        colors.put("3", LEDColor.YELLOW);
    }


    /**
     * TOWManager is loaded on the first execution of TOWManager.getInstance()
     * or the first access to TOWManager.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static TOWManager getInstance() {
        synchronized (TOWManager.class) {
            if (ourInstance == null) {
                ourInstance = new TOWManager();
            }
            return ourInstance;
        }
    }

    public int getTeam() {
        return teamColor;
    }

    public void setTeam(final int teamColor) {
        this.teamColor = teamColor;
    }

    public LEDColor getColor() {
        return (LEDColor) colors.get("" + teamColor);
    }

}
