package ch.bosin.svgjig4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Size of puzzle in pixels
        int width = 1000;
        int height = 600;

        // Amount of pieces
        int piecesX = 8; // aka. rows
        int piecesY = 6; // aka. columns

        // Randomization multiplier (best results using 0.1D)
        double randomizeBy = 0.1D;


        IndexBased puzzleGrid = new IndexBased(width, height, piecesX, piecesY);
        try {
            puzzleGrid.setup(randomizeBy);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        ArrayList<String> output = new ArrayList<>();
        output.addAll(puzzleGrid.makeConnections());
        output.add(puzzleGrid.outerLines());

        // Save generated puzzle grid to file
        writeSVG(output, "test", width, height);
    }

    /**
     * Writes a list of paths to a file
     * @param paths list of paths
     * @param releaseName file name of output
     * @param width svg width
     * @param height svg height
     */
    private static void writeSVG(List<String> paths, String releaseName, int width, int height) {
        // Write to file
        String svgData = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + width + " " + height + "\">\n" +
                "  <defs>\n" +
                "    <style>\n" +
                "      .a {\n" +
                "        fill: none;\n" +
                "        stroke: #000000;\n" +
                "        stroke-miterlimit: 10;\n" +
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

}
