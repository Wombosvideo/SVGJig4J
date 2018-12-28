package ch.bosin.svgjig4j;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.SparseVector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;

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

    public static ArrayList<Vector> sortedX(ArrayList<Vector> unsorted) {
        ArrayList<Vector> sorted = (ArrayList<Vector>)unsorted.clone();
        sorted.sort(Comparator.comparingDouble((l) -> l.get(0)));
        return sorted;
    }

    public static ArrayList<Vector> sortedY(ArrayList<Vector> unsorted) {
        ArrayList<Vector> sorted = (ArrayList<Vector>)unsorted.clone();
        sorted.sort(Comparator.comparingDouble((l) -> l.get(1)));
        return sorted;
    }

    public static void main(String[] args) {

        int width = 1000;
        int height = 600;

        int piecesX = 8; // aka. rows
        int piecesY = 6; // aka. columns

        double randomizeBy = 0.1D;

        IndexBased ib = new IndexBased(width, height, piecesX, piecesY);
        try {
            ib.setup(randomizeBy);
        } catch (NullPointerException e) {
            e.printStackTrace();
            ib.printNonNull();
        }

        ArrayList<String> output = new ArrayList<>();

        for(int x = 0; x <= piecesX; x++) {
            for(int y = 0; y <= piecesY; y++) {
                Vector v = ib.vec[x][y];
                if(v != null)
                output.add("<circle cx=\"" + v.get(0) + "\" cy=\"" + v.get(1) + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\" class=\"c\"/>\n");
            }
        }

        output.addAll(ib.makeConnections());
        output.add(ib.outerLines());

        writeSVG(output, "test", width, height);

        //writeSVG(ib.makeConnections(), "test", width, height);

        //----------------------------------------------------------------------

        int a = 1;
        a++;
        if(a < 100)
            return;


        SparseVector startPoint = SparseVector.fromArray(new double[] {0, 0});
        SparseVector endPoint = SparseVector.fromArray(new double[] {width, height});
        SVGHelper svg = new SVGHelper(startPoint, endPoint);

        Vector[][] storage = new Vector[piecesY+2][piecesX+2];
        ArrayList<String> circles = new ArrayList<>();
        ArrayList<Vector> circlesV = new ArrayList<>();

        circles.add("<circle cx=\"" + 0 + "\" cy=\"" + 0 + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\"/>\n");
        circles.add("<circle cx=\"" + width + "\" cy=\"" + 0 + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\"/>\n");
        circles.add("<circle cx=\"" + 0 + "\" cy=\"" + height + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\"/>\n");
        circles.add("<circle cx=\"" + width + "\" cy=\"" + height + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\"/>\n");

        storage[0][0] = startPoint;
        storage[0][piecesX] = Vector.fromArray(new double[] {0, width});
        storage[piecesY][0] = Vector.fromArray(new double[] {height, 0});
        storage[piecesY][piecesX] = Vector.fromArray(new double[] {height, 0});

/*
        for(int x = 0; x < piecesX-1; x++) {
            double vx = Integer.valueOf(x).doubleValue() * (Integer.valueOf(width).doubleValue() / Integer.valueOf(piecesX-2).doubleValue());
            storage[0][x] = Vector.fromArray(new double[] { vx, 0 });
            storage[piecesY][x] = Vector.fromArray(new double[] { vx, height });
            circles.add("<circle cx=\"" + vx + "\" cy=\"" + 0 + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/10) + "\"/>\n");
            circles.add("<circle cx=\"" + vx + "\" cy=\"" + height + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/10) + "\"/>\n");
        }
*/

        ArrayList<Vector> topRow = sortedX(middleCntd(SparseVector.fromArray(new double[] {0, 0}), SparseVector.fromArray(new double[] {width, 0}), randomizeBy, piecesX, piecesX, 1));
        circlesV.addAll(topRow);
        for(int i = 0; i < piecesX-1; i++) {
            storage[0][i+1] = topRow.get(i);
        }

        ArrayList<Vector> bottomRow = sortedX(middleCntd(SparseVector.fromArray(new double[] {0, height}), SparseVector.fromArray(new double[] {width, height}), randomizeBy, piecesX, piecesX, 1));
        circlesV.addAll(bottomRow);
        for(int i = 0; i < piecesX-1; i++) {
            storage[piecesY][i+1] = bottomRow.get(i);
        }

        ArrayList<Vector> leftRow = sortedY(middleCntd(SparseVector.fromArray(new double[] {0, 0}), SparseVector.fromArray(new double[] {0, height}), randomizeBy, piecesY, piecesY, -1));
        circlesV.addAll(leftRow);
        for(int i = 0; i < piecesY-1; i++) {
            storage[i+1][0] = leftRow.get(i);
        }

        ArrayList<Vector> rightRow = sortedY(middleCntd(SparseVector.fromArray(new double[] {width, 0}), SparseVector.fromArray(new double[] {width, height}), randomizeBy, piecesY, piecesY, -1));
        circlesV.addAll(rightRow);
        for(int i = 0; i < piecesY-1; i++) {
            storage[i+1][piecesX] = rightRow.get(i);
        }

/*        for(int y = 1; y < piecesY; y++){
            double vy = Integer.valueOf(y).doubleValue() * (Integer.valueOf(height).doubleValue() / Integer.valueOf(piecesY).doubleValue());
            storage[y][0] = Vector.fromArray(new double[] { 0, vy });
            storage[y][piecesX] = Vector.fromArray(new double[] { width, vy });
            circles.add("<circle cx=\"" + 0 + "\" cy=\"" + vy + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\"/>\n");
            circles.add("<circle cx=\"" + width + "\" cy=\"" + vy + "\" r=\"" + (((width/piecesX + height/piecesY)/2)/10) + "\"/>\n");
        }*/

        //System.out.println(Arrays.deepToString(storage));

        // Write to output-stg_01.svg
        writeSVG(circles, "stg_01", width, height);

        int randomIndexX = 8;
        /*
        circles.set(2 * randomIndexX, circles.get(2 * randomIndexX).replaceAll("\\/>", " class=\"b\"\\/>"));
        circles.set(2 * randomIndexX + 1, circles.get(2 * randomIndexX + 1).replaceAll("\\/>", " class=\"b\"\\/>"));
        */
        Vector posTop = circlesV.get(randomIndexX);
        Vector posBottom = circlesV.get(piecesX + randomIndexX);


        writeSVG(circles, "stg_02", width, height);

        int[]   v1 = {0, 0},
                v2 = {piecesY, 0},
                v3 = {0, piecesX},
                v4 = {piecesY, piecesX};

        if(storage[v2[0]][v2[1]].subtract(storage[v1[0]][v1[1]]).norm() < storage[v3[0]][v3[1]].subtract(storage[v1[0]][v1[1]]).norm()) {
            System.out.println("H länger V");
            posTop = storage[v1[0] + (v3[0]-v1[0])/2][v1[1] + (v3[1]-v1[1])/2]; // Links Mitte
            posBottom = storage[v2[0] + (v4[0]-v2[0])/2][v2[1] + (v4[1]-v2[1])/2]; // Rechts Mitte
        } else {
            System.out.println("V länger H");
            posTop = storage[v1[0] + (v2[0]-v1[0])/2][v1[1] + (v2[1]-v1[1])/2]; // Oben Mitte
            posBottom = storage[v3[0] + (v4[0]-v3[0])/2][v3[1] + (v4[1]-v3[1])/2]; // Unten Mitte
        }

        svg.startPath();
        svg.moveTo(startPoint);
        svg.lineTo(endPoint);
        circles.add(svg.getPathAndClear());
        writeSVG(circles, "stg_03", width, height);


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

        ArrayList<Vector> test = middleCntd(posTop, posBottom, randomizeBy, piecesY, piecesY, 1);
        circlesV.addAll(test);

        for(int i = 0; i < test.size(); i++) {
            storage[piecesX/2][i+1] = test.get(i);
        }

        System.out.println("What the heck: " + Arrays.toString(test.toArray()));


        /*
                v1 = startPoint;
                v2 = SparseVector.fromArray(new double[] {width, 0});
                v3 = SparseVector.fromArray(new double[] {0, height});
                v4 = endPoint;

        if(v2.subtract(v1).norm() > v3.subtract(v1).norm()) {
            posTop = circlesV.get(piecesX/2-1);
            posBottom = circlesV.get(piecesX + piecesX/2-2);
        } else {
            posTop = v1.add(v3.subtract(v1).divide(2D));
            posBottom = v2.add(v4.subtract(v2).divide(2D));
        }
        */
        svg.startPath();
        svg.moveTo(startPoint);
        svg.lineTo(endPoint);
        circles.add(svg.getPathAndClear());



        for(Vector v : circlesV) {
            circles.add("<circle cx=\"" + v.get(0) + "\" cy=\"" + v.get(1) + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\" class=\"c\"/>\n");
        }

        writeSVG(circles, "stg_05", width, height);


        //Vector middle = middleRnd(startPoint, endPoint, randomizeBy, 0);
        //System.out.println(middle.mkString(NumberFormat.getNumberInstance(), ","));

        //middleEdgeRnd(startPoint, Vector.fromArray(new double[]{width, 0}), Vector.fromArray(new double[]{0, height}), endPoint, middle, randomizeBy);

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
            Files.write(Paths.get("C:\\Users\\Wombosvideo\\Documents\\output\\output-" + releaseName + ".svg"), svgData.getBytes());
            //Files.write(Paths.get("G:\\Matura\\output-" + releaseName + ".svg"), svgData.getBytes());
        } catch(IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static ArrayList<Vector> middleCntd(Vector p1, Vector p2, double randomizeBy, int frac, int max, int additional) {
        return middleCntd(p1, p2, randomizeBy, frac, max, additional, "c");
    }

    private static ArrayList<Vector> middleCntd(Vector p1, Vector p2, double randomizeBy, int frac, int max, int additional, String klass) {
        Vector m;
        if(frac % 2 == 0) {
            // Middle exists
            // FIXME: 0.5D scheint falsch
            m = middleRnd(p1, p2, randomizeBy, 0.5D, additional);
        } else {
            // Middle doesn't exist, use (frac/2)/max
            m = middleRnd(p1, p2, randomizeBy, (Integer.valueOf(frac).doubleValue()/2D)/Integer.valueOf(max).doubleValue(), additional);

        }

        int fracLow = Math.floorDiv(frac, 2);
        int fracHigh = frac-fracLow;
        ArrayList<Vector> vectors = new ArrayList<>();


        vectors.add(m);

        /*
        if(fracHigh == 1 && fracLow == 1) {
            vectors.add(m);
            return vectors;
        }
        */
        if(fracLow > 1)
            vectors.addAll(middleCntd(p1, m, randomizeBy, fracLow, max, additional));
/*
        if(fracLow%2==0) {
            vectors.addAll(middleCntd(p1, m, randomizeBy, fracLow, max));
        } else {
            for(int i = 1; i < fracLow; i++){
                Vector mdl = middleRnd(p1, m, randomizeBy, Integer.valueOf(i).doubleValue()/Integer.valueOf(fracLow).doubleValue());
                vectors.add(mdl);
            }
        }
*/
        if(fracHigh > 1)
            vectors.addAll(middleCntd(m, p2, randomizeBy, fracHigh, max, additional));
/*
        if(fracHigh%2==0) {
            System.out.println("This is high funny: " + fracHigh);
            vectors.addAll(middleCntd(m, p2, randomizeBy, fracHigh, max));
        } else {
            for(int i = 1; i < fracHigh; i++){
                Vector mdl = middleRnd(m, p2, randomizeBy, Integer.valueOf(i).doubleValue()/Integer.valueOf(fracHigh).doubleValue());
                vectors.add(mdl);
            }
        }
*/
        return vectors;
    }

    private static Vector middleRnd(Vector p1, Vector p2, double randomizeBy, int additional) {
        return middleRnd(p1, p2, randomizeBy, 0.5D, additional);
    }

    private static Vector middleRnd(Vector p1, Vector p2, double randomizeBy, double frac, int additional) {
        Vector r1 = p2.subtract(p1);
        Vector tst1 = r1.multiply(frac);
        Vector tst2 = tst1.add(p1);

        Random random = new Random();
        System.out.println("DEBUG: x" + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(0));
        System.out.println("DEBUG: y" + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(1));
        System.out.println(tst2.mkString(NumberFormat.getNumberInstance(), ","));
        Vector mod = Vector.fromArray(new double[] {
                tst2.get(0) + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(0) + (additional > 0 ? random.nextDouble()*(randomizeBy/10D)*r1.norm() : 0),
                tst2.get(1) + (random.nextBoolean() ? 0.5 : -0.5)*randomizeBy*random.nextDouble()*r1.get(1) + (additional < 0 ? random.nextDouble()*(randomizeBy/10D)*r1.norm() : 0)
        });
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
