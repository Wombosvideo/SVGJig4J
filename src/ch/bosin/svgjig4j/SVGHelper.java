package ch.bosin.svgjig4j;

import org.la4j.Matrix;
import org.la4j.Vector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SVGHelper {

    private final Vector startPoint, endPoint, unitVectorX, unitVectorY;
    private String id;
    private StringBuilder path;
    public final int width, height;
    private static final NumberFormat NUMFORM = new DecimalFormat("#.###");

    /**
     * Constructs an SVG helper class without support for relative coordinates
     */
    public SVGHelper() {
        this(Vector.fromArray(new double[]{0,0}), Vector.fromArray(new double[]{0,0}));
    }

    /**
     * Constructs an SVG helper class with support for relative coordinates
     * @param startPoint start point for relative coordinates
     * @param endPoint end point for relative coordinates
     */
    public SVGHelper(final Vector startPoint, final Vector endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        // Unit vectors for relative coordinates
        this.unitVectorX = this.endPoint.subtract(startPoint);
        this.unitVectorY = this.unitVectorX.multiply(Matrix.from1DArray(2,2,new double[]{0,1,-1,0}));
        // Width and height are calculated by horizontal and vertical distance between start and end point
        this.width = Double.valueOf(Math.ceil(this.endPoint.subtract(this.startPoint).get(0))).intValue();
        this.height = Double.valueOf(Math.ceil(this.endPoint.subtract(this.startPoint).get(1))).intValue();
    }

    /**
     * @param vector vector to be appended after letter
     * @param letter letter to be prepended before vector
     * @return letter and vector
     */
    private String endPointHelper(final Vector vector, final String letter) {
        return letter + this.vecToString(vector);
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
        return vector.mkString(NUMFORM, ",").replaceAll("’", "");
    }

    /**
     * Converts relative curve coordinates into an absolute point
     * @param x relative coordinate in direct direction
     * @param y relative coordinate in perpendicular direction
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
        if(this.path != null)
            path.append("C" + String.join(" ", Arrays.stream(curvePoints).map(this::vecToString).collect(Collectors.toList())));
        return "C" + String.join(" ", Arrays.stream(curvePoints).map(this::vecToString).collect(Collectors.toList()));
    }

    /**
     * Creates a cubic svg curve to end point via control point
     * @param controlPoint control point of cubic curve
     * @param endPoint end point of cubic curve
     * @return a String in the format "Qx1,y1 x,y"
     */
    public String cubicCurveTo(final Vector controlPoint, final Vector endPoint) {
        if(this.path != null)
            path.append("Q" + vecToString(controlPoint) + "," + vecToString(endPoint));
        return "Q" + vecToString(controlPoint) + "," + vecToString(endPoint);
    }

    /**
     * Converts an absolute coordinate vector to an svg move to command
     * @param endPoint an absolute coordinate Vector of the cursor point
     * @return a String in the format "Mx,y"
     */
    public String moveTo(final Vector endPoint) {
        if (this.path != null)
            path.append(this.endPointHelper(endPoint, "M"));
        return this.endPointHelper(endPoint, "M");
    }

    /**
     * Moves the svg cursor using relative coordinates
     * @param x Relative coordinates of cursor point in direct direction
     * @param y Relative coordinates of cursor point in perpendicular direction
     * @return a String in the format "Mx,y"
     */
    public String moveTo(final double x, final double y) {
        return this.moveTo(this.curvePoint(x, y));
    }

    /**
     * Creates an svg curve using relative coordinates
     * @param x1 relative coordinates of control point of start point in direct direction
     * @param y1 relative coordinates of control point of start point in perpendicular direction
     * @param x2 relative coordinates of control point of end point in direct direction
     * @param y2 relative coordinates of control point of end point in perpendicular direction
     * @param x relative coordinates of end point in direct direction
     * @param y relative coordinates of end point in perpendicular direction
     * @return a String in the format "Cx1,y1 x2,y2 x,y"
     */
    public String curveTo(final double x1, final double y1, final double x2, final double y2, final double x, final double y) {
        return this.curveTo(this.curvePoint(x1, y1), this.curvePoint(x2, y2), this.curvePoint(x, y));
    }

    /**
     * Converts an absolute coordinate vector to an svg line
     * @param endPoint an absolute coordinate Vector of the end point of the line
     * @return a String in the format "Lx,y"
     */
    public String lineTo(final Vector endPoint) {
        if(this.path != null)
            path.append(this.endPointHelper(endPoint, "L"));
        return this.endPointHelper(endPoint, "L");
    }

    /**
     * Creates an svg line using relative coordinates
     * @param x relative coordinates of end point in direct direction
     * @param y relative coordinates of end point in perpendicular direction
     * @return a String in the format "Lx,y"
     */
    public String lineTo(final double x, final double y) {
        return this.lineTo(this.curvePoint(x, y));
    }

    /**
     * Creates a new SVG path
     */
    public void startPath() {
        this.path = new StringBuilder();
    }

    /**
     * Returns the currently generated path
     * @return currently generated path
     */
    public String getPath() {
        return "<path " + (this.id != null && this.id != "" ? "id=\"" + this.id + "\" " : "") + "class=\"a\" d=\"" + this.path.toString() + "\"/>\n";
    }

    /**
     * Returns the currently generated path and clears it from memory
     * @return currently generated path
     */
    public String getPathAndClear() {
        String path = this.getPath();
        this.path.delete(0, this.path.length());
        this.path = null;
        return path;
    }

    /**
     * Returns end point of relative coordinates
     * @return end point of relative coordinates
     */
    public Vector getEndPoint() {
        return endPoint;
    }

    /**
     * Returns start point of relative coordinates
     * @return start point of relative coordinates
     */
    public Vector getStartPoint() {
        return startPoint;
    }

    /**
     * Returns horizontal unit vector for relative coordinates
     * @return horizontal unit vector
     */
    public Vector getUnitVectorX() {
        return unitVectorX;
    }

    /**
     * Returns vertical unit vector for relative coordinates
     * @return vertical unit vector
     */
    public Vector getUnitVectorY() {
        return unitVectorY;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
