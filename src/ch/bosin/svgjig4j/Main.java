package ch.bosin.svgjig4j;

import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.linear.GaussianSolver;
import org.la4j.linear.LinearSystemSolver;
import org.la4j.vector.SparseVector;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

    private final Vector startPoint, endPoint, unitVectorX, unitVectorY;

    public Main(final Vector startPoint, final Vector endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.unitVectorX = this.endPoint.subtract(startPoint);
        this.unitVectorY = this.unitVectorX.multiply(Matrix.from1DArray(2,2,new double[]{0,1,-1,0}));
    }

    private String endPointHelper(final Vector endPoint, final String letter) {
        return letter + this.vecToString(endPoint);
    }

    /**
     * Converts a vector into a string with a specified delimiter
     * @param vector Vector to be converted into a string
     * @param delimiter Delimiter as String
     * @return a String with all coordinates delimited by the given delimiter
     */
    public String vecToString(final Vector vector, final String delimiter) {
        return vector.mkString(NumberFormat.getNumberInstance(), delimiter);
    }

    /**
     * Converts a vector into a string with "," as delimiter
     * @param vector Vector to be converted into a string
     * @return a String with all coordinates delimited by a ","
     */
    public String vecToString(final Vector vector){
        return vector.mkString(NumberFormat.getNumberInstance(), ",");
    }

    /**
     * Converts relative curve coordinates into an absolute point
     * @param x Relative coordinate in direct direction
     * @param y Relative coordinate in perpendicular direction
     * @return absolute point as a Vector
     */
    public Vector curvePoint(final double x, final double y) {
        return this.startPoint.add(this.unitVectorX.multiply(x).subtract(this.unitVectorY.multiply(y)));
    }

    /**
     * Converts an absolute coordinate vector array to an svg curve
     * @param curvePoints an absolute coordinate Vector array containing three vectors where the first vector is the control point of the
     *                    start point, the second vector is the control point of the end point and the third vector is
     *                    the end point itself.
     * @return a String in the format "Cx1,y1 x2,y2 x,y"
     */
    public String curveTo(final Vector...curvePoints) {
        return "C" + String.join(" ", Arrays.stream(curvePoints).map(this::vecToString).collect(Collectors.toList()));
    }

    /**
     * Converts an absolute coordinate vector to an svg move to command
     * @param endPoint an absolute coordinate Vector of the cursor point
     * @return a String in the format "Mx,y"
     */
    public String moveTo(final Vector endPoint) {
        return this.endPointHelper(endPoint, "M");
    }

    /**
     * Moves the svg cursor using relative coordinates
     * @param x Relative coordinates of cursor point in direct direction
     * @param y Relative coordinates of cursor point in perpendicular direction
     * @return a String in the format "Mx,y"
     */
    private String moveTo(final double x, final double y) {
        return this.moveTo(this.curvePoint(x, y));
    }

    /**
     * Creates an svg curve using relative coordinates
     * @param x1 Relative coordinates of control point of start point in direct direction
     * @param y1 Relative coordinates of control point of start point in perpendicular direction
     * @param x2 Relative coordinates of control point of end point in direct direction
     * @param y2 Relative coordinates of control point of end point in perpendicular direction
     * @param x Relative coordinates of end point in direct direction
     * @param y Relative coordinates of end point in perpendicular direction
     * @return a String in the format "Cx1,y1 x2,y2 x,y"
     */
    private String curveTo(final double x1, final double y1, final double x2, final double y2, final double x, final double y) {
        return this.curveTo(this.curvePoint(x1, y1), this.curvePoint(x2, y2), this.curvePoint(x, y));
    }

    /**
     * Converts an absolute coordinate vector to an svg line
     * @param endPoint an absolute coordinate Vector of the end point of the line
     * @return a String in the format "Lx,y"
     */
    private String lineTo(final Vector endPoint) {
        return this.endPointHelper(endPoint, "L");
    }

    /**
     * Creates an svg line using relative coordinates
     * @param x Relative coordinates of end point in direct direction
     * @param y Relative coordinates of end point in perpendicular direction
     * @return a String in the format "Lx,y"
     */
    private String lineTo(final double x, final double y) {
        return this.lineTo(this.curvePoint(x, y));
    }

    public void run() {
        // DEBUG: Prints start and end point and the unit vectors
        System.out.println("A(" + this.vecToString(this.startPoint, "|") + ")");
        System.out.println("B(" + this.vecToString(this.endPoint, "|") + ")");
        System.out.println("u=[" + this.vecToString(this.unitVectorX, ";") + "]");
        System.out.println("v=[" + this.vecToString(this.unitVectorY, ";") + "]");

        // Creates a new path
        StringBuilder path = new StringBuilder();

        // Moves cursor to given start point (0|0)
        path.append(this.moveTo(0.00, 0.00));
        // Draws a line to (0.3|0)
        path.append(this.lineTo(0.40, 0.00));

        // Draws a curve to (0.23|0.2)
        path.append(this.curveTo(
                0.475, 0.00,
                0.39, 0.05,
                0.365, 0.10
        ));
        // Draws a curve to (0.33|0.4)
        path.append(this.curveTo(
                0.345, 0.14,
                0.375, 0.18,
                0.415, 0.20
        ));
        // Draws a curve to (0.67|0.4)
        path.append(this.curveTo(
                0.465, 0.225,
                0.535, 0.225,
                0.585, 0.20
        ));
        // Draws a curve to (0.77|0.2)
        path.append(this.curveTo(
                0.625, 0.18,
                0.655, 0.14,
                0.635, 0.10
        ));
        // Draws a curve to (0.7|0)
        path.append(this.curveTo(
                0.61, 0.05,
                0.525, 0.00,
                0.60, 0.00
        ));

        // Draws a line to given end point (1|0)
        path.append(this.lineTo(endPoint));

        // DEBUG: Prints out the d argument of the svg path
        System.out.println(path.toString());
    }

    public static void main(String[] args) {
        int width = 1000;
        int height = 600;
        double randomizeBy = 0.1;

        SparseVector startPoint = SparseVector.fromArray(new double[] {0, 0});
        SparseVector endPoint = SparseVector.fromArray(new double[] {width, height});

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

    private static Vector middleRnd(Vector p1, Vector p2, double randomizeBy) {
        Vector r1 = p2.subtract(p1);
        Vector tst1 = r1.divide(2);
        Vector tst2 = tst1.add(p1);

        Random random = new Random();
        Vector mod = Vector.fromArray(new double[] {(random.nextBoolean() ? -1 : 1) * random.nextDouble() * randomizeBy * (p2.get(0) - p1.get(0)), (random.nextBoolean() ? -1 : 1) * random.nextDouble() * randomizeBy * (p2.get(1) - p1.get(1))});

        Vector out = tst2.add(mod);
        return out;
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
