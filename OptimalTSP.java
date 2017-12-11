import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by yinningliu on 3/14/17.
 */
public class OptimalTSP {

    private Graph graph;
    private int size;

    private double minSum = Integer.MAX_VALUE;
    private SinglyLinkedList bestList = null;

    //construct out Optimal TSP
    public OptimalTSP(){}

    // find Minimal Path
    public void findMinimalPath(){
        getMinimalMiles(graph.getPairDistance(),size);
    }


    public SinglyLinkedList getBestList(){
        return bestList;
    }

    public double getMinSum(){
        return minSum;
    }

    public Graph getGraph(){
        return graph;
    }


    // get minimal Miles
    private void getMinimalMiles( double[][] distance, int maxIndex){
        // Define a linkedlist
        SinglyLinkedList list = new SinglyLinkedList();
        // Define a array to check whether we have visited
        boolean[] marked = new boolean[size];
        permutations(list,graph.getPairDistance(),0,maxIndex,marked);
    }

    // find permutations, use backtracking method
    private void permutations(SinglyLinkedList list, double[][] distance, int start, int end, boolean[] marked){
        // if current list size is equal to end, we have to calculate minimal miles
        if(list.getSize()==end){
            // add first node again, because we have to go back to the head
            list.addCharAtEnd(list.getHead().getC());
            // get miles
            double sum = getMiles(list,distance);
            // if current smallest miles is greater than new smallest mile, re setup
            if(minSum > sum){
                // set up a new Min Sum
                minSum =  sum;

                // save a new List
                bestList = SinglyLinkedList.copyLinkedList(list);
            }

            // remove last node(dummy head)
            list.removeCharAtEnd();
            return;
        }

        // calculate permutations
        for(int i = 0;i<end;i++){
            // if it is not visited
            if(marked[i]==false) {
                // add into the list
                list.addCharAtEnd(i);
                // set up its visited
                marked[i] = true;
                // do next permutations
                permutations(list, distance, start + 1, end,marked);
                // remove last node
                list.removeCharAtEnd();
                // re-setup this node to unvisited
                marked[i] = false;
            }
        }

    }

    // get current tour's miles
    public double getMiles(SinglyLinkedList list, double[][] distance){
        double sum = 0;
        // Used Copy linkedlist, because if I do not used copy. Then remove any node, the parameter's will be changed as well.
        // I have to keep original linkedlist to find next permutation.
        SinglyLinkedList newList = SinglyLinkedList.copyLinkedList(list);
        int pre = newList.removeCharAtBegin();
        while(!newList.isEmpty()){
            int cur = newList.removeCharAtBegin();
            sum = sum + distance[pre][cur];
            pre= cur;
        }

        return sum;
    }



    // load data. Same Concept as ApproximateTSP. Please refer to the Approximate TSP
    public void loadData(int start, int end) throws java.io.FileNotFoundException{
        File f = new File("CrimeLatLonXY1990.csv");
        Scanner scanner = new Scanner(f);
        scanner.nextLine();
        int i = 0;

        while(scanner.hasNext() && i<start){
            scanner.nextLine();
            i++;
        }

        this.size = end- start+1;
        graph = new Graph(size);
        int j = 0;
        System.out.println("Crime Records Processed:");
        System.out.println();
        while(scanner.hasNext() && i<end+1){
            String str = scanner.nextLine();
            graph.insert(j++, str);
            System.out.println(str);
            i++;
        }
        System.out.println();
    }


    // Same concept as ApproximateTSP. Please refer to the Approximate TSP
    public static void main(String[] args) throws java.io.FileNotFoundException{

        System.out.println("Enter Start Index");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println("Enter End Index");
        scanner = new Scanner(System.in);
        String e = scanner.nextLine();

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

        OptimalTSP tsp = new OptimalTSP();
        tsp.loadData(start, end);
        tsp.findMinimalPath();

        System.out.println("Hamiltonan Cycle (minmal): " + tsp.getBestList());
        System.out.println("Length Of Cycle : " + tsp.getMinSum());

    }
}
