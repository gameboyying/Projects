/**
 * Created by yinningliu on 3/18/17.
 */
public class Graph {

    // record vertex
    private Vertex[] crimeRecords;

    // record distance
    private double[][] pairDistance;

    // record size
    private int size;

    // construct graph
    public Graph(int size){
        crimeRecords = new Vertex[size];
        pairDistance = new double[size][size];
        this.size = size;
    }

    // return vertex array
    public Vertex[] getCrimeRecords(){
        return crimeRecords;
    }

    // return distance 2-D array
    public double[][] getPairDistance(){
        return pairDistance;
    }

    // insert new String to the graph
    public void insert(int pos, String crimeRecord){
        String[] temp = crimeRecord.split(",");
        double x = feetToMiles(Double.valueOf(temp[0]));
        double y = feetToMiles(Double.valueOf(temp[1]));
        Vertex newV = new Vertex(x,y,crimeRecord);
        crimeRecords[pos] = newV;
        pairDistance[pos][pos] = 0.0;


        // get and save distance
        for(int i = 0;i<crimeRecords.length;i++){
            if(crimeRecords[i]==null || i == pos)
                continue;

            Vertex cur = crimeRecords[i];
            double curX = cur.getX();
            double curY = cur.getY();

            pairDistance[i][pos] = pairDistance[pos][i] = Math.sqrt((y-curY) * (y-curY) + (x-curX) * (x-curX));
        }
    }

    // translate feet to miles
    public double feetToMiles(double feet){
        return feet * 0.00018939;
    }

}
