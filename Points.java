/**
 * Created by yinningliu on 17/2/12.
 */
public class Points {
    /**
     * private x value
     */
    private double x;

    /**
     *
     * private y value
     */
    private double y;
    public Points(){};

    /**
     *
     * @param x
     *   x-coordinate
     * @param y
     *   y-coordinate
     * @postcondition
     *   set up points' x and y
     */
    public Points(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * @return
     *      return point's x
     */
    public double getX(){
        return x;
    }

    /**
     * @return
     *      return point's y
     */
    public double getY(){
        return y;
    }
}
