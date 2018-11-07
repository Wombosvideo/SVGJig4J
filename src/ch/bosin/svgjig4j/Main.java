package ch.bosin.svgjig4j;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.SparseVector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

    public void run(SVGHelper svg) {
        // DEBUG: Prints start and end point and the unit vectors
        System.out.println("A(" + svg.vecToString(svg.getStartPoint(), "|") + ")");
        System.out.println("B(" + svg.vecToString(svg.getEndPoint(), "|") + ")");
        System.out.println("u=[" + svg.vecToString(svg.getUnitVectorX(), ";") + "]");
        System.out.println("v=[" + svg.vecToString(svg.getUnitVectorY(), ";") + "]");

        // Creates a new path
        svg.startPath();

        // Moves cursor to given start point (0|0)
        svg.moveTo(0.00, 0.00);
        // Draws a line to (0.3|0)
        svg.lineTo(0.40, 0.00);

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

        // Draws a line to given end point (1|0)
        svg.lineTo(svg.getEndPoint());

        // DEBUG: Prints out the d argument of the svg path
        System.out.println(svg.getPath());

        // Write to file
        writeSVG(List.of(svg.getPathAndClear()), "end", svg.width, svg.height);
    }

    public static void main(String[] args) {
        int width = 1000;
        int height = 600;

        int piecesX = 12; // aka. rows
        int piecesY = 9; // aka. columns

        double randomizeBy = 0.1;

        SparseVector startPoint = SparseVector.fromArray(new double[] {0, 0});
        SparseVector endPoint = SparseVector.fromArray(new double[] {width, height});
        SVGHelper svg = new SVGHelper(startPoint, endPoint);

        Vector[][] storage = new Vector[piecesY+1][piecesX+1];
        ArrayList<String> circles = new ArrayList<>();

        for(int x = 0; x < piecesX-1; x++) {
            double vx = Integer.valueOf(x).doubleValue() * (Integer.valueOf(width).doubleValue() / Integer.valueOf(piecesX-2).doubleValue());
            storage[0][x] = Vector.fromArray(new double[] { vx, 0 });
            storage[piecesY][x] = Vector.fromArray(new double[] { vx, height });
            circles.add("<circle cx=\"" + vx + "\" cy=\"" + 0 + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/10) + "\"/>\n");
            circles.add("<circle cx=\"" + vx + "\" cy=\"" + height + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/10) + "\"/>\n");
        }
/*        for(int y = 1; y < piecesY; y++){
            double vy = Integer.valueOf(y).doubleValue() * (Integer.valueOf(height).doubleValue() / Integer.valueOf(piecesY).doubleValue());
            storage[y][0] = Vector.fromArray(new double[] { 0, vy });
            storage[y][piecesX] = Vector.fromArray(new double[] { width, vy });
            circles.add("<circle cx=\"" + 0 + "\" cy=\"" + vy + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\"/>\n");
            circles.add("<circle cx=\"" + width + "\" cy=\"" + vy + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\"/>\n");
        }*/

        System.out.println(Arrays.deepToString(storage));

        // Write to output-stg_01.svg
        writeSVG(circles, "stg_01", width, height);

        int randomIndexX = 8;
        circles.set(2 * randomIndexX, circles.get(2 * randomIndexX).replaceAll("\\/>", " class=\"b\"\\/>"));
        circles.set(2 * randomIndexX + 1, circles.get(2 * randomIndexX + 1).replaceAll("\\/>", " class=\"b\"\\/>"));

        writeSVG(circles, "stg_02", width, height);

        Vector posTop = storage[0][randomIndexX];
        Vector posBottom = storage[piecesY][randomIndexX];

        svg.startPath();
        svg.moveTo(posTop);
        svg.lineTo(posBottom);
        circles.add(svg.getPathAndClear());

        writeSVG(circles, "stg_03", width, height);

        int piecesY_ = 1;

        Vector posTemp, posMiddle = posTemp = middleRnd(posTop, posBottom, randomizeBy, Integer.valueOf(Math.floorDiv(piecesY, 2)).doubleValue()/Integer.valueOf(piecesY).doubleValue());
        circles.add("<circle cx=\"" + posMiddle.get(0) + "\" cy=\"" + posMiddle.get(1) + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\" class=\"b\"/>\n");
        piecesY_++;


/*
        posMiddle = middleRnd(posTop, posTemp, randomizeBy, 1D/6D);
        circles.add("<circle cx=\"" + posMiddle.get(0) + "\" cy=\"" + posMiddle.get(1) + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\" class=\"b\"/>\n");
        piecesY_++;
        posMiddle = middleRnd(posTop, posTemp, randomizeBy, 2D/3D);
        circles.add("<circle cx=\"" + posMiddle.get(0) + "\" cy=\"" + posMiddle.get(1) + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\" class=\"b\"/>\n");
        piecesY_++;

        posMiddle = middleRnd(posTemp, posBottom, randomizeBy, 1D/3D);
        circles.add("<circle cx=\"" + posMiddle.get(0) + "\" cy=\"" + posMiddle.get(1) + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\" class=\"b\"/>\n");
        piecesY_++;
        posMiddle = middleRnd(posTemp, posBottom, randomizeBy, 2D/3D);
        circles.add("<circle cx=\"" + posMiddle.get(0) + "\" cy=\"" + posMiddle.get(1) + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\" class=\"b\"/>\n");
        piecesY_++;
*/

        writeSVG(circles, "stg_04", width, height);

        ArrayList<Vector> test = middleCntd(posTop, posBottom, randomizeBy, piecesY-1);
        System.out.println("What the heck: " + Arrays.toString(test.toArray()));
        for(Vector v : test) {
            circles.add("<circle cx=\"" + v.get(0) + "\" cy=\"" + v.get(1) + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\" class=\"c\"/>\n");
        }

        writeSVG(circles, "stg_05", width, height);


        Vector middle = middleRnd(startPoint, endPoint, randomizeBy);
        System.out.println(middle.mkString(NumberFormat.getNumberInstance(), ","));

        middleEdgeRnd(startPoint, Vector.fromArray(new double[]{width, 0}), Vector.fromArray(new double[]{0, height}), endPoint, middle, randomizeBy);

/*
        SparseVector startPoint = SparseVector.fromArray(new double[]{0,100});
        SparseVector endPoint = SparseVector.fromArray(new double[]{100,100});

        SparseVector[][] yel = new SparseVector[10][10];
        for(int y = 1; y < 10; y++) {
            SparseVector[] xel = new SparseVector[10];
            for(int x = 0; x < 10; x++) {
                Arrays.fill(xel, SparseVector.fromArray(new double[]{x*100, y*100}));
            }
            yel[y]=xel;
        }
        System.out.println(Arrays.toString(yel));
        new Main(startPoint, endPoint).run();
*/
    }

    private static void writeSVG(List<String> paths, String releaseName, int width, int height) {
        // Write to file
        String svgData = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + width + " " + height + "\">\n" +
                "  <defs>\n" +
                "    <style>\n" +
                "      .a {\n" +
                "        fill: none;\n" +
                "        stroke: #e52421;\n" +
                "        stroke-miterlimit: 10;\n" +
                "      }\n" +
                "      .b {\n" +
                "        fill: #e52421;\n" +
                "      }\n" +
                "      .c {\n" +
                "        fill: #0000ff;\n" +
                "      }\n" +
                "      .d {\n" +
                "        fill: #00ff00;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </defs>\n" +
                "  <title>curve_1</title>\n";
        for(String path : paths)
            svgData += "  " + path;
        svgData +=
                "</svg>\n";
        try{
            System.out.println("Writing file");
            Files.write(Paths.get("G:\\Matura\\output-" + releaseName + ".svg"), svgData.getBytes());
        } catch(IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static ArrayList<Vector> middleCntd(Vector p1, Vector p2, double randomizeBy, int frac) {
        return middleCntd(p1, p2, randomizeBy, frac, "c");
    }

    private static ArrayList<Vector> middleCntd(Vector p1, Vector p2, double randomizeBy, int frac, String klass) {
        int fracLow = Math.floorDiv(frac, 2);
        int fracHigh = frac-fracLow;
        ArrayList<Vector> vectors = new ArrayList<>();

        Vector m = middleRnd(p1, p2, randomizeBy, Integer.valueOf(fracLow).doubleValue()/Integer.valueOf(frac).doubleValue());

        if(fracLow == 1) {
            // Don't know what to do
            System.out.println("Don't know what to do: " + fracLow + "/" + frac + " or " + fracHigh + "/" + frac);
            vectors.add(middleRnd(p1, m, randomizeBy, Integer.valueOf(fracLow).doubleValue()/Integer.valueOf(frac).doubleValue()));
        }
        if(fracHigh == 1) {
            // Don't know what to do
            System.out.println("Don't know what to do: " + fracLow + "/" + frac + " or " + fracHigh + "/" + frac);
            vectors.add(middleRnd(m, p2, randomizeBy, Integer.valueOf(fracLow).doubleValue()/Integer.valueOf(frac).doubleValue()));
        }
        if(fracHigh == 1 || fracLow == 1)
            return vectors;

        if(fracLow%2==0) {
            System.out.println("This is low funny: " + fracLow);
            vectors.addAll(middleCntd(p1, m, randomizeBy, fracLow));
        } else {
            for(int i = 1; i < fracLow; i++){
                Vector mdl = middleRnd(p1, m, randomizeBy, Integer.valueOf(i).doubleValue()/Integer.valueOf(fracLow).doubleValue());
                vectors.add(mdl);
            }
        }

        if(fracHigh%2==0) {
            System.out.println("This is high funny: " + fracHigh);
            vectors.addAll(middleCntd(m, p2, randomizeBy, fracHigh));
        } else {
            for(int i = 1; i < fracHigh; i++){
                Vector mdl = middleRnd(m, p2, randomizeBy, Integer.valueOf(i).doubleValue()/Integer.valueOf(fracHigh).doubleValue());
                vectors.add(mdl);
            }
        }
        return vectors;
    }

    private static Vector middleRnd(Vector p1, Vector p2, double randomizeBy) {
        return middleRnd(p1, p2, randomizeBy, 0.5D);
    }

    private static Vector middleRnd(Vector p1, Vector p2, double randomizeBy, double frac) {
        Vector r1 = p2.subtract(p1);
        Vector tst1 = r1.multiply(frac);
        Vector tst2 = tst1.add(p1);

        Random random = new Random();
        System.out.println("DEBUG: x" + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(0));
        System.out.println("DEBUG: y" + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(1));
        System.out.println(tst2.mkString(NumberFormat.getNumberInstance(), ","));
        Vector mod = Vector.fromArray(new double[] {tst2.get(0) + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(0), tst2.get(1) + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(1) });

        Vector out = tst2.add(mod);
        return mod;
    }

    private static Vector[] middleEdgeRnd(Vector p1, Vector p2, Vector p3, Vector p4, Vector m1, double randomizeBy){
        Matrix inverse = Matrix.from1DArray(2,2,new double[]{0,1,-1,0});
        Vector ab = p4.subtract(p3);double l_ab = ab.norm();Vector e_ab = ab.divide(l_ab),ne_ab = e_ab.multiply(inverse);
        Vector bc = p2.subtract(p4);double l_bc = bc.norm();Vector e_bc = bc.divide(l_bc),ne_bc = e_bc.multiply(inverse);
        Vector cd = p1.subtract(p2);double l_cd = cd.norm();Vector e_cd = cd.divide(l_cd),ne_cd = e_cd.multiply(inverse);
        Vector da = p3.subtract(p1);double l_da = da.norm();Vector e_da = da.divide(l_da),ne_da = e_da.multiply(inverse);

        Vector dm = m1.subtract(p1);double x_dm = dm.get(0), y_dm = dm.get(1);
        Vector cm = m1.subtract(p2);double x_cm = -cm.get(0), y_cm = cm.get(1);
        Vector am = m1.subtract(p3);double x_am = am.get(0), y_am = -am.get(1);

        double  xa = p3.get(0), ya = p3.get(1),
                xb = p4.get(0), yb = p4.get(1),
                xc = p2.get(0), yc = p2.get(1),
                xd = p1.get(0), yd = p1.get(1),
                xm = m1.get(0), ym = m1.get(1);

        double a1 = ((xc*(xd-xm)-Math.pow(xd,2)+xd*xm+(yc-yd)*(yd-ym))*Math.sqrt(Math.pow(xc,2)-2*xc*xd+Math.pow(xd,2)+Math.pow(yc-yd,2)))/(Math.pow(xc,2)-2*xc*xd+Math.pow(xd,2)+Math.pow(yc,2)-2*yc*yd+Math.pow(yd,2));

        System.out.println(e_cd.multiply(a1).mkString(NumberFormat.getNumberInstance(), ","));

        double dmc = 0.5 - x_dm/(x_dm + x_cm);
        double dma = 0.5 - y_dm/(y_dm + y_am);

        System.out.println("Relativ auf x: " + dmc + " [" + x_dm + ";" + y_dm + "], [" + x_cm + ";" + y_cm + "]");
        System.out.println("Relativ auf y: " + dma + " [" + x_dm + ";" + y_dm + "], [" + x_am + ";" + y_am + "]");
        return null;
    }
}
