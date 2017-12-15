/**
 * Created by yinningliu on 3/19/17.
 */
public class HeapElement {

    // set up key such as: 1,2,3,4,5,6
    private int key;

    // set up distance such as 1->2, 2->3
    private double distance;

    // set up parent
    private int parent;

    // construct HeapElement without parent, used for primMST
    public HeapElement(int key, double distance){
        this.key = key;
        this.distance = distance;
    }

    //construct Heap Element with parent, used for primMST2
    public HeapElement(int key, double distance, int parent){
        this.key = key;
        this.distance = distance;
        this.parent = parent;
    }

    // return key
    public int getKey(){
        return key;
    }

    // return distance
    public double getDistance(){
        return distance;
    }


    // return parent key
    public int getParent(){
        return parent;
    }

}
