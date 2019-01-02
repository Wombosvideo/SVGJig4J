package ch.bosin.svgjig4j;

import org.la4j.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        int width = 1000;
        int height = 600;

        int piecesX = 8; // aka. rows
        //int piecesX = 2; // aka. rows
        int piecesY = 6; // aka. columns
        //int piecesY = 2; // aka. columns

        double randomizeBy = 0.1D;

        IndexBased ib = new IndexBased(width, height, piecesX, piecesY);
        try {
            ib.setup(randomizeBy);
        } catch (NullPointerException e) {
            e.printStackTrace();
            ib.printNonNull();
        }

        ArrayList<String> output = new ArrayList<>();
/*
        for(int x = 0; x <= piecesX; x++) {
            for(int y = 0; y <= piecesY; y++) {
                Vector v = ib.vec[x][y];
                if(v != null)
                output.add("<circle cx=\"" + v.get(0) + "\" cy=\"" + v.get(1) + "\" r=\"" + (((width/(piecesX-1) + height/(piecesY-1))/2)/12) + "\" class=\"c\"/>\n");
            }
        }
*/
        output.addAll(ib.makeConnections());
        output.add(ib.outerLines());

        writeSVG(output, "test", width, height);
    }

    private static void writeSVG(List<String> paths, String releaseName, int width, int height) {
        // Write to file
        String svgData = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + width + " " + height + "\">\n" +
                "  <defs>\n" +
                "    <style>\n" +
                "      .a {\n" +
                "        fill: none;\n" +
         //     "        stroke: #e52421;\n" +
                "        stroke: #000000;\n" +
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

}
