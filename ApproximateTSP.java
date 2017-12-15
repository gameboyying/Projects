import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by yinningliu on 3/14/17.
 */
public class ApproximateTSP {

    // set up graph
    private Graph graph;

    private int size;

    // save TSP
    private SinglyLinkedList list;

    public ApproximateTSP(){
    }

    // get TSP tour
    public SinglyLinkedList getTSPTour(){
        return list;
    }

    public Graph getGraph(){
        return graph;
    }

    // build our TSP
    public void buildMST(){
        MST mst = new MST(size);

        // get parent array
        int[] p = mst.primMST2(graph.getPairDistance());

        // get TSP
        list = mst.preOrderTreeWalk(p);

   }

    // get Miles we make a tour
    public double getMiles(SinglyLinkedList list, double[][] distance){
        double sum = 0;
        int pre = list.removeCharAtBegin();
        while(!list.isEmpty()){
            int cur = list.removeCharAtBegin();
            sum = sum + distance[pre][cur];
            pre= cur;
        }
        return sum;
    }


    // load data into graph
    public void loadData(int start, int end) throws java.io.FileNotFoundException{
        File f = new File("CrimeLatLonXY1990.csv");

        // read file
        Scanner scanner = new Scanner(f);

        // skip the header
        scanner.nextLine();

        int i = 0;

        // find first index
        while(scanner.hasNext() && i<start){
            scanner.nextLine();
            i++;
        }

        // get size of graph
        this.size = end- start+1;
        graph = new Graph(size);
        int j = 0;
        System.out.println("Crime Records Processed:");
        System.out.println();

        // create our graph
        while(scanner.hasNext() && i<end+1){
            String str = scanner.nextLine();
            graph.insert(j++, str);
            System.out.println(str);
            i++;
        }

        System.out.println();
    }

    public static void main(String[] args) throws java.io.FileNotFoundException{

        System.out.println("Enter Start Index");

        //input start index
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println("Enter End Index");

        //input end index
        scanner = new Scanner(System.in);
        String e = scanner.nextLine();

        // you must input start and end index
        if(s.equals("")||e.equals("")){
            System.out.println("please enter both start index and end index");
            System.out.println("please restart this program");
            return;
        }

        int start = Integer.valueOf(s);
        int end = Integer.valueOf(e);

        if(start>end){
            System.out.println("You have input a wrong start index or end index");
            return;
        }

        ApproximateTSP tsp = new ApproximateTSP();

        // load data into graph
        tsp.loadData(start, end);

        // create our MST and calculate TSP and getMiles
        tsp.buildMST();

        // output TSP tour
        System.out.println("Hamiltonan Cycle (not necessarily optimum): " + tsp.getTSPTour());

        // output miles
        System.out.println("Length Of Cycle : " + String.format("%.2f", tsp.getMiles(tsp.getTSPTour(),tsp.getGraph().getPairDistance())).toString());


    }


}
