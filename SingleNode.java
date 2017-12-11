/**
 * The class DoubleNode holds one pointers and a character. It is used to represent a single node on a
 * single linked list.
 * Created by yinningliu on 17/1/23.
 */
public class SingleNode extends java.lang.Object{

    /**
     * private variable of SingleNode's value
     */
    private String c;

    /**
     *  private variable of next SingleNode
     */
    private SingleNode next;

    /**
     * Constructor with no arguments. Assign null values to next and the null character to c.
     * @postcondition
     *     This new SingleNode has next node which contains null.
     */
    public SingleNode(){
        this.next = null;
        this.c = null;
    }

    /**
     * Construct a SingleNode
     * @param
     *      c - is a character for this node.
     * @param
     *      next - is a pointer to a next node.
     * @postcondition
     *      This new node contains the specified data and 1 links to the next node
     */

    public SingleNode(String c, SingleNode next){
        this.c = c;
        this.next = next;
    }

    /**
     * return SingleNode's value
     * @return
     *      return character
     */

    public String getC(){
        return c;
    }

    /**
     * return next SingleNode
     * @return
     *      return SingleNode
     */

    public SingleNode getNext(){
        return next;
    }

    /**
     * set up SingleNode's value
     * @param
     *      c - is assigned to this node
     * @postcondition
     *      The data of this node has been set to new Data
     */

    public void setC(String c){
        this.c = c;
    }

    /**
     * set up next link
     * @param
     *      next input SingleNode
     * @postcondition
     *      The link to the node after this node has been set to the newLink. Any other node(that used to be in
     * this link) is no longer connected to this node
     */

    public void setNext(SingleNode next){
        this.next = next;
    }

    /**
     * return SingleNode's String value
     * @return
     *      return String
     * @overrides toString in class java.lang.Object
     */
    @Override

    public java.lang.String toString(){
        return String.valueOf(c);
    }


}
