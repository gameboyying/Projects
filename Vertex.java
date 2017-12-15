/**
 * Created by yinningliu on 3/18/17.
 */
public class Vertex {

    // x coordinates
    private double x;

    // y coordinates
    private double y;

    // value of that Vertex
    private String value;


    // construct Vertex
    public Vertex(double x, double y, String value){
        this.x = x;
        this.y = y;
        this.value = value;
    }

    // return value
    public String getValue(){
        return value;
    }

    // return x
    public double getX(){
        return x;
    }

    // return y
    public double getY(){
        return y;
    }

    // set up value
    public void setValue(String value){
        this.value = value;
    }

    // set up X
    public void setX(double x){
        this.x= x;
    }

    // set up Y
    public void setY(double y){
        this.y = y;
    }

}

