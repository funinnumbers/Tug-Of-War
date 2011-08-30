package eu.funinnumbers.games.sg.tow.engine.ui.visualoutput;

import processing.core.PApplet;

/**
 * A single circle of the Interactome.
 */
public class Circle { //NOPMD

    private final PApplet parent;

    // Positions, Radius, Velocities and Territory Borders.
    private float posX, posY, radius, xVelocity, yVelocity, minwidth, maxwidth;
    private int status; // 0=normal, 1=looser, 2=winner.
    private int colorInt, colorExt, side; //NOPMD // circle color, side.
    private int screens; //NOPMD
    private final static float SPEED = 8;

    /**
     * Minimum player size in pixels.
     */
    public final int minBorder;

    /**
     * Player is still active.
     */
    public final static int STATUS_NORMAL = 0;

    /**
     * Player has lost.
     */
    public final static int STATUS_LOSER = 1;

    /**
     * Player has won.
     */
    public final static int STATUS_WINNER = 2;

    /**
     * Circle is changing level.
     */
    public final static int STATUS_CHANGING = 5;

    /**
     * Default constructor.
     *
     * @param parent
     * @param screens
     * @param xPos
     * @param yPos
     * @param radius
     * @param color
     * @param side
     */
    Circle(final PApplet parent,
           final int screens,
           final float xPos,
           final float yPos,
           final float radius,
           final int color,
           final int colorExt,
           final int side) {

        this.parent = parent;
        this.screens = screens;
        this.posX = xPos;
        this.posY = yPos;
        this.radius = radius;
        this.colorInt = color;
        this.colorExt = colorExt;
        xVelocity = this.parent.random(-SPEED, SPEED);
        yVelocity = this.parent.random(-SPEED, SPEED);
        this.side = side;
        status = STATUS_NORMAL;

        minBorder = (int) (parent.width * 0.18);

        // decided Min/max for generic screens and sides
        minwidth = this.side * this.parent.width / this.screens - 10;
        maxwidth = (this.side + 1) * this.parent.width / this.screens + 10;

        // Decide borders for 2 sides only and ignore screens
        switch (side) {
            case Interactome.LEFT:
                minwidth = 0;
                maxwidth = this.parent.width / 2 - 10;
                break;

            case Interactome.RIGHT:
                minwidth = this.parent.width / 2 + 10;
                maxwidth = this.parent.width;
                break;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(final float radius) {
        this.radius = radius;
    }

    // Draw Circles.

    void display() {
        parent.fill(colorInt, 50);
        parent.ellipse(posX, posY, radius * 1.5f, radius * 1.5f);
        parent.fill(colorExt, 150);
        parent.ellipse(posX, posY, radius, radius);
    }

    /**
     * Right player shrings from left side.
     *
     * @param ratio shrink speed.
     */
    void shrinkLeft(final int ratio) {
        if (minwidth < parent.width - minBorder) {
            minwidth += 2 * ratio;
            radius -= ratio * radius / (maxwidth - minwidth);

        } else {
            status = STATUS_LOSER;
        }
    }

    /**
     * Right player expands towards left side.
     *
     * @param ratio expansion speed.
     */
    void expandLeft(final int ratio) {
        if (minwidth >= minBorder) {
            minwidth -= 2 * ratio;
            radius += ratio * radius / (maxwidth - minwidth);

        } else {
            status = STATUS_WINNER;
        }
    }

    /**
     * Left player shrinks from right side.
     *
     * @param ratio shrink speed.
     */
    void shrinkRight(final int ratio) {
        if (maxwidth > minBorder) {
            maxwidth -= 2 * ratio;
            radius -= ratio * radius / (maxwidth - minwidth);

        } else {
            status = STATUS_LOSER;
        }
    }

    /**
     * Left player expands towards right side.
     *
     * @param ratio expansion speed.
     */
    void expandRight(final int ratio) {
        if (maxwidth < parent.width + minBorder) {
            maxwidth += 2 * ratio;
            radius += ratio * radius / (maxwidth - minwidth);

        } else {
            status = STATUS_WINNER;
        }

    }

    /**
     * Blow circle to cover all screen and then continue with white color.
     *
     * @param ratio speed of blow.
     * @return true if blow has concluded.
     */
    boolean blow(final int ratio) { //NOPMD
        boolean rtn = false;
        switch (status) {
            case STATUS_CHANGING:
                if ((maxwidth - minwidth) < parent.width) {
                    maxwidth += ratio;
                    minwidth -= ratio;
                    radius -= ratio * radius / (maxwidth - minwidth) / 5;
                } else {
                    rtn = true;
                }
                break;

            case STATUS_WINNER:
                if ((maxwidth - minwidth) > 0) {
                    maxwidth -= ratio;
                    minwidth += ratio;
                    radius -= ratio * radius / (maxwidth - minwidth) / 2;
                } else {
                    colorInt = parent.color(parent.random(255), parent.random(255), parent.random(255), 20);
                    status = STATUS_CHANGING;
                }
                break;
        }
        return rtn;
    }

    /**
     * Move the circle.
     */
    void move() { //NOPMD
        // bounce Against The Sides.
        if (posX - radius < minwidth) {         //min width
            posX = minwidth + radius + 1;
            xVelocity *= -1;

        } else if (posX + radius > maxwidth) {   //max width
            posX = maxwidth - radius - 1;
            xVelocity *= -1;
        }

        if (posY - radius < 0) {                //min height
            posY = radius + 1;
            yVelocity *= -1;

        } else if (posY + radius > parent.height) {     //max height
            posY = parent.height - radius - 1;
            yVelocity *= -1;
        }

        // add velocities to position
        posX += xVelocity;
        posY += yVelocity;
    }

}
