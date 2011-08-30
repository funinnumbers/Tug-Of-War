package eu.funinnumbers.games.sg.tow.engine.ui.visualoutput;

import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.games.sg.tow.engine.ui.audioouput.SamplePlayer;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import eu.funinnumbers.util.Logger;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;
import eu.funinnumbers.util.eventconsumer.EventConsumer;

import javax.media.opengl.GL;
import java.util.*;

/**
 * Sophisticated Front End for the "Tug Of War" Installation.
 * <p/>
 * Each color mass defines a territory owned by a player. The aim of each player
 * is to expand his territory as much as possible.
 * <p/>
 * Simulation Notes:
 * - The cube is simulated when the value of the SCREENS var is 4.
 */
public class Tow extends PApplet implements Observer { //NOPMD

    /**
     * Version for Serialization.
     */
    public static final long serialVersionUID = 42L; //NOPMD

    /**
     * Step to increase circle size for simple user actions.
     */
    public static final int INC_SIMPLE = 20;

    public static final int IMG_REFRESHRATE = 350;

    public static final int STAGE_REFRESHRATE = 500;

    /**
     * Background image.
     */
    final PImage ICON_BACKGROUND = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/grayScaleBlender.jpg");

    final PImage ICON_NULL = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/null.gif");

    /**
     *
     */
    private final ArrayList<PImage> horFrames = new ArrayList<PImage>();

    /**
     *
     */
    private final ArrayList<PImage> forFrames = new ArrayList<PImage>();

    /**
     *
     */
    private final ArrayList<PImage> verFrames = new ArrayList<PImage>();

    /**
     *
     */
    private final ArrayList<PImage> anyFrames = new ArrayList<PImage>();

    /**
     * Font for printing the counter.
     */
    private final PFont fontCounter = createFont("Verdana", 48);

    /**
     *
     */
    private int curImageFrame = 0;

    /**
     * Image for current gesture of team A.
     */
    private PImage team1gesA;

    /**
     * Image for current gesture of team B.
     */
    private PImage team2gesA;


    /**
     * Frames change timer.
     */
    private final Timer timer = new Timer();

    //-------------------------------------------------

    // Stages Variables.
    private int phase = 1;
    private int kare;
    private int trans = 250;
    private int stage = 0;
    boolean unlockControls = false;

    //-------------------------------------------------
    // Installation Preferences.
    //-------------------------------------------------

    /**
     * Number of Active Sides = Territories = Active Players (0..4].
     */
    private final static int SCREENS = 2;

    /**
     * Number of circles per interctome.
     */
    private final static int CIRCLE_COUNT = 400;

    /**
     * Maximum size of interactome circles.
     */
    private final static float MAX_SIZE = 90;

    /**
     * Minimum size of interactome circles.
     */
    private final static float MIN_SIZE = 5;

    // Team width
    private int teamWidth;

    // Counter settings
    private int[] counterBoxX = new int[2];
    private int counterBoxY;
    private int[] counterBoxWidth = new int[2];
    private static final int BOX_HEIGHT = 55;
    private int[] counterX = new int[2];
    private int counterY;

    private int[] counterValue = new int[2];

    //Main Enviroment Vars.
    private Interactome thisInteractome;

    private boolean changingLevel;

    //Level Variables
    //-------------------------------------------------
    private final Random randomGenerator = new Random();

    /**
     * Array position for BLUE team.
     */
    public static final int BLUE = 0;

    /**
     * Array position for GREEN team.
     */
    public static final int GREEN = 1;

    /**
     * Vector with team actions.
     */
    private final ArrayList<String>[] receivedGestures;

    private final String[] curGesture;

    private final Map<Integer, String> gestMap = new HashMap<Integer, String>();

    private final Map<String, ArrayList<PImage>> imagesForGestures = new HashMap<String, ArrayList<PImage>>();

    private int stage1Team1width;
    private int stage1Team2width;

    private int stage1height;
    private SamplePlayer splayer; //NOPMD

    //-------------------------------------------------
    Titles starting, ending;
    int[] randInt = new int[SCREENS];

    /**
     * Default constructor.
     */
    @SuppressWarnings("unchecked")
    public Tow() {
        curGesture = new String[2];
        receivedGestures = new ArrayList[2];
        splayer = new SamplePlayer(this);
    }

    /**
     * Setup processing.
     */
    public void setup() { //NOPMD

        receivedGestures[BLUE] = new ArrayList<String>();
        receivedGestures[GREEN] = new ArrayList<String>();

        // Create Enviroment.
        size(screen.width - 1, screen.height - 1, OPENGL);
        textMode(MODEL);
        background(255);
        noStroke();
        smooth();
        createRandomness();

        thisInteractome = new Interactome(this, MAX_SIZE, MIN_SIZE, CIRCLE_COUNT, SCREENS, splayer);
        initializeTitles(); //starting, ending
        kare = 0;
        trans = 0;

        //Initialize gestures Map.
        gestMap.put(0, "TOWhor");
        gestMap.put(1, "TOWver");
        gestMap.put(2, "TOWfor");

        for (int i = 1; i < 13; i++) {
            final PImage tmpImage = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/TOWhor_" + i + ".gif");
            tmpImage.resize(tmpImage.width / 4, tmpImage.height / 4);
            horFrames.add(tmpImage);
        }

        for (int i = 1; i < 16; i++) {
            final PImage tmpImage = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/TOWver_" + i + ".gif");
            tmpImage.resize(tmpImage.width / 4, tmpImage.height / 4);
            verFrames.add(tmpImage);
        }

        for (int i = 1; i < 8; i++) {
            final PImage tmpImage = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/TOWfor_" + i + ".gif");
            tmpImage.resize(tmpImage.width / 4, tmpImage.height / 4);
            forFrames.add(tmpImage);
        }

        for (int i = 0; i < 6; i++) {
            PImage tmpImage = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/any.gif");
            tmpImage.resize(tmpImage.width / 2, tmpImage.height / 2);
            anyFrames.add(tmpImage);

            tmpImage = loadImage("eu/funinnumbers/games/sg/tow/engine/ui/visualoutput/img/null.gif");
            tmpImage.resize(tmpImage.width / 2, tmpImage.height / 2);
            anyFrames.add(tmpImage);
        }

        imagesForGestures.put("TOWhor", horFrames);
        imagesForGestures.put("TOWver", verFrames);
        imagesForGestures.put("TOWfor", forFrames);

        team1gesA = horFrames.get(0);
        team2gesA = horFrames.get(0);

        stage1Team1width = width / 4 - team1gesA.width / 2;
        stage1Team2width = 3 * width / 4 - team2gesA.width / 2;

        stage1height = height - team1gesA.height;

        //fillImageVectors();
        timer.scheduleAtFixedRate(new ChangeCurrentImages(), 2000, IMG_REFRESHRATE);
        timer.scheduleAtFixedRate(new CheckStage(), 4000, STAGE_REFRESHRATE);

        // Set the default font
        textFont(fontCounter, 255);
        textSize(48);

        // Setup Counters
        teamWidth = width / SCREENS;

        // Counter settings
        counterBoxX[0] = 100 - 5;
        counterBoxX[1] = width - 100 - 5;
        counterBoxY = height - 95;
        counterX[0] = 100;
        counterX[1] = width - 100;
        counterY = height - 50;

    }

    public void draw() { //NOPMD

        switch (phase) {
            case 0:// Show Starting SlidingBar.
                Logger.getInstance().debug("Case 0");
                if (starting.fadeIn(100, 0, 100, 0, 5, 0, 0)) {
                    phase = 1;
                }
                textFont(fontCounter, 255);
                textSize(48);
                break;

            case 1:// Show Transition Effect.
                splayer.playSample(SamplePlayer.START_GAME + 2);

                if (createTransition(kare)) {
                    phase = 2;
                    changingLevel = false;
                    kare = 0;
                    trans = 250;
                }

                kare += 1;
                textFont(fontCounter, 255);
                textSize(48);
                break;

            case 2:// Show Actual Game Environment.
                if (redrawMorphome()) {
                    counterValue[BLUE] = 0;
                    counterValue[GREEN] = 0;
                    phase = 3;
                    changingLevel = true;
                }

                if (!changingLevel) {
                    // draw gestures for both teams
                    drawGestures();

                    // draw gesture counter
                    drawCounters();
                }

                break;

            case 3:// Make Everything White/Serene/Tranquil/Peaceful.
                eraser(255, 20);
                glow();
                unlockControls = false;
                if (thisInteractome.serene()) {
                    phase = 4;
                }

                break;

            case 4:// Erase Everything for once.
                eraser(255, 255);
                phase = 5;
                break;

            case 5:// Show Ending SlidingBar. For Instance "http://finn.cti.gr"
                phase = 6;
                ending.fadeIn(50, 0, 10, (SCREENS - 1) * 50, 10, 10, 2);
                ending.reInitialize();
                break;

            case 6:// Uber Black Box.
                phase = 7;
                ending.fadeOut(50, 0, 0, 300, 30, teamWidth, 0, 0, 0, color(0, 0, 0), 1);
                ending.reInitialize();
                break;

            case 7:// Enwhiten.
                phase = 1;
                ending.fadeOut(25, 0, 0, 200, 10, teamWidth, 0, 0, 0, color(255, 255, 255), 1);
                createRandomness();
                thisInteractome = new Interactome(this, MAX_SIZE, MIN_SIZE, CIRCLE_COUNT, SCREENS, splayer);
                //initializeTitles(); //starting, ending
                kare = 0;
                trans = 0;
                break;

            default:
                break;
        }
    }

    private void drawCounters() {
        // Write counter
        fill(0, 0, 0, 255);
        for (int i = 0; i < 2; i++) {
            rect(counterBoxX[i], counterBoxY, counterBoxWidth[i], BOX_HEIGHT);
        }

        fill(255, 255, 255, 255);
        for (int i = 0; i < 2; i++) {
            text(counterValue[i], counterX[i], counterY);
        }
    }

    private boolean redrawMorphome() {

        eraser(0, trans); // Fade Trick.
        if (trans > 25) {
            trans -= 25;
        }
        if (kare >= 120) {
            // glow();
            unlockControls = true;
        } // OpenGL Trick.
        kare++;

        return thisInteractome.display();
    }

    private void drawGestures() {

        try {
            switch (stage) {
                case 0:
                    // Draw gesture for team A
                    image(team1gesA, stage1Team1width, stage1height);

                    // Draw gesture for team B
                    image(team2gesA, stage1Team2width, stage1height);
                    break;

                case 1:
                    // Draw gesture for team A
                    image(team1gesA, stage1Team1width, stage1height);

                    // Draw gesture for team B
                    image(team2gesA, stage1Team2width, stage1height);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Logger.getInstance().debug("No imageIoann   Ioa?");
        }
    }

    /**
     * full screen erase.
     *
     * @param color to use when clearing the screen.
     * @param alpha to use when clearing the screen.
     */
    void eraser(final int color, final int alpha) {
        fill(color, alpha);
        noStroke();
        rect(0, 0, width, height);
    }

    // OpenGL Trick.

    void glow() {
        final PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
        final GL openGL = pgl.beginGL();
        openGL.glDisable(GL.GL_DEPTH_TEST);
        openGL.glEnable(GL.GL_BLEND);
        openGL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        pgl.endGL();
    }

    public void setChangingLevel(final boolean changingLevel) {
        this.changingLevel = changingLevel;
    }

    // Simulation.

    public void keyPressed() { //NOPMD
        if ((phase != 2) || (changingLevel)) {
            return;
        }

        if ((key == 'b') || (key == 'B')) {
            final int whoAmI = BLUE;
            counterValue[whoAmI]++;
            final boolean check = thisInteractome.terra(whoAmI, INC_SIMPLE + (counterValue[whoAmI] / 5));
            fixCounterBoxWidth(whoAmI);
            splayer.playSample(whoAmI);
            if (check) {
                Logger.getInstance().debug("Found winner");
            }

        } else if ((key == 'g') || (key == 'G')) {
            final int whoAmI = GREEN;
            counterValue[whoAmI]++;
            final boolean check = thisInteractome.terra(whoAmI, INC_SIMPLE + (counterValue[whoAmI] / 5));
            fixCounterBoxWidth(whoAmI);
            splayer.playSample(whoAmI);
            if (check) {
                Logger.getInstance().debug("Found winner");
            }
        }
    }

    // Gray-Scale Happy Tree Friends Effect.

    boolean createTransition(final int frames) { //NOPMD
        boolean rtn = false;
        fill(0, 1);
        noStroke();
        rect(0, 0, width, height);
        if (trans <= 250) {
            for (int i = 0; i < 100; i++) {
                final int xPos = (int) (random(ICON_BACKGROUND.width));
                final int Pos = (int) (random(ICON_BACKGROUND.height));
                //int pix = (int)map(a.get(posX, posY), 0, 255, 0, 255-alpha);
                final int pix = ICON_BACKGROUND.get(xPos, Pos);
                fill(pix, 10);
                final int randx = (int) (random(width));
                final int randy = (int) (random(height));
                ellipse(randx, randy, i, i);
                fill(0, trans);
                ellipse(randx, randy, i, i);
            }
            if (trans <= 10) {    //1
                if (frames % 4 == 0) {
                    trans += 1;
                }
            } else if (trans >= 200) {
                trans = 255;
            } else {
                trans += 1;
            }
        } else {
            fill(0, 255);
            noStroke();
            rect(0, 0, width, height);
            rtn = true;
        }
        return rtn;
    }

    // Creating Random Numbers for SlidingBar in Each Game Instance.    

    void createRandomness() {
        for (int j = 0; j < SCREENS; j++) {
            randInt[j] = (int) random(0, 30);
        }
    }

    // HardCoded Starting SlidingBar. Feel Free to Add of Modify.

    void initializeTitles() {
        String[] quotes = new String[31];
        quotes[0] = "Liquifying Gravity...";
        quotes[1] = "Sandbagging Expectations...";
        quotes[2] = "Requesting Funds...";
        quotes[4] = "Challenging Immortals...";
        quotes[5] = "Loading Emotions...";
        quotes[6] = "Homogenizing Objects...";
        quotes[7] = "Resolving Dependencies...";
        quotes[8] = "Firing Up Perfection...";
        quotes[9] = "Blending Memories...";
        quotes[10] = "Writting Last Lines of Code...";
        quotes[11] = "Giving Life to Morphomes...";
        quotes[12] = "Eating Breakfast...";
        quotes[13] = "Picking Up Lucy...";
        quotes[14] = "Fixing Bugs...";
        quotes[15] = "Normalizing Geometry...";
        quotes[16] = "Bending Space...";
        quotes[17] = "Importing Oxygen...";
        quotes[18] = "Outputing Thoughts...";
        quotes[19] = "Channelling Ideas...";
        quotes[20] = "Buffering Prospects...";
        quotes[21] = "Mixing Ingredients...";
        quotes[22] = "Calculating Jetlag...";
        quotes[23] = "Sculpturing World...";
        quotes[24] = "Restructuring Time...";
        quotes[25] = "Redefining Games...";
        quotes[26] = "Expecting the unexpected...";
        quotes[27] = "Ha!";
        quotes[28] = "Fatal Error...";
        quotes[29] = "Increasing temperature on 0.II.7";
        quotes[30] = "Oops, I did it again...";

        //Choose 3 for all the setup.
        final String[] selected = new String[SCREENS];
        for (int j = 0; j < SCREENS; j++) {
            selected[j] = quotes[randInt[j]];
        }
        starting = new Titles(this, selected, 0, width - 400, 0, height - 100, 5);

        quotes = new String[SCREENS];
        for (int j = 0; j < SCREENS; j++) {
            quotes[j] = "http://finn.cti.gr";
        }
        ending = new Titles(this, quotes, width / SCREENS, 20, 0, height / 2, 2);
    }

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
        // Only interested to updates received from TOW Manager
        if (!(obs instanceof EventConsumer)) {
            return;
        }

        // Only interested for updates conveing an Event
        if (!(arg instanceof Event)) {
            return;
        }

        final Event event = (Event) arg;

        if (!event.getType().equals("TOW")) {
            // ignore it
            return;
        }

        // Extract team
        final int team = event.getGuardianID();
        if (team < 0 || team > 1) {
            Logger.getInstance().debug("Illegal");
            return;
        }

        Logger.getInstance().debug("Team " + event.getGuardianID() + event.getDescription());
        switch (stage) {

            case 0:
                if ((phase != 2) || (changingLevel)) {
                    // Accept gesture only when we are playing a stage
                    return;
                }
                Logger.getInstance().debug(" \n\n\t\tWELL DONE! team " + team);
                counterValue[team]++;
                fixCounterBoxWidth(team);
                thisInteractome.terra(team, INC_SIMPLE + (counterValue[team] / 10));
                // TODO play sample correct gesture
                splayer.playSample(team);
                break;

            // Gesture per team
            case 1:
                if ((phase != 2) || (changingLevel)) {
                    // Accept gesture only when we are playing a stage
                    return;
                }

                // check if gesture is correct
                if (event.getDescription().equals(curGesture[team])) {
                    Logger.getInstance().debug(" \n\n\t\tWELL DONE! team " + team);
                    thisInteractome.terra(team, INC_SIMPLE + (counterValue[team] / 10));
                    counterValue[team]++;
                    fixCounterBoxWidth(team);
                    // TODO play sample correct gesture
                    splayer.playSample(team);
                }

                break;

            default:
                break;
        }
    }

    private void fixCounterBoxWidth(final int team) {
        // Decide counter box size depending on counter value
        if (counterValue[team] < 10) {
            counterBoxWidth[team] = 45;

        } else if (counterValue[team] < 100) {
            counterBoxWidth[team] = 80;

        } else {
            counterBoxWidth[team] = 125;
        }
    }

    final class ChangeCurrentImages extends TimerTask { //NOPMD

        /**
         *
         */
        public void run() { //NOPMD

            try {
                synchronized (team1gesA) {
                    switch (stage) {
                        case 0:
                            team1gesA = team2gesA = anyFrames.get(curImageFrame);
                            break;

                        case 1:
                            // Only check curBlueGes and curGreenGes
                            team1gesA = imagesForGestures.get(curGesture[BLUE]).get(curImageFrame);
                            team2gesA = imagesForGestures.get(curGesture[GREEN]).get(curImageFrame);
                            break;

                        default:
                            break;
                    }
                }

                if (curImageFrame >= 6) {
                    curImageFrame = -1;
                }
                curImageFrame++;

            } catch (Exception e) {
                Logger.getInstance().debug("No gesture?");
            }
        }
    }

    final class CheckStage extends TimerTask { //NOPMD

        private int counter = 0;

        private boolean firstTimeInBonus = true;
        private int stageDuration = 5;

        public void run() { //NOPMD
            counter++;
            switch (stage) {

                case 0:
                    if (firstTimeInBonus) {
                        counter = 0;
                        firstTimeInBonus = false;
                        stageDuration = 5;
                        Logger.getInstance().debug("\t\t---Level 0---");

                    } else if (counter > stageDuration) {
                        Logger.getInstance().debug("\t\t--End of level 0--");

                        // Random next stage
                        stage = randomGenerator.nextInt(3);
                        // give more changes to level 1
                        if (stage > 1) {
                            stage = 1;
                        }

                        firstTimeInBonus = true;
                    }
                    break;

                case 1:
                    if (firstTimeInBonus) {
                        counter = 0;
                        firstTimeInBonus = false;
                        stageDuration = 5;
                        Logger.getInstance().debug("\t\t---Level 1---");

                        // Generate gesture for blue team
                        curGesture[BLUE] = gestMap.get(randomGenerator.nextInt(gestMap.size()));
                        Logger.getInstance().debug("Blue Current Gesture: " + curGesture[BLUE]);

                        // Generate gesture for green team
                        curGesture[GREEN] = gestMap.get(randomGenerator.nextInt(gestMap.size()));
                        Logger.getInstance().debug("Green Current Gesture: " + curGesture[GREEN]);

                    } else if (counter > stageDuration) {
                        Logger.getInstance().debug("\t\t--End of level 1--");

                        // Random next stage
                        stage = randomGenerator.nextInt(3);

                        // give more changes to level 1
                        if (stage > 1) {
                            stage = 1;
                        }
                        firstTimeInBonus = true;
                        curGesture[BLUE] = null;
                        curGesture[GREEN] = null;
                        resetCurrentFrames();
                    }
                    break;

                default:
                    break;
            }
        }

        private void resetCurrentFrames() {
            team1gesA = ICON_NULL;
            team2gesA = ICON_NULL;
        }
    }

}
