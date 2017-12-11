/**
 * Created by yinningliu on 17/2/22.
 */
public class Stack <E> {

    /**
     * arrays to save the item
     */
    private Object[] arrays;

    /**
     * minmium Size of the arrays
     */
    private int minmumSize;

    /**
     * always point to the top
     */
    private int topPointerIndex;

    /**
     * inialized the stack
     */
    public Stack(){
        minmumSize = 6;
        arrays = new Object[minmumSize];
        topPointerIndex = -1;
    }

    /**
     * check whether stack is empty
     * @return
     */

    public boolean isEmpty(){

        // check whether stack is empty
        if(topPointerIndex==-1){
            return true;
        }
        return false;
    }

    /**
     * push an item into the stack
     * @param item
     * @bigthetavalues
     *      worst case is Θ(n) n is equal to minmumSize when we need to do double size
     *      best case is Θ(1) when we do not need to do double size.
     */

    public void push(E item){
        //if top Index is reach to the minmumSize, need to double size
        if(++topPointerIndex == minmumSize){
            doubleSize(minmumSize * 2);
        }

        // put item in the top of the arrays
        arrays[topPointerIndex] = item;
    }

    /**
     * pop an item from the stack
     * @return
     * @throws Exception
     */
    public E pop() throws Exception{
        // if it is empty, pop stack underflow exception
       if(isEmpty())
           throw new Exception("error:Stack Underflow Exception");

        // pop the top item
       E top = (E) arrays[topPointerIndex--];

        return top;
    }


    /**
     * increae the size of the array
     * @param newSize
     */
    private void doubleSize(int newSize){
        // create new bigger array
        Object[] newArrays = new Object[newSize];
        // copy original array to this new bigger array
        System.arraycopy(arrays,0,newArrays,0,minmumSize);
        // re set up minmum Size
        minmumSize = newSize;
        // point bigger array to original array
        arrays = newArrays;
    }

    /**
     * main function to test the stack
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception{

        /**
         * create a new stack
         */
        Stack<Integer> stack = new Stack<Integer>();

        /**
         * push integer from 0 to 999 into the stack
         */
        System.out.println("push from 0 to 999");
        for(int i=0;i<1000;i++){
            stack.push(i);
        }

        /**
         * pop integer from 999 to 0
         */
        System.out.println("pop from 999 to 0");
        while(stack.isEmpty()==false){
            System.out.println(stack.pop());
        }
    }
}
