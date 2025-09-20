public class Plane extends Surface {

    protected double surfaceFunction(double x, double y) {
        return 0;
    }

//    public void surfaceFunction(double[][] ps, double height) {
//
//        o = new double[ps.length][3];
//        p = new double[ps.length][5];
//
//        for (int i=0;i<ps.length;i++) {
//
//            //PARABOLA: (p[i][0]*p[i][0]+p[i][1]*p[i][1])
//            //DIAGONAL COSINE: (Math.cos(p[i][0]+p[i][1])+1)
//
//            p[i][0]=ps[i][0];
//            p[i][1]=ps[i][1];
//            p[i][2]=height*0;
//            p[i][3]=0;
//            p[i][4]=i;
//            o[i][0]=ps[i][0];
//            o[i][1]=ps[i][1];
//
//        }
//
//    }

}
