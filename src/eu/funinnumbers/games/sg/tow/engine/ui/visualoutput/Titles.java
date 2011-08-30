package eu.funinnumbers.games.sg.tow.engine.ui.visualoutput;

import processing.core.PApplet;
import eu.funinnumbers.util.Logger;

import java.util.ArrayList;

/**
 * Generic class for printing out messages with fade in/out effect.
 */
public class Titles { //NOPMD

    /**
     * Number of Active Sides = Territories = Active Players (0..4].
     */
    private static final int SCREENS = 4;

    /**
     * width height analogy.
     */
    private static final int ANALOGY = 3;

    /**
     * Default font name.
     */
    private static final String FONT_NAME = "Verdana";

    private static final int TXT_SIZE = 15 * ANALOGY / 4;

    final ArrayList<TitleInstance> phrase = new ArrayList<TitleInstance>();

    /**
     * mul is used so as to send images to different sides of the cube.
     * offset is used to send images within screen.
     * @param parent
     * @param text
     * @param mulX
     * @param offsetX
     * @param mulY
     * @param offsetY
     * @param tranparency
     */
    public Titles(final PApplet parent,
                  final String[] text,
                  final int mulX,
                  final int offsetX,
                  final int mulY,
                  final int offsetY,
                  final int tranparency) {
        for (int j = 0; j < text.length; j++) {
            phrase.add(new TitleInstance(parent, text[j], j * mulX + offsetX, j * mulY
                    + offsetY, tranparency, 0)); //NOPMD //This is for testing purposes.
        }
    }

    /**
     * state = 0 gia fadeIn, 1 gia display, 2 gia fadeIn kai display.
     * @param startingTime
     * @param startingOffset
     * @param stoppingTime
     * @param stopingOffset
     * @param timeInterval
     * @param keepDisplay
     * @param state
     * @return
     */
    public boolean fadeIn(final int startingTime,
                          final int startingOffset,
                          final int stoppingTime,
                          final int stopingOffset,
                          final int timeInterval,
                          final int keepDisplay,
                          final int state) {    //NOPMD
        boolean rtn = false;
        for (int j = 0; j < phrase.size(); j++) {
            //TitleInstance tli = (TitleInstance) phrase.get(randInt[j]);
            final TitleInstance tli = phrase.get(j);
            if (state == 0) {
                rtn = tli.fadeIn(j * startingTime + startingOffset, (j + 1) * stoppingTime
                        + stopingOffset, timeInterval);
            } else if (state == 1) {
                tli.display();
            } else if (state == 2) {
                rtn = tli.fadeIn(j * startingTime + startingOffset, (j + 1) * stoppingTime
                        + stopingOffset, timeInterval);
                //Auto ginetai giati sto ena screen mporei na exei teleiwsei to effex kai sto allo akomh na trexei
                if ((keepDisplay >= 0)
                        && (tli.timeOn > stoppingTime - keepDisplay)) { //ligo prin teleiwsei to effex.
                    tli.display();
                }
            }
        }
        return rtn;
    }

    /**
     * state = 0 gia display, 1 gia fade, 2 gia display kai fade.
     * @param startingTime
     * @param startingOffset
     * @param stoppingTime
     * @param stopingOffset
     * @param timeInterval
     * @param mulX
     * @param offsetX
     * @param mulY
     * @param offsetY
     * @param color
     * @param state
     * @return
     */
    public boolean fadeOut(final int startingTime,
                           final int startingOffset,
                           final int stoppingTime,
                           final int stopingOffset,
                           final int timeInterval,
                           final int mulX,
                           final int offsetX,
                           final int mulY,
                           final int offsetY,
                           final int color,
                           final int state) { //NOPMD
        boolean rtn = false;
        for (int j = 0; j < phrase.size(); j++) {
            final TitleInstance tli = phrase.get(j); ///FIXED H RANDOM?
            if (state == 0) {
                tli.display();
            } else if (state == 1) {
                rtn = tli.fadeOut((j + 1) * startingTime + startingOffset, j * stoppingTime
                        + stopingOffset, timeInterval, j * mulX + offsetX, j * mulY + offsetY, color);
            } else if (state == 3) {
                tli.display();
                rtn = tli.fadeOut((j + 1) * startingTime + startingOffset, j * stoppingTime
                        + stopingOffset, timeInterval, j * mulX + offsetX, j * mulY + offsetY, color);
            }
        }
        return rtn;

    }

    public void reInitialize() {
        for (Object aPhrase : phrase) {
            final TitleInstance tli = (TitleInstance) aPhrase;
            tli.display();
            tli.timeOn = 0;
            tli.alpha = 0;
        }
    }

    class TitleInstance { //NOPMD
        // Title, Position, Transparency
        PApplet parent;
        String title = "";
        int xPos, yPos, alpha, timeOn;

        TitleInstance(final PApplet parent,
                      final String title,
                      final int thisX,
                      final int thisY,
                      final int alpha,
                      final int timeOn) {
            this.parent = parent;
            this.title = title;
            this.xPos = thisX;
            this.yPos = thisY;
            this.alpha = alpha;
            this.timeOn = timeOn;
        }

        void display() {
            parent.fill(0, alpha);
            parent.textFont(parent.createFont(FONT_NAME, TXT_SIZE), 255);
            //parent.textSize(TXT_SIZE);
            parent.text(title, xPos, yPos);
        }

        /**
         * to timeInterval einai gia to ka8e pote 8a ginetai au3hsh tou alpha kata 10.
         * @param startingTime
         * @param stoppingTime
         * @param timeInterval
         * @return
         */
        boolean fadeIn(final int startingTime,
                       final int stoppingTime,
                       final int timeInterval) {    //NOPMD
            boolean rtn = false;
            parent.fill(255, 5);
            parent.noStroke();
            parent.rect(0, 0, parent.width, parent.height);
            parent.textFont(parent.createFont(FONT_NAME, TXT_SIZE), 255);
            //parent.textSize(TXT_SIZE);
            parent.noStroke();
            if (startingTime < timeOn) {
                if (stoppingTime > timeOn) {
                    parent.fill(0, alpha);
                    parent.text(title, xPos, yPos, 0);
                } else if (stoppingTime == timeOn) {
                    parent.fill(255, 255);
                    parent.text(title, xPos, yPos, 0);
                } else {
                    //mh kaneis tpt akoma.
                    rtn = true;
                }
                if (timeOn % timeInterval == 0 && alpha <= 245) {
                    alpha += 1;
                }
            }
            timeOn++;
            return rtn;
        }

        /**
         * to timeInterval einai gia to ka8e pote 8a ginetai au3hsh tou alpha kata 10.
         * @param startingTime
         * @param stoppingTime
         * @param timeInterval
         * @param xCoord
         * @param yCoord
         * @param color
         * @return
         */
        boolean fadeOut(final int startingTime,
                        final int stoppingTime,
                        final int timeInterval,
                        final int xCoord,
                        final int yCoord,
                        final int color) { //NOPMD
            boolean rtn = false;
            parent.fill(color, alpha);
            parent.noStroke();
            if (startingTime < timeOn) {
                if (stoppingTime > timeOn) {
                    parent.rect(xCoord, yCoord, parent.width / SCREENS, parent.height);
                } else if (stoppingTime == timeOn) {
                    //nothing.
                    Logger.getInstance().debug("Do nothing");
                } else {
                    //mh kaneis tpt akoma.
                    rtn = true;
                }
                if (timeOn % timeInterval == 0 && alpha <= 245) {
                    alpha += 1;
                }
            }

            if (timeOn % timeInterval == 0 && alpha <= 245) {
                alpha += 1;
            }
            timeOn++;
            return rtn;
        }

    }
}
