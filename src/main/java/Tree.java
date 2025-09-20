import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    boolean init;
    boolean leaf;
    double[] point;
    Tree left;
    Tree right;

    public Tree() {
        init = false;
    }
    public Tree(double[] p) {
        init = true;
        leaf = true;
        point = p;
        left = null;
        right = null;
    }

    public void add(double[] p) {

        if (init) {

            if (p[2]<=point[1]) {
                if (left!=null) left.add(p);
                else { if (leaf) leaf = false; left = new Tree(p);}
            } else { //if (p[2]>point[1])
                if (right!=null) right.add(p);
                else { if (leaf) leaf = false; right = new Tree(p);}
            }

        } else { point = p; init=true; }

    }

    public void addLeft(double[] p) { left.add(p); }
    public void addRight(double[] p) { right.add(p); }

    public Tree addAll(double[][] ps) {
        for (double[] p : ps) {
            //System.out.println(p[0]);
            add(p);
        } return this;
    }

    public int size() {
        return 1+(left!=null?left.size():0)+(right!=null?right.size():0);
    }

    public double[][] traverse() {
        ArrayList<double[]> list = new ArrayList<double[]>();
        traverse(0, list);
        //System.out.println(Arrays.deepToString(list.toArray(new double[][]{})));

        double[][] output = new double[list.size()][4];
        for (int i = 0;i<list.size();i++) {
            output[i]=list.get(i);
        }
        //System.out.println(output[output.length-1][2]);
        return output;
    }

    public double[][] traverse(boolean backwards) {
        if (backwards) {
            ArrayList<double[]> list = new ArrayList<double[]>();
            traverseb(0, list);
            //System.out.println(Arrays.deepToString(list.toArray(new double[][]{})));

            double[][] output = new double[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                output[i] = list.get(i);
            }
            //System.out.println(output[output.length - 1][2]);
            return output;
        } else return traverse();
    }

    public void traverse(int n, ArrayList<double[]> l) {  /*System.out.println("IS LEAF: "+leaf+"\n"+
        (left==null?"null":left.num)+" "+(right==null?"null":right.num)+"\n\n");*//*if (leaf) return num+"  ";
        else if (left!=null) return left.traverse()+num+"  "+(right==null?"":right.traverse());
        else return num+"  "+right.traverse();*/

        if (left!=null) left.traverse(0, l);
        l.add(point);
        if (right!=null) right.traverse(0, l);

    }
    public void traverseb(int n, ArrayList<double[]> l) {  /*System.out.println("IS LEAF: "+leaf+"\n"+
        (left==null?"null":left.num)+" "+(right==null?"null":right.num)+"\n\n");*//*if (leaf) return num+"  ";
        else if (left!=null) return left.traverse()+num+"  "+(right==null?"":right.traverse());
        else return num+"  "+right.traverse();*/

        if (right!=null) right.traverse(0, l);
        l.add(point);
        if (left!=null) left.traverse(0, l);

    }

    public String print() {
        return (left!=null?left.print():"")+(int)(point[1]*100)+"  "+(right!=null?right.print():"");
    }

    public void printToLaTeX() {

        try {
            FileWriter file = new FileWriter("C:/Users/fabou/IdeaProjects/RenderingBasics/src/LaTeXFiles/TreeGraph/TreeVisual.tex");
            file.write("""
                          \\documentclass{report}
                          \\usepackage{tikz-qtree}
                          
                          \\begin{document}
                          
                          \\title{%
                          RANDOMLY GENERATED TREE \\\\
                          """+
                    "\\large "+traverse()+" \\\\}\n"
                    +"""
                          \\author{Logan Abounader}
                          \\date{\\today}
                          \\maketitle
                          
                          \\begin{center}
                          \\begin{tikzpicture}
                          
                          \\Tree
                          """+
                    printToLaTeX(0)
                    +"""
                          \\end{tikzpicture}
                          \\end{center}
                          
                          \\end{document}
                          """);
            file.close();
        } catch (IOException ignored) {}

    }

    public String printToLaTeX(int layer) {

        //System.out.println(layer);

        /*return ("[.{$"+point[2]+"$ } \n" +
                (left==null?"":left.printToLaTeX(layer+1)+"\n") +
                (right==null?"":right.printToLaTeX(layer+1)+"\n") +
                "]");*/

        return "[.{$"+point[2]+"$ } ]\n";

    }

}