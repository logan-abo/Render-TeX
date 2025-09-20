public class NormalDistribution extends Surface{

    @Override
    protected double surfaceFunction(double x, double y) {
        return height*Math.exp((-1)*(x*x+y*y));
    }

/*    public void surfaceFunction(double[][] ps, double height) {

        o = new double[ps.length][3];
        p = new double[ps.length][5];

        for (int i=0;i<ps.length;i++) {

            p[i][0]=ps[i][0];
            p[i][1]=ps[i][1];
            p[i][2]=height*Math.exp((-1)*(p[i][0]*p[i][0]+p[i][1]*p[i][1]));
            p[i][3]=Math.exp((-1)*(p[i][0]*p[i][0]+p[i][1]*p[i][1]));
            p[i][4]=i;
            o[i][0]=ps[i][0];
            o[i][1]=ps[i][1];

        }

    }

    public void surfaceFunction(int n, double height) {

        p = new double[n][5];

        for (int i=0;i<n;i++) {

            p[i][0]=(Math.random()*2-1)/2;
            p[i][1]=(Math.random()*2-1)/2;
            p[i][2]=height*Math.exp((-1)*(p[i][0]*p[i][0]+p[i][1]*p[i][1]));
            p[i][3]=Math.exp((-1)*(p[i][0]*p[i][0]+p[i][1]*p[i][1]));
            p[i][4]=i;

            //System.out.println(""+p[i][0]+p[i][1]+p[i][2]);

        }

    }*/

}
