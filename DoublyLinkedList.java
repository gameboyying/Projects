import java.math.BigInteger;

/**
 * This class implements a doubly linked list of characters in Java. The instance variables head and tail are initially
 * null. As elements are added head points to the first element on the list and tail points to the last element.
 * Each node on the list is of type DoubleNode. Each DoubleNode holds a pointer to the previous node and a pointer
 * to the next node in the list.
 * Created by yinningliu on 17/1/22.
 */


public class DoublyLinkedList extends java.lang.Object{

    /**
     * private DoubleNode for head;
     */
    private DoubleNode head;
    /**
     * private DoubleNode for tail;
     */
    private DoubleNode tail;


    /**
     *   Constructs a new DoublyLinkedList object with head and tail as null.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     *   @postcondition
     *          This new doublyLinked list has head node and tail node which contains null value.
     */
    public DoublyLinkedList(){
        this.head = null;
        this.tail = null;
    }

    /**
     * Add a character node containing the character c to the end of the linked list. This routine does not require a search.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param
     *      data- -- a single character
     * @postcondition
     *      A new node has been created and placed in the end of the doublylinkedList. The data for the new node is element
     * @throws
     *     OutOfMemoryError
     *     Indicates that there is insufficient memory for a new Node
     */

    public void addNodeAtEnd(BigInteger data){

        //create a new Node with new Value of c
        DoubleNode newNode = new DoubleNode(tail,data,null);

        //check whether linkedlist is empty,
        if(isEmpty()){
            head = newNode;
            tail = newNode;
            return;
        }


        tail.setNext(newNode);

        // re-setup tail node
        tail = newNode;

    }

    /**
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      head node
     */

    public DoubleNode getHead(){
        return head;
    }

    /**
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      tail node
     */

    public DoubleNode getTail(){
        return tail;
    }

    /**
     * Add a character node containing the character c to the front of the linked list. No search is required.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @param
     *      data- -- a single character
     * @postcondition
     *      A new node has been created and places in the begin of the doublylinkedList. The data for the new node is element.
     * @throws
     *         OutOfMemoryError
     *         Indicates that there is insufficient memory for a new Node.
     */

    public void addNodeAtFront(BigInteger data){

        //create a new Node with new Value of c
        DoubleNode newNode = new DoubleNode(null,data,head);


        //check whether linkedlist is empty,
        if(isEmpty()){
            head = newNode;
            tail = newNode;
            return;
        }

        head.setPrev(newNode);

        //re-set up head node
        head = newNode;
    }

    /**
     * Counts the number of nodes in the list. We are not maintaining a counter so a search is required.
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     * @return
     *      the number of nodes in the doubly linked list between head and tail inclusive
     * @note
     *      A wrong answer occurs for lists longer than Int.MAX_VALUE because of arithmetic overflow
     */

    public int countNodes(){
        int count = 0;
        DoubleNode dummyHead = head;

        //count the node from head to tail(tail exclusive)
        while(dummyHead!=null && dummyHead!= tail){
            dummyHead = dummyHead.getNext();
            count++;
        }

        //count the last node( tail node)
        if(dummyHead!=null && dummyHead==tail)
            count++;
        return count;
    }

    /**
     * Deletes the first occurrence of the character c from the list. If the character c is not in the list then no
     * modifications are made. This method needs to search the list.
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(1)
     * @param
     *      data - -- the character to be searched for.
     * @return
     *      true if a deletion occurred and false otherwise
     * @precondition
     *      linked list is not empty.
     * @postcondition
     *      remove fist node which value is equal to the parameter. If head need to be deleted, re-setup
     * head to the next node. If tail need to be deleted, re-set tail to the previous node.
     */

    public boolean deleteNode(BigInteger data){

        if(isEmpty()==true){
            throw new IllegalArgumentException("The list is empty");
        }

        boolean res = false;
        DoubleNode dummyHead = head;
        while(dummyHead!=null) {

            if (dummyHead.getData().compareTo(data)==0) {
                DoubleNode prev = dummyHead.getPrev();
                DoubleNode next = dummyHead.getNext();
                if (prev != null)
                    prev.setNext(next);
                else
                    head = head.getNext();
                if (next != null)
                    next.setPrev(prev);
                else
                    tail = tail.getPrev();
                res = true;
                break;
            }

            dummyHead = dummyHead.getNext();
        }
        return res;
    }

    /**
     * Returns true if the list is empty false otherwise
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      true if the list empty false otherwise
     */
    public boolean isEmpty(){
        if(head==null && tail==null){
            return true;
        }

        return false;
    }

    /**
     * Remove and return the character at the end of the doubly linked list. No searching is required.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      the character at the end
     * @precondition
     *      the list is not empty
     * @postcondition
     *      The last node of Linkedlist has been removed. The node before this removed node becomes a new tail Node
     * If head is equal to the tail, remove the node and re-set head and tail to nulls.
     */

    public BigInteger removeNodeAtEnd(){

        if(isEmpty()==true){
            throw new IllegalArgumentException("head is null and tail is null");
        }

        //if linkedlist only has one node, remove it and resetup head and tail to nulls
        if(head==tail){
            BigInteger c = head.getData();
            head = null;
            tail = null;
            return c;
        }

        // if linkedlist has many nodes, remove front one and resetup tail node.
        BigInteger c = tail.getData();
        DoubleNode temp = tail.getPrev();
        temp.setNext(tail.getNext());
        tail = temp;

        return c;
    }

    /**
     * Remove and return the character at the front of the doubly linked list.
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @return
     *      the character at the front
     * @precondition
     *      the list is not empty
     * @postcondition
     *      The first node of Linkedlist has been removed. The node after this removed node becomes a new head Node
     * If head is equal to the tail, remove the node and re-set head and tail to nulls.
     */

    public BigInteger removeNodeFromFront(){

        if(isEmpty()){
            throw new IllegalArgumentException("The list is empty");
        }

        //if linkedlist only has one node, remove it and resetup head and tail to nulls
        if(head==tail){
            BigInteger c = head.getData();
            head = null;
            tail = null;
            return c;
        }

        // if linkedlist has many nodes, remove front one and resetup head node.
        BigInteger c = head.getData();
        DoubleNode temp = head.getNext();
        temp.setPrev(head.getPrev());
        head = temp;
        return c;
    }

    /**
     * reverse the list. a to b to c becomes c to b to a
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     * @precondition
     *      the list is not empty
     * @postcondition
     *      the original list has been reversed. Also, the head and tail are swapped
     */

    public void reverse(){
        if(isEmpty()){
            throw new IllegalArgumentException("The list is empty");
        }
        DoubleNode dummyHead = head;

        //reverse the node from head to tail
        while(dummyHead!=null && head != tail){
            DoubleNode temp = dummyHead.getNext();
            dummyHead.setNext(dummyHead.getPrev());
            dummyHead.setPrev(temp);
            dummyHead = temp;
        }

        //swap between head and tail
        DoubleNode temp = head;
        head = tail;
        tail = temp;

    }

    /**
     * Returns the node that is present at the index location<br><br> Best Case
     * Running Time: Big Theta(1)<br> Worst Case Running Time: Big Theta(n)<br>
     * Pre-Condition: The linked list should not be empty <br> Post-Condition:
     * none <br>
     *
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     * @precondition
     *      head is not null
     * @param index
     *      Index location whose node has to be returned
     * @return
     *      The node that is present in index location
     */
    public DoubleNode get(int index) {
        if (head == null) {
            return null;
        }

        DoubleNode iterator = head;
        for (int i = 0; i < index; i++) {
            iterator = iterator.getNext();
            if (iterator == null) {
                return null;
            }
        }
        return iterator;
    }

    /**
     * Returns the list as a String. The class DoubleNode has a toString that will be called from this toString.
     * The String returned must be presented clearly. Null pointers must be represented differently than non-null pointers.
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     * @return
     *      a String containing the characters in the list
     * @overrides toString in class java.lang.Object
     */

    @Override
    public java.lang.String toString(){

        if(isEmpty()){
            return "The list is Empty";
        }

        StringBuilder sb = new StringBuilder();
        DoubleNode dummyNode = head;
        while(dummyNode!=null) {
            sb.append(dummyNode.getData().toString());
            dummyNode = dummyNode.getNext();
        }
        return sb.toString();
    }

}
