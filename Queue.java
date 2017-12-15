/**
 * Created by yinningliu on 17/2/12.
 */
public class Queue{
    /**
     *  arrays to save the TwoDTreeNode
     */

    private TwoDTreeNode[] arrays;

    /**
     *  front point for add
     */
    private int front;

    /**
     *  rear point for remove
     */
    private int rear;

    /**
     *  manyItems for check whether arrays are empty();
     */
    private int manyItems;

    /**
     * construct this class with empty parameter
     * @postcondtion
     *     set up manyitems is equal to 0 and initialized the arrays with 10 length;
     */

    public Queue(){
        final int INITIAL_CAPACITY = 20;
        manyItems = 0;
        arrays = new TwoDTreeNode[INITIAL_CAPACITY];
    }

    /**
     * construct this class based on parameter of initialCapacity.
     * @param initialCapacity
     * @precondition
     *    initial Capacity must be greater than 0
     * @postcondition
     *    set up manyitems is equal to 0 and initialized the arrays with input value;
     */

    public Queue(int initialCapacity){
        if(initialCapacity<= 0)
            throw new IllegalArgumentException("Cannot be negative or zero");
        manyItems = 0;
        arrays = new TwoDTreeNode[initialCapacity];
    }

    /**
     * add node in the end of the array;
     * @param item
     * @postcondition
     *    manyitems +1; put newNode into arrays' rear; rear value will be changed by method of nextIndex.
     */

    public void add(TwoDTreeNode item){
        if(manyItems == arrays.length){
            ensureCapacity(manyItems*2+1);
        }
        // if list is empty, set up front and rear index
        if(manyItems == 0){
            front = 0;
            rear = 0;
        }
        else{
            rear = nextIndex(rear);
        }
        // put item into end of arrays
        arrays[rear] = item;
        // manyitems plus 1
        manyItems++;
    }

    /**
     * check current array's size;
     * @return
     *    return arrays' length
     */
    public int getCapacity(){
        return arrays.length;
    }

    /**
     * check whether arrays is empty;
     * @return
     *   check if arrays is empty
     */

    public boolean isEmpty(){
        return manyItems==0;
    }

    /**
     * calculate nextIndex for rear index and front index
     * @param i
     * @return
     *    if next index reach to the end of arrays. Return 0. Other wise, return next index;
     */

    private int nextIndex(int i){
        // if next index is more than length, i become 0. Other wise, return i.
        if(++i == arrays.length){
            return 0;
        }
        else{
            return i;
        }
    }

    /**
     * ensure the arrays have enough space to save the newNode
     * @param minimumCapacity
     * @precondition
     *      minimumCapacity must be greater than the current array's length
     * @postcondition
     *      arrays will be double size from original arrays. The data will be copied to the new Array from old Array
     *      front and rear could be re-setup
     */

    public void ensureCapacity(int minimumCapacity){
        // define a new array;
        TwoDTreeNode[] biggerArray;
        int n1, n2;
        //current array's size is bigger than minimum capacity. Nothing to do, just return.
        if(arrays.length>= minimumCapacity)
            return;
        // current array is empty
        else if (manyItems ==0 )
            arrays = new TwoDTreeNode[minimumCapacity];
        // rear index is behind front index
        else if(front<= rear){
            // initialized a new array
            biggerArray = new TwoDTreeNode[minimumCapacity];
            // copy data from current array to new array
            System.arraycopy(arrays,front,biggerArray,front,manyItems);
            // link bigger array to current array
            arrays = biggerArray;
        }
        // front index is behind rear index
        else{
            // initialized a new array
            biggerArray = new TwoDTreeNode[minimumCapacity];
            // calculate length from front index to the end
            n1 = arrays.length -front;
            // calculate length from begin to the rear index
            n2 = rear +1;
            // copy front to end firstly
            System.arraycopy(arrays,front,biggerArray,0,n1);
            // copy begin to rear secondly
            System.arraycopy(arrays,0,biggerArray,n1,n2);
            // re-set up front index
            front = 0;
            // re-set up rear index
            rear = manyItems -1;
            // link bigger array to current array
            arrays = biggerArray;
        }
    }

    /**
     * find and return first node entered from this array
     * @return
     *    return node based on the front index. Re-value front index based on the method of nextIndex
     * @precondition
     *    array is not empty
     * @postcondition
     *    re-set up front index and manyitems minus 1.
     */

    public TwoDTreeNode remove(){
        // define a return node
        TwoDTreeNode res;
        // if tree is empty, return null
        if(manyItems ==0 ) {
            return null;
        }
        // return first entered node
        res = arrays[front];
        // set up next node's index
        front = nextIndex(front);
        // decrease number of nodes by 1
        manyItems--;
        // return node
        return res;
    }

    /**
     * check saved node's size
     * @return
     *    return saved node's size based on the manyItems.
     */

    public int size(){
        return manyItems;
    }


}
