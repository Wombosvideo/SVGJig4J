package ch.bosin.svgjig4j;

import org.la4j.Vector;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class IndexBased {

    public Vector[][] vec;
    public int width, height;
    public int piecesX, piecesY;

    public IndexBased(int width, int height, int piecesX, int piecesY) {
        this.vec = new Vector[piecesX + 1][piecesY + 1];

        this.width = width;
        this.height = height;

        this.piecesX = piecesX;
        this.piecesY = piecesY;
    }

    public void outerGrid(double randomize) {
        System.out.println("Drawing outer grid...");

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

        System.out.println("Outer grid completed!");
    }

    public void midLine(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Right next to each other or same point
        if(x2 - x1 <= 2 && y2 - y1 <= 2)
            return;

        int xm1, ym1;
        int xm2, ym2;

        if(x2 - x1 > y2 - y1) {
            xm1 = x1 + (x2 - x1) / 2;
            ym1 = y1;

            xm2 = x1 + (x2 - x1) / 2;
            ym2 = y2;
            System.out.println("Generating y-midLine between (" + x1 + "|" + y1 + ") and (" + x2 + "|" + y2 + ")");
        } else {
            xm1 = x1;
            ym1 = y1 + (y2 - y1) / 2;

            xm2 = x2;
            ym2 = y1 + (y2 - y1) / 2;
            System.out.println("Generating x-midLine between (" + x1 + "|" + y1 + ") and (" + x2 + "|" + y2 + ")");
        }

        this.midPoints(xm1, ym1, xm2, ym2, randomizeX, randomizeY);
    }

    public void midPoints(int x1, int y1, int x2, int y2, double randomizeX, double randomizeY) {
        // Right next to each other or same point
        if(x2 - x1 <= 1 && y2 - y1 <= 1)
            return;

        System.out.println("Generating midPoints between (" + x1 + "|" + y1 + ") and (" + x2 + "|" + y2 + ")");

        Vector v1 = this.vec[x1][y1];
        Vector v2 = this.vec[x2][y2];

        int xm = x1 + (x2 - x1) / 2;
        int ym = y1 + (y2 - y1) / 2;

        int factor =
                ((x2 - x1) / 2) / Math.max(1, x2 - x1) +
                ((y2 - y1) / 2) / Math.max(1, y2 - y1);

        Vector vm = v1.add(v2.subtract(v1).multiply(Integer.valueOf(factor).doubleValue()));

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

        System.out.println("Generated new midPoint at (" + xm + "|" + ym + ")");

        this.midPoints(x1, y1, xm, ym, randomizeX, randomizeY);
        this.midPoints(xm, ym, x2, y2, randomizeX, randomizeY);

        this.midLine(x1, y1, xm, ym, randomizeX, randomizeY);
        this.midLine(xm, ym, y2, y2, randomizeX, randomizeY);
    }

    public void setup(double randomize) {
        System.out.println("Setting up...");
        this.outerGrid(randomize);
        System.out.println("Drawing first line...");
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

}
