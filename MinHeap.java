/**
 * Created by yinningliu on 3/18/17.
 * heap to track what is next of vertex which shall be do next prim();
 */
public class MinHeap {

    // create arrays to save each element
    private HeapElement[] heap;

    // keep track lastIndex
    private int lastIndex;

    // initialized the MinHeap
    public MinHeap(int size){
        heap = new HeapElement[size];
        lastIndex = 0;
    }

    // insert into arrays at the end, used for primMST
    public void add(int key, double distance){

        // if current heap's capacity is not enough, double help size
        if(lastIndex>=heap.length){
            ensureCapacity(heap.length * 2);
        }

        // create a new element which contains (vertex, smallest distance)
        HeapElement heapElement = new HeapElement(key, distance);

        // put it in the place which is last index
        heap[lastIndex] = heapElement;

        // check with its parent
        rotateDownToUp(lastIndex);

        // move lastIndex to next;
        lastIndex++;
    }

    // insert into arrays at the end, used for PrimMST2
    public void add(int key, double distance, int parent){

        // if current heap's capacity is not enough, double help size
        if(lastIndex>=heap.length){
            ensureCapacity(heap.length * 2);
        }

        // create a new element which contains (vertex, smallest distance)
        HeapElement heapElement = new HeapElement(key, distance,parent);

        // put it in the place which is last index
        heap[lastIndex] = heapElement;

        // check with its parent
        rotateDownToUp(lastIndex);

        // move lastIndex to next;
        lastIndex++;
    }

    // move small key from down to up
    private void rotateDownToUp(int index){
        // if index is beyond arrays, stop
        if(index<=0)
            return;

        // get parent index
        int parent = (index-1)/2;

        // when parent distance is bigger than new inserted node distance, swap
        if(heap[parent].getDistance()>heap[index].getDistance()){
            // swap parent and child
            swap(parent,index);
            // do next check
            rotateDownToUp(parent);
        }
        else {
            // stop when parent distance is smaller than new inserted node distance
            return;
        }
    }

    // swap two nodes
    private void swap(int parent, int index){
        HeapElement temp = heap[parent];
        heap[parent] = heap[index];
        heap[index] = temp;
    }

    // double current array
    public void ensureCapacity(int minimumCapacity){
        // define a new array;
        HeapElement[] biggerArray;
        int n1, n2;
        //current array's size is bigger than minimum capacity. Nothing to do, just return.
        if(heap.length>= minimumCapacity)
            return;
        else
        {
            // initialized bigger Array
            biggerArray = new HeapElement[minimumCapacity];

            // copy current array to that bigger array
            System.arraycopy(heap,0,biggerArray,0,heap.length);

            // set biggerArray as current using array
            heap = biggerArray;
        }
    }

    // check if array is empty;
    public boolean isEmpty(){
        if(lastIndex==0)
            return true;
        return false;
    }

    // get heap's root and do rotation
    public HeapElement deleteMin(){
        // if array is empty, return -1
        if(isEmpty()) return null;

        // get key(vertex index) from root
        HeapElement temp = heap[0];

        // copy last node to root
        heap[0] = heap[lastIndex-1];

        // set up last node to null
        heap[lastIndex-1] = null;

        // check with children
        rotateTopToDown(0);

        // move last index to previous
        lastIndex--;

        // return root
        return temp;
    }

    //
    private void rotateTopToDown(int index){

        // if index is larger than last index. That means, it is beyond heap range
        if(index>=lastIndex)
            return;

        // get left child
        HeapElement left = index * 2 + 1<lastIndex? heap[index*2+1]:null;

        // get right child
        HeapElement right = index * 2 + 2<lastIndex? heap[index*2+2]:null;

        // if both children is null, i have reached to the leaf. Just return
        if(left==null && right == null)
            return;

        // if only left child available, compare with left only
        if(left!=null && right == null){
            // if parent is smaller. Just stop
            if(heap[index].getDistance()<left.getDistance()){
                return;
            }
            // otherwise, swap parent with left child
            swap(index, index * 2 + 1);

            // move to left child
            rotateTopToDown(index * 2 + 1);
        }
        // if only right child available, compare with right only
        else if(left==null && right !=null){

            // if parent is smaller, just stop
            if(heap[index].getDistance()<right.getDistance()){
                return;
            }
            // otherwise, swap parent with right child
            swap(index, index * 2 + 2);

            // move to right child
            rotateTopToDown(index * 2 + 2);
        }
        else{

            // if parent is smaller than left and right, stop
            if(heap[index].getDistance()<left.getDistance() && heap[index].getDistance()<right.getDistance()){
                return;
            }

            //if parent is smaller than right but bigger than left, swap with left. Move to left child
            if(heap[index].getDistance()>left.getDistance() && heap[index].getDistance()<right.getDistance()){
                swap(index, index * 2 + 1);
                rotateTopToDown(index * 2 + 1);
            }
            // if parent is smaller than left but bigger than right, swap with right. Move to right child
            else if(heap[index].getDistance()<left.getDistance() && heap[index].getDistance()>right.getDistance()) {
                swap(index, index * 2 + 2);
                rotateTopToDown(index * 2 + 2);
            }
            // find which child is smaller, left or right
            else{
                int temp = left.getDistance()<right.getDistance()?index * 2 + 1:index * 2 + 2;

                // swap with smaller child
                swap(index,temp);

                // move to smaller child
                rotateTopToDown(temp);
            }
        }
    }

}
