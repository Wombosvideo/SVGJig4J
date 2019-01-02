package ch.bosin.svgjig4j;

import org.la4j.Vector;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class IndexBased {

    public Vector[][] vec;
    public int width, height;
    public int piecesX, piecesY;

    /**
     *
     * @param width
     * @param height
     * @param piecesX
     * @param piecesY
     */
    public IndexBased(int width, int height, int piecesX, int piecesY) {
        this.vec = new Vector[piecesX + 1][piecesY + 1];

        this.width = width;
        this.height = height;

        this.piecesX = piecesX;
        this.piecesY = piecesY;
    }

    public void outerGrid(double randomize) {
        this.vec[0][0] = Vector.fromArray(new double[] {0D, 0D});
        this.vec[piecesX][0] = Vector.fromArray(new double[] {this.width, 0D});
        this.vec[0][piecesY] = Vector.fromArray(new double[] {0D, this.height});
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

    public void midLine(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Right next to each other or same point
        if(x2 - x1 <= 1 && y2 - y1 <= 1)
            return;

        int xm1, ym1;
        int xm2, ym2;

        if(x2 - x1 > y2 - y1) {
            xm1 = x1 + (x2 - x1) / 2;
            ym1 = y1;

            xm2 = x1 + (x2 - x1) / 2;
            ym2 = y2;
        } else {
            xm1 = x1;
            ym1 = y1 + (y2 - y1) / 2;

            xm2 = x2;
            ym2 = y1 + (y2 - y1) / 2;
        }

        this.midPoints(xm1, ym1, xm2, ym2, randomizeX, randomizeY);
        this.midLine(x1, y1, xm2, ym2, randomizeX, randomizeY);
        this.midLine(xm1, ym1, x2, y2, randomizeX, randomizeY);
    }

    public void midPoints(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Right next to each other or same point
        if(x2 - x1 <= 1 && y2 - y1 <= 1)
            return;

        Vector v1 = this.vec[x1][y1];
        Vector v2 = this.vec[x2][y2];

        int xm = x1 + (x2 - x1) / 2;
        int ym = y1 + (y2 - y1) / 2;

        double factor =
                Integer.valueOf((x2 - x1) / 2).doubleValue() / Math.max(1D, Integer.valueOf(x2 - x1).doubleValue()) +
                Integer.valueOf((y2 - y1) / 2).doubleValue() / Math.max(1D, Integer.valueOf(y2 - y1).doubleValue());


        if(v1 == null || v2 == null) {
            System.err.println("Grid error occured! There is no Vector at (" + (v1 == null ? x1 + "|" + y1 : x2 + "|" + y2) + ")" + (v1 == null && v2 == null ? " and (" + x2 + "|" + y2 + ")." : "."));
            return;
        }


        Vector vm = v1.add(v2.subtract(v1).multiply(factor));

        // BEGIN: Randomization
        Random random = new Random();
        boolean rndB1 = random.nextBoolean();
        boolean rndB2 = random.nextBoolean();
        double rndD1 = random.nextDouble();
        double rndD2 = random.nextDouble();

        Vector vRandom = Vector.fromArray(new double[] {
                (rndB1 ? 1 : -1) * (((v2.get(0) - v1.get(0)) * randomizeX * rndD1) / 2D),
                (rndB2 ? 1 : -1) * (((v2.get(1) - v1.get(1)) * randomizeY * rndD2) / 2D)
        });
        // END: Randomization

        this.vec[xm][ym] = vm.add(vRandom);

        this.midPoints(x1, y1, xm, ym, randomizeX, randomizeY);
        this.midPoints(xm, ym, x2, y2, randomizeX, randomizeY);
    }

    public void setup(double randomize) {
        this.outerGrid(randomize);
        this.midLine(0, 0, piecesX, piecesY, randomize, randomize);
    }

    public void printNonNull() {
        ArrayList<Vector> nonNull = new ArrayList<>();
        for(int x = 0; x <= piecesX; x++)
            for(int y = 0; y <= piecesY; y++)
                if(this.vec[x][y] != null)
                    nonNull.add(Vector.fromArray(new double[] {x, y}));
        for(Vector idx : nonNull)
            System.out.println(idx.mkString(NumberFormat.getNumberInstance(), ","));
    }

    public String connect(int x1, int y1, int x2, int y2, int x3, int y3) {
        SVGHelper svg;
        Vector from, to, connect = null;
        boolean hasStart = x1 == 0 || y1 == 0;
        boolean hasConnection = x3 <= piecesX && y3 <= piecesY;

        // BEGIN: Randomization
        Random random = new Random();
        boolean rndB = random.nextBoolean();
        if(rndB) {
            from = this.vec[x1][y1];
            to = this.vec[x2][y2];
        } else {
            from = this.vec[x2][y2];
            to = this.vec[x1][y1];
        }
        if(hasConnection)
            connect = this.vec[x3][y3];
        svg = new SVGHelper(from, to);
        // END: Randomization


        System.out.println(
                "rndB: " + Boolean.toString(rndB) + " " +
                "hasStart: " + Boolean.toString(hasStart) + " " +
                "hasConnection: " + Boolean.toString(hasConnection) + " " +
                "from: " + svg.vecToString(from) + " " +
                "to: " + svg.vecToString(to));


        // Creates a new path
        svg.startPath();

        /*
        if(rndB) {
            if(hasStart) {
                svg.moveTo(vec[x1][y1]);
            } else {
                svg.moveTo(vec[x1][y1].add(vec[x2][y2].subtract(vec[x1][y1]).multiply(0.40)));
            }
        } else {
            if(hasConnection) {
                svg.moveTo(vec[x2][y2].add(vec[x3][y3].subtract(vec[x2][y2]).multiply(0.40)));
                svg.cubicCurveTo(vec[x2][y2], vec[x2][y2].add(vec[x2][y2].subtract(vec[x1][y1]).multiply(0.40)));
            } else {
                svg.moveTo(vec[x2][y2]);
            }
        }
        */

        if((rndB && (x1 == 0 || y1 == 0)) || (!rndB && (x2 == piecesX || y2 == piecesY))) {
            svg.moveTo(0, 0);
            svg.lineTo(0.4, 0);
        } else {
            svg.moveTo(0.4, 0);
        }

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

        if((!rndB && (x1 == 0 || y1 == 0)) || (rndB && (x2 == piecesX || y2 == piecesY))) {
            svg.lineTo(1, 0);
        }


        /*
        if(rndB) {
            if(hasConnection) {
                svg.cubicCurveTo(vec[x2][y2], vec[x2][y2].add(vec[x3][y3].subtract(vec[x2][y2]).multiply(0.40)));
            } else {
                svg.lineTo(vec[x2][y2]);
            }
        } else {
            if(hasStart) {
                svg.lineTo(vec[x1][y1]);
            }
        }
        */

        svg.setId(Integer.toString(x1) + "|" + Integer.toString(y1) + " - " + Integer.toString(x2) + "|" + Integer.toString(y2));

        String curvePath = svg.getPathAndClear();

        if(x2 > 0 && x2 < piecesX && y2 > 0 && y2 < piecesY) {

            svg = new SVGHelper(Vector.fromArray(new double[] {0, 0}), Vector.fromArray(new double[] {0, 0}));
            svg.startPath();
            svg.moveTo(vec[x1][y1].add(vec[x2][y2].subtract(vec[x1][y1]).multiply(0.6)));
            svg.cubicCurveTo(vec[x2][y2], vec[x2][y2].add(vec[x3][y3].subtract(vec[x2][y2]).multiply(0.4)));
            curvePath += svg.getPathAndClear();

        }


        //return svg.getPathAndClear();
        return curvePath;
    }

    public ArrayList<String> makeConnections() {
        ArrayList<String> connections = new ArrayList<>();
        for(int x = 1; x <= piecesX; x++) {
            for(int y = 1; y <= piecesY; y++) {
                if(y != piecesY)
                    connections.add(this.connect(x - 1, y, x, y, x + 1, y));
                if(x != piecesX)
                    connections.add(this.connect(x, y - 1, x, y, x, y + 1));
            }
        }
        return connections;
    }

    public String outerLines() {
        return "<rect width=\"" + width + "\" height=\"" + height + "\" class=\"a\" />";
    }

}
