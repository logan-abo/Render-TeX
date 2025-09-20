public class WaveFunction extends Surface {

    public WaveFunction() {
        super();
    }
    public WaveFunction(double height) {
        super(height);
    }

    @Override
    protected double surfaceFunction(double x, double y) {

        return Math.min((height*Math.sin(x*5+1)*Math.sin(y*5+1)), 0.2);

    }

}

//        // o = new double[ps.length][3];
//        // p = new double[ps.length][5];
//
//        for (int i=0;i<ps.length;i++) {
//
//            p[i][0] = ps[i][0];
//            p[i][1] = ps[i][1];
//            p[i][2] = Math.min((height*Math.sin(p[i][0]*5+1)*Math.sin(p[i][1]*5+1)), 0.2);
//
//            // p[i][3] = Math.sin(p[i][0]*5+1)*Math.sin(p[i][1]*5+1);
//
//            //p[i][4] = i;
//
//            o[i][0] = ps[i][0];
//            o[i][1] = ps[i][1];
//
//        }
