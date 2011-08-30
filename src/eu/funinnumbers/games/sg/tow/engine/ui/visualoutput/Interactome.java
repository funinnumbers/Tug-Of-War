package eu.funinnumbers.games.sg.tow.engine.ui.visualoutput;

import eu.funinnumbers.games.sg.tow.engine.ui.audioouput.SamplePlayer;
import eu.funinnumbers.util.Logger;

import java.util.ArrayList;

/**
 * Controls the color mass of each player team.
 */
public class Interactome { //NOPMD

    /**
     * Left side.
     */
    public final static int LEFT = 0;

    /**
     * Right side.
     */
    public final static int RIGHT = 1;

    /**
     * Parent applet.
     */
    private final Tow parent;

    /**
     * List of circles per side.
     */
    private final ArrayList<Circle>[] mole;

    /**
     * Number of screens.
     */
    private final int screens;

    /**
     * Number of circles with finalized status.
     */
    private int counter = 0;
    private SamplePlayer splayer; //NOPMD

    /**
     * Default constructor.
     *
     * @param parent     the parent applet.
     * @param max        maximum radius size.
     * @param min        minimum radius size.
     * @param complexity total number of circles per screen used to form the Interactome.
     * @param screens    number of different screens the Interactome covers.
     */
    @SuppressWarnings("unchecked")
    public Interactome(final Tow parent,
                       final float max,
                       final float min,
                       final int complexity,
                       final int screens,
                       final SamplePlayer splayer) {

        this.screens = screens;
        this.parent = parent;
        this.splayer = splayer;

        // Setup moles
        mole = new ArrayList[screens];

        // Randomness Variables, Same (Color Scheme) for Each Side/Territory.
        final int[][] rSeed = new int[4][3];

        // Fill the "seed"
        // Side 1 -- Internal Color (Reddish)
        rSeed[0][0] = 255;
        rSeed[0][1] = 155;
        rSeed[0][2] = 55;

        // Side 2 -- Internal Color (Greenish)
        rSeed[1][0] = 200;
        rSeed[1][1] = 255;
        rSeed[1][2] = 55;

        // Side 1 -- External Color (Red)
        rSeed[2][0] = 255;
        rSeed[2][1] = 55;
        rSeed[2][2] = 155;

        // Side 2 -- External Color (Yellow)
        rSeed[3][0] = 255;
        rSeed[3][1] = 255;
        rSeed[3][2] = 0;


        // Grab a Different Color Scheme and Create the Cube Territories.
        for (int surface = LEFT; surface <= RIGHT; surface++) {
            mole[surface] = new ArrayList<Circle>(); //NOPMD

            for (int i = 0; i < complexity; i++) {
                final int colorInt = this.parent.color(this.parent.random(rSeed[surface][0]),
                        this.parent.random(rSeed[surface][1]),
                        this.parent.random(rSeed[surface][2]), 80);

                final int colorExt = this.parent.color(rSeed[surface + 2][0],
                        rSeed[surface + 2][1],
                        rSeed[surface + 2][2], 50);

                mole[surface].add(new Circle(this.parent,
                        screens,
                        this.parent.random(this.parent.width),
                        this.parent.random(this.parent.height),
                        this.parent.random(max - min) + min,
                        colorInt,
                        colorExt,
                        surface)); //NOPMD
            }
        }
    }

    /**
     * Display the circles of the interactome.
     *
     * @return true if one side of the interactome has won.
     */
    public boolean display() { //NOPMD
        boolean rtn = false;
        // Do the Stuff.
        for (int surface = 0; surface < screens; surface++) {
            for (final Circle circle : mole[surface]) {
                circle.move();
                circle.display();
                // In case we have a winner, do the Supernova thingie.
                if (circle.getStatus() == Circle.STATUS_WINNER || circle.getStatus() == Circle.STATUS_CHANGING) {
                    rtn = circle.blow(10);
                }
            }
        }
        return rtn;
    }

    public boolean serene() { //NOPMD
        boolean rtn = false;

        for (int surface = 0; surface < screens; surface++) {
            for (final Circle circle : mole[surface]) {
                circle.move();
                circle.display();
                if (circle.getRadius() <= parent.height) {
                    circle.setRadius(circle.getRadius() * 1.2f);
                } else {
                    counter++;
                }

                if (counter > mole[surface].size()) {
                    counter = 0;
                    rtn = true;
                }
            }
        }
        return rtn;
    }

    // Expand Territory.

    public boolean terra(final int myPos, final int via) { //NOPMD
        int statusExpander = 0;
        int statusShrinker = 0;

        switch (myPos) {

            case LEFT:
                // Expand left player
                for (final Circle circle : mole[LEFT]) {
                    circle.expandRight(via);
                    statusExpander += circle.getStatus();
                }

                // Shrink Right player
                for (final Circle circle : mole[RIGHT]) {
                    circle.shrinkLeft(via);
                    statusShrinker += circle.getStatus();
                }
                break;

            case RIGHT:
                // Expand right player
                for (final Circle circle : mole[RIGHT]) {
                    circle.expandLeft(via);
                    statusExpander += circle.getStatus();
                }

                // Shrink LEft player
                for (final Circle circle : mole[LEFT]) {
                    circle.shrinkRight(via);
                    statusShrinker += circle.getStatus();
                }
        }

        if (statusExpander + statusShrinker != Circle.STATUS_NORMAL) {
            Logger.getInstance().debug("\t\t\t ENDING GAME");
            splayer.playSample(SamplePlayer.END_GAME + 2);
            parent.setChangingLevel(true);


            // declare winners
            for (final Circle circle : mole[myPos]) {
                circle.setStatus(Circle.STATUS_WINNER);
            }

            // declare losers
            for (final Circle circle : mole[getRight(myPos)]) {
                circle.setStatus(Circle.STATUS_LOSER);
            }
        }

        return (statusExpander + statusShrinker != Circle.STATUS_NORMAL);
    }

    // Get the 1st Right Neighbour that is not Dead.

    public int getRight(final int myPos) {
        if (myPos == LEFT) {
            return RIGHT;
        }

        return LEFT;
        /*
        int side = 99;
        for (int surface = myPos + 1; surface < myPos + screens; surface++) {
            for (final Circle circle : mole[surface]) {
                if ((circle.status == 0) && (circle.side == surface % screens)) {
                    side = circle.side;
                }
            }
        }
        return side;
        */
    }

    // Get the 1st Left Neighbour that is not Dead.

    public int getLeft(final int myPos) {
        if (myPos == LEFT) {
            return RIGHT;
        }

        return LEFT;
        /*
        int side = 99;
        for (int surface = myPos + screens; surface > myPos; surface--) {
            for (final Circle circle : mole[surface]) {
                if ((circle.status == 0) && (circle.side == surface % screens)) {
                    side = circle.side;
                }
            }
        }
        return side;
        */
    }
}

