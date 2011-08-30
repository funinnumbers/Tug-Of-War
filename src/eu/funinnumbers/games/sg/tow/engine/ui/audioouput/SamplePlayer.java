package eu.funinnumbers.games.sg.tow.engine.ui.audioouput;

import ddf.minim.AudioSample;
import ddf.minim.Minim;

import eu.funinnumbers.engine.playertracking.PlayersTracker;
import eu.funinnumbers.games.sg.tow.engine.ui.visualoutput.Tow;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;

import java.util.Random;

public class SamplePlayer implements Observer { //NOPMD

    private final Tow parent;
    /**
     * Number of sample for gestures (indentical for both teams) .
     */
    private final static int GESTURES_SAMP_NUM = 3;

    /**
     * Number of  start and end game samples.
     */
    private final static int GAME_SAMP_NUM = 1;

    public final static int START_GAME = 0;
    public final static int END_GAME = 1;

    /**
     * Number of sample collection (identical for both teams).
     */
    private final static int COLLECTIONS_NUM = 3;

    /**
     * The root for sample tree.
     */
    private final static String SAMP_ROOT = "src/eu/funinnumbers/games/sg/tow/engine/ui/audioouput/samples/";

    private AudioSample gestureSamples[][][] = new AudioSample[2][COLLECTIONS_NUM][GESTURES_SAMP_NUM]; //NOPMD
    private AudioSample gameSamples[][] = new AudioSample[2][GAME_SAMP_NUM]; //NOPMD
    private int currCollection = 0;

    private Minim minim; //NOPMD

    /**
     * Last received parameter for sample playing.
     */
    private int lastParam = 0;

    private int complexity = 1;

    public SamplePlayer(final Tow parentApplet) { //NOPMD


        parent = parentApplet;
        minim = new Minim(parent);
        PlayersTracker.getInstance().addObserver(this);
        // load samples
        // use statics from TOW
        for (int col = 0; col < COLLECTIONS_NUM; col++) {
            for (int team = 0; team < 2; team++) {
                for (int gest = 0; gest < GESTURES_SAMP_NUM; gest++) {
                    final String filename = SAMP_ROOT + team + "/" + col + "/" + gest + ".wav";
                    Logger.getInstance().debug("Loading file " + filename);
                    gestureSamples[team][col][gest] = minim.loadSample(filename, 2048);
                }
            }
        }
        for (int state = 2; state < 4; state++) {
            for (int sample = 0; sample < GAME_SAMP_NUM; sample++) {
                final String filename = SAMP_ROOT + state + "/" + sample + ".wav";
                Logger.getInstance().debug("Loading file " + filename);
                gameSamples[state - 2][sample] = minim.loadSample(filename, 2048);
            }
        }
    }

    public void playSample(final int param) {   //NOPMD
        // Gesture sound
        if (param > -1 && param < 2) {
            gestureSamples[param][currCollection][(new Random()).nextInt(complexity)].trigger();
            lastParam = param;

            // Game sound
        } else if (param >= 2 && param != lastParam) {
            gameSamples[param - 2][(new Random()).nextInt(GAME_SAMP_NUM)].trigger();
            lastParam = param;

            // Choose next collection for both teams.
            if (param - 2 == START_GAME) {
                currCollection = (new Random().nextInt(COLLECTIONS_NUM));
                Logger.getInstance().debug("Using collection " + currCollection);
            }
        }
    }


    public void setComplexity(final int population) {
        if (population < GESTURES_SAMP_NUM) {
            this.complexity = 1 + (int) Math.ceil(population / 2);
            Logger.getInstance().debug("Complexity set to " + this.complexity);
        }
    }


    public void update(final Observable obs, final Object arg) {
        if (!(obs instanceof PlayersTracker)) {
            return;
        }
        if (!(arg instanceof Integer)) {
            return;
        }
        final int population = (Integer) arg;
        setComplexity(population);

    }
}

