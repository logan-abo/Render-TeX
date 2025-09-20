import java.io.IOException;
import java.util.Date;

public class Main {

    public static void main(String[] args)
            throws IOException {

        System.out.println(new Date());

        /*sort.addAll(figure.GeneratePoints(2000));
        System.out.println("TREE: "+Arrays.toString(sort.traverse()));
        System.out.println("TREE SIZE: "+sort.size());
        figure.setPoints(sort.traverse());*/

        Surface singleImage = new WaveFunction(0.5);
        singleImage.render(
                "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\",
                "test_image.tex",
                false,
                false,
                true,
                true
        );
        singleImage.compileFrames(
                "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\",
                "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\"
        );


//        Surface figure = new WaveFunction(0.5);
//        figure.renderRotation("C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\RawTex\\",
//                              30, 4);
//        figure.compileFrames("C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\RawTex\\",
//                             "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\CompiledTex");
//        figure.createSlideshow("pdf_animation",
//                               "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\CompiledTex");
//        figure.createVideo("video_animation",
//                           "C:\\Users\\fabou\\IdeaProjects\\RenderingBasics\\src\\main\\java\\OutputFiles\\GeneratedVideos");

        /*
        //Tree tree1 = new Tree();
        //Tree tree2 = new Tree();

        //figure.NormalDistribution(figure.GenerateGridXY(51), 2.5);
        //figure.TestShape(Surface.GenerateGridXY(50), 0.5);

        //System.out.println(Arrays.deepToString(figure.p));

        //tree1.addAll(figure.getPoints());
        //System.out.println("TREE SIZE: "+tree.size());

        //figure.setPoints(tree.left.traverse(),tree.right.traverse());
        //figure.setPoints(tree1.traverse(), tree2.traverse());
        ///figure.setPoints(tree1.traverse());

        //System.out.println("TREE: "+Arrays.toString(tree.traverse()));
        //System.out.println(Arrays.deepToString(figure.p));

        //System.out.println(Arrays.deepToString(tree.traverse()));

        //tree1.printToLaTeX();
        */

    }



}