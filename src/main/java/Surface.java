import java.io.*;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

public abstract class Surface {

    protected double xDeg = -45;
    protected double yDeg = 0;
    protected double zDeg = 30;


    protected int width = 30;
    protected double height = 1;

    protected double[][] domain;

    protected double[][] p = new double[][]{}; //range
    protected double[][] o = new double[][]{}; //PLANE ORIENTATION


    public Surface() {
        domain = generateGridXY(width);
    }
    public Surface(double height) {
        this.height = height;
        domain = generateGridXY(width);
    }
    public Surface(double[][] base) {
        domain = base;
    }


    public static double[][] GenerateRandomXY(int n) {

        double[][] points = new double[n][2];

        for (int i=0;i<n;i++) {
            points[i][0] = (Math.random() * 2 - 1) / 2;
            points[i][1] = (Math.random() * 2 - 1) / 2;
        }

        return points;

    }
    public static double[][] generateGridXY(int w) {

        double[][] points = new double[w*w][2];

        for (double i=0; i<w; i++) {
            for (double j=0; j<w; j++) {
                points[(int)(j+w*i)] = new double[]{j/(w-1)-0.5, i/(w-1)-0.5};
            }
        }

        return points;
    }


    protected abstract double surfaceFunction(double x, double y);


    public void createSurface() {

        o = new double[domain.length][3];
        p = new double[domain.length][5];

        for (int i=0 ; i<domain.length ; i++) {

            p[i][0]=domain[i][0];
            p[i][1]=domain[i][1];
            p[i][2]= this.surfaceFunction(domain[i][0], domain[i][1]);

            //p[i][3]=Math.sin(p[i][0]*5+1)*Math.sin(p[i][1]*5+1);

            p[i][4]=i;

            o[i][0]=domain[i][0];
            o[i][1]=domain[i][1];

        }

    }

    public void applyTransformations() {

        createSurface();

        double[] P;
        double[] O;

        for (int i=0; i<p.length; i++) {
            /*d[0] = r[0][0]*d[0]+r[0][1]*d[1]+r[0][2]*d[2];
            d[1] = r[1][0]*d[0]+r[1][1]*d[1]+r[1][2]*d[2];
            d[2] = r[2][0]*d[0]+r[2][1]*d[1]+r[2][2]*d[2];*/

            double[] a = new double[]{ Math.cos(zDeg*Math.PI/180),
                                       Math.sin(zDeg*Math.PI/180) };
            double[] b = new double[]{ Math.cos(xDeg*Math.PI/180),
                                       Math.sin(xDeg*Math.PI/180) };
            double[] c = new double[]{ Math.cos(yDeg*Math.PI/180),
                                       Math.sin(yDeg*Math.PI/180) };

            // Z-axis
            P = new double[] {p[i][0],p[i][1],p[i][2]};
            p[i][0]=b[0]*P[0]-b[1]*P[1];
            p[i][1]=b[1]*P[0]+b[0]*P[1];

            O = new double[] {o[i][0],o[i][1],o[i][2]};
            o[i][0]=b[0]*O[0]-b[1]*O[1];
            o[i][1]=b[1]*O[0]+b[0]*O[1];


            // X-axis
            P = new double[] {p[i][0],p[i][1],p[i][2]};
            p[i][1]=a[0]*P[1]+a[1]*P[2];
            p[i][2]=a[0]*P[2]-a[1]*P[1];

            O = new double[] {o[i][0],o[i][1],o[i][2]};
            o[i][1]=a[0]*O[1]+a[1]*O[2];
            o[i][2]=a[0]*O[2]-a[1]*O[1];

            // Y-axis
            P = new double[] {p[i][0],p[i][1],p[i][2]};
            p[i][0] = c[0]*P[0] + c[1]*P[2];
            p[i][2] = c[0]*P[2] - c[1]*P[0];

            O = new double[] {o[i][0],o[i][1],o[i][2]};
            o[i][0] = c[0]*O[0] + c[1]*O[2];
            o[i][2] = c[0]*O[2] - c[1]*O[0];

        }

    }


    public void renderRotation(String directory,
                               int fps,
                               int seconds) {

        int frames = fps*seconds;
        int numDigits = String.valueOf(frames).length()+1;

        String prefix = String.valueOf(
                LocalTime.now().getNano()*LocalTime.now().getNano()
        ); //sudo-random

        for (int i=0 ; i<frames ; i++) {

            String frame_number = "0".repeat(numDigits-String.valueOf(i).length()) + i;
            String filename = frame_number + "_" + prefix + ".tex";

            try {

                render(directory, filename,
                        false, true, true, true);

                yDeg += (360.0/frames);
                //this.rotate(360.0 / frames * i);

            } catch (IOException e) {
                System.out.println(
                        "Frame " + i + " failed with IOException.\n"+
                        "   Check for file " + filename);
            }
        }
    }

    public void compileFrames(String sourceDir,
                              String outputDir) {

        Path sourcePath = Paths.get(sourceDir);
        Path outputPath = Paths.get(outputDir);

        List<Path> texFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(sourcePath, 1)) {
            texFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".tex"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading files from source directory.");
            return;
        }

        for (Path texFile : texFiles) {

            String fileName = texFile.getFileName().toString();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

            try {

                boolean success = compileLatex(texFile,
                                               sourceDir);

                if (success) {

                    Path sourcePdfPath = sourcePath.resolve(baseName + ".pdf");
                    Path destinationPdfPath = outputPath.resolve(baseName + ".pdf");

                    if(Files.exists(sourcePdfPath)) {

                        Files.move(sourcePdfPath, destinationPdfPath, StandardCopyOption.REPLACE_EXISTING);

                    } else {
                        System.err.println("PDF file not found after compilation: " + sourcePdfPath);
                    }

                } else {
                    System.err.println("Failed to compile " + fileName);
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("An error occurred while processing " + fileName);
            }
        }

    }
    public void createSlideshow(String filename,
                                String pdfsDir) {

        Path pdfsPath = Paths.get(pdfsDir);

        try {
            combinePdfs(pdfsPath, filename);
        } catch (IOException e) {
            System.err.println("Failed to combine pdf frames into "+filename);
        }
    }

    public void createVideo(String filename,
                            String outputDir) {

    }

    private static boolean compileLatex(Path texFilePath,
                                        String outputDir)
            throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command(
                "pdflatex",
                "-interaction=nonstopmode",
                "-output-directory=" + outputDir,
                texFilePath.toAbsolutePath().toString()
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {}
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private static void combinePdfs(Path directoryPath, String outputFileName)
            throws IOException {

        Path finalPdfPath = directoryPath.resolve(outputFileName+".pdf");

        List<File> pdfFiles;
        try (Stream<Path> paths = Files.list(directoryPath)) {
            pdfFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                    .map(Path::toFile)
                    .filter(file -> !file.getName().equals(outputFileName))
                    .sorted()
                    .collect(Collectors.toList());
        }

        System.out.println("Found " + pdfFiles.size() + " PDFs to merge into " + outputFileName);

        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(finalPdfPath.toString());

        for (File pdfFile : pdfFiles) {
            pdfMerger.addSource(pdfFile);
        }

        pdfMerger.mergeDocuments(null);
    }

    private double[][] sortPolygons() {

        Tree t = new Tree();
        for (int i=0; i<width-1; i++) {
            for (int j=0; j<width-1; j++) {
                t.add(p[i*width+j]);
            }
        }
        return t.traverse();

    }

    public void render(String directory,
                       String filename,
                       boolean vertices,
                       boolean edges,
                       boolean faces,
                       boolean colored) throws IOException {

        createSurface();
        applyTransformations();
        double[][] z = sortPolygons();

        FileWriter file = new FileWriter(directory+filename);
        file.write("""
                
                \\documentclass{article}
                
                \\usepackage[utf8]{inputenc}
                \\usepackage{tikz}
                \\usepackage[paperheight=6in,paperwidth=10in]{geometry}
                                    
                \\begin{document}
                
                \\break
                                    
                \\begin{tikzpicture}
                                    
                """+
                renderToLaTeX(vertices, edges, faces, z, colored)
                + """
                
                \\end{tikzpicture}
                
                \\end{document}
                """);
        file.close();
    }

    public String renderToLaTeX(boolean vertices,
                                boolean edges,
                                boolean faces,
                                double[][] z,
                                boolean colored) {
        String syntax = "";
        for (int i =0; i<z.length; i++) {

            /*
            syntax += "\\fill [fill={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}]("+p[i][0]+","+p[i][1]+") circle (0.75pt);\n";
            */ //THICK POINTED CURVE
            /*
            syntax += "\\fill [fill=red!"+((p[i][1]+1)*30)+"!green]("+p[i][0]+","+p[i][1]+") circle (1pt);\n";
            */ //THICK POINTED CURVE (RED)
            /*
            syntax += "\\draw [color={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}, line width=0.05mm] ("+p[i][0]+","+p[i][1]+")--("+p[i][0]+","+(o[(int)p[i][4]][1]+1)+");\n";syntax += "\\fill [fill=red!"+((p[i][1])*20)+"!green!"+((p[i][0]+1)*20)+"] ("+p[i][0]+","+p[i][1]+") circle (1pt);\n"+
                                  "\\draw [line width=0.75mm, red!"+((p[i][1])*20)+"!green!"+((p[i][0]+1)*20)+"] ("+p[i][0]+","+p[i][1]+")--("+p[i][0]+","+(p[i][1]-0.3)+");\n";
            */ //SHELL OVER VOLUME (different colors)
            /*
            syntax += "\\fill [fill={rgb:red,"+((p[i][1]-p[0][1])*50)+
                    ";green,"+((-p[i][1]+p[p.length-1][1])*10)+
                    ";blue,"+((p[i][3]+1)*10)+"}]"+
                    "("+p[i][0]+","+p[i][1]+") "+
                    "circle ("+(0.2*(p[i][2]-p[0][2]))+"pt);\n";
            */ //CURRENT
            /*
            syntax += "\\fill [fill=black]"+
                    "("+p[i][0]+","+p[i][1]+") "+
                    "circle ("+(0.5*(p[i][2]-p[0][2]))+"pt);\n";
            */ //BLACK POINTS SIZE Z DEPENDENT
            /*
            syntax += "\\draw [color={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}, line width=0.05mm] "+
                                                                                                    "("+p[i][0]+","+p[i][1]+")"+
                                                                                                  "--("+p[i+1][0]+","+(p[i+1][1])+")"+
                                                                                                  "--("+p[i+2][0]+","+(p[i+2][1])+")"+
                                                                                                  "--cycle;\n";
            */ //SHADED FAILED POLYGONS
            /*
            syntax += "\\draw [color={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}, line width=0.05mm] "+
                                                                                                    "("+p[i][0]+","+p[i][1]+")"+
                                                                                                  "--("+p[i+1][0]+","+(p[i+1][1])+");\n";
            */ //UNKNOWN
            /*
            syntax += "\\fill [fill={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}]"+
                    "("+p[i][0]+","+p[i][1]+") "+
                    "circle ("+(0.3*(p[i][2]-p[0][2]))+"pt);\n";
            */ // SHADED POINTS SIZE Z DEPENDENT
            /*syntax += "\\fill [fill={rgb:red,"+((p[i][2]+1)*100)+
                                       ";green,"+(((-p[i][1]+1))*100)+
                                       ";blue,"+((p[i][0]+1)*100)+"}]"+
                                       "("+(p[i][0]*5)+","+(p[i][1]*5)+") "+
                                "circle ("+(5*0.1*(p[i][2]-p[0][2]+1))+"pt);\n";
            */ //SHADED POINTS SIZE Z DEPENDENT (GOOD POINTS)

            double[] cur = p[ (int)(z[i][4]) ];    // POLYGONS CURRENT --v

            // double[][] grid = generateGridXY(width);

            String color;

            if (colored) {
//                color = "{rgb:red,"+((cur[2]+1)*100)+
//                        ";green,"+(((-cur[2]+1))*100)+
//                        ";blue,"+((cur[1]+1)*100)+"}";
                color = "{rgb:red,"+((cur[1]+1)*100)+
                        ";green,40"+
                        ";blue,40"+"}";
            } else {
                color = "red!"+( 69-( ( domain[(int)z[i][4]][1]+0.5 )*70 + ( p[(int)z[i][3]][0]+0.5 )*0 ) )+"!white";
                //color = "blue!"+( 100-((z[i][3]+0.5)*70) )+"!white";
                //color = "blue!"+( 25+100*(cur[3]) )+"!white";
            }

            //color = "blue!"+( ( grid[(int)z[i][4]][1]+0.5 )*70 + ( p[(int)z[i][3]][0]+0.5 )*0 )+"!white";

            if (vertices) {

                syntax += "\\fill [fill="+color+"]"+
                        "("+ p[(int)(cur[4])][0]*5 +","+ p[(int)(cur[4])][1]*5 +") "+
                        "circle ("+(5*0.1*(cur[2]-z[0][2]+1))+"pt);\n";

// (circle?(domain[(int)cur[4]][0]*domain[(int)cur[4]][0]+domain[(int)cur[4]][1]*domain[(int)cur[4]][1]):0.25)<=0.25

            } else {

                syntax += "\\draw [" + "color=" + (edges ? "black" : color) + (faces ? "," + "fill=" + color : "") + "] " +
                        "(" + cur[0] * 5 + "," + cur[1] * 5 + ")--" +
                        "(" + p[(int) (cur[4] + 1)][0] * 5 + "," + p[(int) (cur[4] + 1)][1] * 5 + ")--" +
                        "(" + p[(int) (cur[4] + width)][0] * 5 + "," + p[(int) (cur[4] + width)][1] * 5 + ")--cycle;\n";

                cur = p[(int) (z[i][4]) + 1];

                /*
                if (colored) {
                    color = "{rgb:red," + ((cur[2] + 1) * 100) +
                            //";green," + (((-cur[1] + 1)) * 100) +
                            ";blue," + ((cur[0] + 1) * 70) + "}";
                } else {
                    color = "blue!"+( 69-( (grid[(int)z[i][4]][1]+0.5)*70 ) )+"!white";
                    //color = "blue!"+( 100-((z[i][3]+0.5)*70) )+"!white";
                    color = "blue!"+( 25+100*(cur[3]) )+"!white";
                }
                 */

                syntax += "\\draw [" + "color=" + (edges ? "black" : color) + (faces ? "," + "fill=" + color : "") + "] " +
                        "(" + p[(int) (cur[4])][0] * 5 + "," + p[(int) (cur[4])][1] * 5 + ")--" +
                        "(" + p[(int) (cur[4] + width - 1)][0] * 5 + "," + p[(int) (cur[4] + width - 1)][1] * 5 + ")--" +
                        "(" + p[(int) (cur[4] + width)][0] * 5 + "," + p[(int) (cur[4] + width)][1] * 5 + ")--cycle;\n";

            }

            /*syntax += "\\fill [fill={rgb:red,"+((z[i][2]+1)*100)+
                    ";green,"+(((-z[i][1]+1))*100)+
                    ";blue,"+((z[i][0]+1)*100)+"}]"+
                    "("+(z[i][0]*5)+","+(z[i][1]*5)+") "+
                    "circle ("+(5*0.1*(z[i][2]-z[0][2]+1))+"pt);\n";
            */ //GOOD POINTS w/ new poly method

        } /*for (int i=0; i<o.length/2; i++) {

            syntax += "\\fill [fill={rgb:red,"+((p[i][0]+1)*100)+";green,"+(((-p[i][0]+1))*100)+";blue,"+((p[i][3])*120)+"}]("+o[i][0]+","+o[i][1]+") circle (0.75pt);\n";

        }*/

        return syntax;

    }

    public void setPoints(double[][] nps) {
        for (int i=0;i<p.length;i++) p[i] = nps[i];
        for (int i=p.length;i<nps.length;i++) {
            o[i-p.length][0] = nps[i][0];
            o[i-p.length][1] = nps[i][1];
            o[i-p.length][2] = nps[i][2];
        }
    }

    public void setPoints(double[][] nps, int numP) {
        for (int i=0;i<numP;i++) p[i] = nps[i];
        for (int i=numP;i<nps.length;i++) {
            o[i-numP][0] = nps[i][0];
            o[i-numP][1] = nps[i][1];
            o[i-numP][2] = nps[i][2];
        }
    }

    public void setPoints(double[][] nps, double[][] org) {
        p = nps;
        o = org;
    }

    /*public double[][][] getPoints() {
        double[][][] all = new double[2][][];
        all[0] = p;
        all[1] = o;
        return all;
    }*/

    public double[][] getPoints() {
        return p;
    }

}
