package ch.bosin.svgjig4j;

import org.la4j.Vector;

import java.util.ArrayList;
import java.util.Random;

public class IndexBased {

    public Vector[][] vec;
    public int width, height;
    public int piecesX, piecesY;

    /**
     * Constructs an index based jigsaw puzzle
     * @param width width of jigsaw puzzle in pixels
     * @param height height of jigsaw puzzle in pixels
     * @param piecesX horizontal amount of pieces
     * @param piecesY vertical amount of pieces
     */
    public IndexBased(int width, int height, int piecesX, int piecesY) {
        this.vec = new Vector[piecesX + 1][piecesY + 1];

        this.width = width;
        this.height = height;

        this.piecesX = piecesX;
        this.piecesY = piecesY;
    }

    /**
     * Generates outer-most points' coordinates with randomization multiplier.
     * @param randomize
     */
    public void outerGrid(double randomize) {
        // Upper left corner
        this.vec[0][0] = Vector.fromArray(new double[] {0D, 0D});
        // Upper right corner
        this.vec[piecesX][0] = Vector.fromArray(new double[] {this.width, 0D});
        // Lower left corner
        this.vec[0][piecesY] = Vector.fromArray(new double[] {0D, this.height});
        // Lower right corner
        this.vec[piecesX][piecesY] = Vector.fromArray(new double[] {this.width, this.height});

        // Top row
        this.midPoints(0, 0, this.piecesX, 0, randomize, 0D);
        // Bottom row
        this.midPoints(0, this.piecesY, this.piecesX, this.piecesY, randomize, 0D);
        // Left row
        this.midPoints(0, 0, 0, this.piecesY, 0D, randomize);
        // Right row
        this.midPoints(this.piecesX, 0, this.piecesX, this.piecesY, 0D, randomize);
    }

    /**
     * Generates a new line of mid-points with given start and end indices and randomization multipliers.
     * @param x1 horizontal index of start point
     * @param y1 vertical index of start point
     * @param x2 horizontal index of end point
     * @param y2 vertical index of end point
     * @param randomizeX horizontal randomization multiplier
     * @param randomizeY vertical randomization multiplier
     */
    public void midLine(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Start and end point right next to each other or same point
        if(x2 - x1 <= 1 && y2 - y1 <= 1)
            return;

        int xm1, ym1;
        int xm2, ym2;

        if(x2 - x1 > y2 - y1) { // Rectangle wider than tall (> 1:1)
            // Set splitting indices to split rectangle in width (vertically)
            xm1 = x1 + (x2 - x1) / 2;
            ym1 = y1;

            xm2 = x1 + (x2 - x1) / 2;
            ym2 = y2;
        } else { // Rectangle taller than wide (<= 1:1)
            // Set splitting indices to split rectangle in height (horizontally)
            xm1 = x1;
            ym1 = y1 + (y2 - y1) / 2;

            xm2 = x2;
            ym2 = y1 + (y2 - y1) / 2;
        }

        // Generates mid-points between these splitting indices
        this.midPoints(xm1, ym1, xm2, ym2, randomizeX, randomizeY);
        // Generates a new line of mid-points between start point and second splitting indices
        this.midLine(x1, y1, xm2, ym2, randomizeX, randomizeY);
        // Generates a new line of mid-points between first splitting indices and end point
        this.midLine(xm1, ym1, x2, y2, randomizeX, randomizeY);
    }

    /**
     * Recursively generates mid-points between given start and end indices with given randomization multiplier
     * @param x1 horizontal index of start point
     * @param y1 vertical index of start point
     * @param x2 horizontal index of end point
     * @param y2 vertical index of end point
     * @param randomizeX horizontal randomization multiplier
     * @param randomizeY vertical randomization multipliert
     */
    public void midPoints(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Start and end point right next to each other or same point
        if(x2 - x1 <= 1 && y2 - y1 <= 1)
            return;

        // Grabs vectors (with coordinates) of start and end point
        Vector v1 = this.vec[x1][y1];
        Vector v2 = this.vec[x2][y2];

        // Calculates indices for mid-point
        int xm = x1 + (x2 - x1) / 2;
        int ym = y1 + (y2 - y1) / 2;

        double factor =
                Integer.valueOf((x2 - x1) / 2).doubleValue() / Math.max(1D, Integer.valueOf(x2 - x1).doubleValue()) +
                Integer.valueOf((y2 - y1) / 2).doubleValue() / Math.max(1D, Integer.valueOf(y2 - y1).doubleValue());


        if(v1 == null || v2 == null) {
            System.err.println("Grid error occured! There is no Vector at (" + (v1 == null ? x1 + "|" + y1 : x2 + "|" + y2) + ")" + (v1 == null && v2 == null ? " and (" + x2 + "|" + y2 + ")." : "."));
            return;
        }

        // Calculates mid-point coordinates
        Vector vm = v1.add(v2.subtract(v1).multiply(factor));

        /* BEGIN: Randomization */
        Random random = new Random();
        // Random booleans for positive/negative randomization
        boolean rndB1 = random.nextBoolean();
        boolean rndB2 = random.nextBoolean();
        // Random numbers between 0 and 1
        double rndD1 = random.nextDouble();
        double rndD2 = random.nextDouble();

        // Calculates random translation of mid-point (randomization vector)
        //  amount: distance of start and end point multiplied by randomization multipliers and random number
        //          divided by two and either positive or negative
        Vector vRandom = Vector.fromArray(new double[] {
                (rndB1 ? 1 : -1) * (((v2.get(0) - v1.get(0)) * randomizeX * rndD1) / 2D),
                (rndB2 ? 1 : -1) * (((v2.get(1) - v1.get(1)) * randomizeY * rndD2) / 2D)
        });
        // Translates the mid-point by the randomization vector
        vm = vm.add(vRandom);
        /* END: Randomization */

        // Saves the coordinates of the newly generated mid-point
        this.vec[xm][ym] = vm;

        // Generates new random mid-point between start point and recently generated mid-point
        this.midPoints(x1, y1, xm, ym, randomizeX, randomizeY);
        // Generates new random mid-point between recently generated mid-point and end point
        this.midPoints(xm, ym, x2, y2, randomizeX, randomizeY);
    }

    /**
     * Generates the whole grid based on a randomization multiplier
     * @param randomize
     */
    public void setup(double randomize) {
        this.outerGrid(randomize);
        this.midLine(0, 0, piecesX, piecesY, randomize, randomize);
    }

    /**
     * Connects two mid-points using a predefined jigsaw curve (with random direction)
     * @param x1 horizontal index of start point
     * @param y1 vertical index of start point
     * @param x2 horizontal index of end point
     * @param y2 vertical index of end point
     * @param x3 horizontal index of next piece's end point
     * @param y3 vertical index of next piece's end point
     * @return SVG-Paths of both jigsaw curve and connection between pieces
     */
    public String connect(int x1, int y1, int x2, int y2, int x3, int y3) {
        SVGHelper svg;
        Vector from, to;

        // BEGIN: Randomization
        Random random = new Random();
        // Random boolean for forward/backward direction
        boolean rndB = random.nextBoolean();
        if(rndB) { // Forward
            from = this.vec[x1][y1];
            to = this.vec[x2][y2];
        } else { // Backward
            from = this.vec[x2][y2];
            to = this.vec[x1][y1];
        }
        // END: Randomization

        // Creates a new SVG-Path
        svg = new SVGHelper(from, to);
        svg.startPath();

        // Determines whether the puzzle piece starts on the sideline or not
        if((rndB && (x1 == 0 || y1 == 0)) || (!rndB && (x2 == piecesX || y2 == piecesY))) {
            // if it does, draw the line to the side
            svg.moveTo(0, 0);
            svg.lineTo(0.4, 0);
        } else {
            // it doesn't, just move to the beginning of the jigsaw curve
            svg.moveTo(0.4, 0);
        }

        // BEGIN: Jigsaw curve
        // Draws a curve to (0.23|0.2)
        svg.curveTo(
                0.475, 0.00,
                0.39, 0.05,
                0.365, 0.10
        );
        // Draws a curve to (0.33|0.4)
        svg.curveTo(
                0.345, 0.14,
                0.375, 0.18,
                0.415, 0.20
        );
        // Draws a curve to (0.67|0.4)
        svg.curveTo(
                0.465, 0.225,
                0.535, 0.225,
                0.585, 0.20
        );
        // Draws a curve to (0.77|0.2)
        svg.curveTo(
                0.625, 0.18,
                0.655, 0.14,
                0.635, 0.10
        );
        // Draws a curve to (0.7|0)
        svg.curveTo(
                0.61, 0.05,
                0.525, 0.00,
                0.60, 0.00
        );
        // END: Jigsaw curve

        // Determines whether the puzzle piece ends on the sideline
        if((!rndB && (x1 == 0 || y1 == 0)) || (rndB && (x2 == piecesX || y2 == piecesY))) {
            // if it does, draw the line to the side
            svg.lineTo(1, 0);
        }

        // TODO: Remove debug
        svg.setId(Integer.toString(x1) + "|" + Integer.toString(y1) + " - " + Integer.toString(x2) + "|" + Integer.toString(y2));

        // Fetches the generated jigsaw curve's path
        String curvePath = svg.getPathAndClear();

        // Determines whether the end point is surrounded by other points (not on the sideline)
        if(x2 > 0 && x2 < piecesX && y2 > 0 && y2 < piecesY) {
            // Creates a new SVG-Path
            svg = new SVGHelper(Vector.fromArray(new double[] {0, 0}), Vector.fromArray(new double[] {0, 0}));
            svg.startPath();

            // Connects the end of the jigsaw curve of this piece to the start of the jigsaw curve of the next piece
            svg.moveTo(vec[x1][y1].add(vec[x2][y2].subtract(vec[x1][y1]).multiply(0.6)));
            svg.cubicCurveTo(vec[x2][y2], vec[x2][y2].add(vec[x3][y3].subtract(vec[x2][y2]).multiply(0.4)));

            // Appends the connection's path to the output
            curvePath += svg.getPathAndClear();
        }

        return curvePath;
    }

    /**
     * Connects all mid-points using a predefined jigsaw curve
     * @return a list of all generated paths
     */
    public ArrayList<String> makeConnections() {
        ArrayList<String> connections = new ArrayList<>();
        for(int x = 1; x <= piecesX; x++) {
            for(int y = 1; y <= piecesY; y++) {
                if(y != piecesY) // Prevents horizontal ghost curves
                    connections.add(this.connect(x - 1, y, x, y, x + 1, y));
                if(x != piecesX) // Prevents vertical ghost curves
                    connections.add(this.connect(x, y - 1, x, y, x, y + 1));
            }
        }
        return connections;
    }

    /**
     * For now the outer lines consists of a basic rectangle
     * @return
     */
    public String outerLines() {
        return "<rect width=\"" + width + "\" height=\"" + height + "\" class=\"a\" />";
    }

}
