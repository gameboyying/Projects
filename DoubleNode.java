/**
 * The class DoubleNode holds two pointers and a character. It is used to represent a single node on a
 * double linked list.
 * Created by yinningliu on 17/1/22.
 */

import java.lang.String;
import java.math.BigInteger;

public class DoubleNode extends java.lang.Object {

    /**
     * private variable of previous DoubleNode
     */
    private DoubleNode p;

    /**
     * private variable of next DoubleNode
     */
    private DoubleNode n;

    /**
     *  private variable of DoubleNode's value
     */
    private BigInteger data;

    /**
     * Constructor with no arguments. Assign null values to previous, next and the null character to c.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @postcondition
     *      This new DoubleNode has previous node and next node which contains nulls
     */
    public DoubleNode(){
        this.p = null;
        this.n = null;
        this.data = null;
    }

    /**
     * Construct a DoubleNode
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param p
     *    - is a pointer to a previous node.
     * @param
     *    data - is a character for this node.
     * @param
     *    n - is a pointer to a next node.
     * @postcondition
     *    This new node contains the specified data and 2 links to the previous node and next node
     */

    public DoubleNode(DoubleNode p, BigInteger data, DoubleNode n){
        this.p = p;
        this.n = n;
        this.data = data;
    }

    /**
     * set up DoubleNode's value
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param
     *      data - is assigned to this node
     * @postcondition
     *      The data of this node has been set to new Data
     */
    public void setData(BigInteger data){
        this.data = data;
    }

    /**
     * set up next link
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param
     *      next input DoubleNode
     * @postcondition
     *      The link to the node after this node has been set to the newLink. Any other node(that used to be in
     * this link) is no longer connected to this node
     */
    public void setNext(DoubleNode next){
        n = next;
    }

    /**
     * set up previous DoubleNode
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param
     *      prev input DoubleNode
     * @postcondition
     *      The link to the node before this node has been set to the newLink. Any other node(that used to be in
     * this link) is no longer connected to this node
     */

    public void setPrev(DoubleNode prev){
        p = prev;
    }

    /**
     * return DoubleNode's value
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      return character
     */

    public BigInteger getData(){
        return data;
    }

    /**
     * return next DoubleNode
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      return DoubleNode
     */

    public DoubleNode getNext(){
        return n;
    }

    /**
     * a pointer to the previous node or null if none exists
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      a pointer to the previous node or null if none exists
     */

    public DoubleNode getPrev(){
        return p;
    }

    /**
     * return DoubleNode's String value
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      return String from character
     * @overrides toString in class java.lang.Object
     */
    @Override
    public java.lang.String toString(){
        return data.toString();
    }

}
