/**
 * This class implements a singly linked list of characters in Java. The instance variables head are initially
 * null. As elements are added head points to the first element on the list.
 * Each node on the list is of type SingleNode. Each SingleNode holds a pointer to the next node in the list.
 * Created by yinningliu on 17/1/23.
 */
public class SinglyLinkedList extends java.lang.Object{

    /**
     * private head node
     */
    private SingleNode head;

    /**
     *   Constructs a new SingleLinkedList object with head as null.
     *   @postcondition
     *      This new singlyLinked list has head node which contains null value.
     */

    public SinglyLinkedList(){
        this.head = null;
    }

    /**
     * Add a character node containing the character c to the end of the linked list. This routine does not require a search.
     * @param
     *      c- -- a single character
     * @postcondition
     *      A new node has been created and placed in the end of the singlylinkedList. The data for the new node is c.
     *     if singlylinkedlist is empty, setup head to the newNode.
     * @throws
     *     OutOfMemoryError
     *     Indicates that there is insufficient memory for a new Node
     */

    public void addCharAtEnd(String c){

        SingleNode newNode = new SingleNode(c,null);

        if(isEmpty()){
            head = newNode;
            return;
        }

        SingleNode dummyHead = head;
        while(dummyHead.getNext()!=null){
            dummyHead = dummyHead.getNext();
        }
        dummyHead.setNext(newNode);
    }


    /**
     * Returns true if the list is empty false otherwise
     * @return
     *      true if the list empty false otherwise
     */

    public boolean isEmpty(){
        if(head==null)
            return true;
        return false;
    }

    /**
     * get head
     * @return
     *      head;
     */

    public SingleNode getHead(){
        return head;
    }

    /**
     * count how many nodes
     * @return
     *     return count of node
     */

    public int countNode(){
        int res =0;
        SingleNode dummyNode = head;
        while(dummyNode!=null){
            dummyNode = dummyNode.getNext();
            res++;
        }
        return  res;
    }


    /**
     * Returns the list as a String. The class SingleNode has a toString that will be called from this toString.
     * @return
     *      a String containing the all strings in the list. And mark \n for line break;
     * @overrides toString in class java.lang.Object
     */

    @Override
    public java.lang.String toString(){

        if(isEmpty()){
            return "The list is Empty";
        }

        StringBuilder sb = new StringBuilder();
        SingleNode dummyHead = head;
        while(dummyHead!=null){
            sb.append(dummyHead.getC());
            sb.append("\n");
            dummyHead = dummyHead.getNext();
        }

        return sb.toString();
    }
}
