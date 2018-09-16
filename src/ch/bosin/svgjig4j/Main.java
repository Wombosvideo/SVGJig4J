package ch.bosin.svgjig4j;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.vector.SparseVector;

import java.text.NumberFormat;
import java.util.Arrays;
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
        path.append(this.lineTo(0.30, 0.00));

        // Draws a curve to (0.23|0.2)
        path.append(this.curveTo(
                0.45, 0.00,
                0.28, 0.10,
                0.23, 0.20
        ));
        // Draws a curve to (0.33|0.4)
        path.append(this.curveTo(
                0.19, 0.28,
                0.25, 0.36,
                0.33, 0.40
        ));
        // Draws a curve to (0.67|0.4)
        path.append(this.curveTo(
                0.43, 0.45,
                0.57, 0.45,
                0.67, 0.40
        ));
        // Draws a curve to (0.77|0.2)
        path.append(this.curveTo(
                0.75, 0.36,
                0.81, 0.28,
                0.77, 0.20
        ));
        // Draws a curve to (0.7|0)
        path.append(this.curveTo(
                0.72, 0.10,
                0.55, 0.00,
                0.70, 0.00
        ));

        // Draws a line to given end point (1|0)
        path.append(this.lineTo(endPoint));

        // DEBUG: Prints out the d argument of the svg path
        System.out.println(path.toString());
    }

    public static void main(String[] args) {
        SparseVector startPoint = SparseVector.fromArray(new double[]{0,0});
        SparseVector endPoint = SparseVector.fromArray(new double[]{0,100});

        new Main(startPoint, endPoint).run();
    }
}
