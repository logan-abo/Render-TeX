public class SaddleFunction extends Surface{

    protected double surfaceFunction(double x, double y) {
        return height*(x*x-y*y);
    }

//    public void surfaceFunction(double[][] ps, double height) {
//
//        o = new double[ps.length][3];
//        p = new double[ps.length][5];
//
//        for (int i=0;i<ps.length;i++) {
//
//            p[i][0]=ps[i][0];
//            p[i][1]=ps[i][1];
//            p[i][2]=height*(p[i][0]*p[i][0]-p[i][1]*p[i][1]);
//            p[i][3]=(p[i][0]*p[i][0]-p[i][1]*p[i][1]);
//            p[i][4]=i;
//            o[i][0]=ps[i][0];
//            o[i][1]=ps[i][1];
//
//        }
//
//    }

}
