/**
 * Created by yinningliu on 17/2/12.
 */
public class ListOfCrimes {

    /**
     * define list with head node only without tail node
     */
    private SinglyLinkedList list;

    /**
     * initalize the SinglyLinkedlist
     * @postcondition
     *    the list has been initalized.
     */
    public ListOfCrimes(){
        list = new SinglyLinkedList();
    }

    /**
     * create a KML representation of the list.
     * @precondition
     *    list must have at least one node (list is not null)
     * @return
     *    return a KML representation of the list.
     */
    public String toKML(){

        if(list.getHead()==null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.append("<kml xmlns=\"http://earth.google.com/kml/2.2\">\n");
        sb.append("<Document>\n");
        sb.append("<Style id=\"style1\">\n");
        sb.append("<IconStyle>\n");
        sb.append("<Icon>\n");
        sb.append("<href>http://maps.gstatic.com/intl/en_ALL/mapfiles/ms/micons/blue-dot.png</href>\n");
        sb.append("</Icon>\n");
        sb.append("</IconStyle>\n");
        sb.append("</Style>\n");

        SingleNode dummyHead = list.getHead();
        while(dummyHead!=null){
            String temp = dummyHead.getC();
            String[] tempData = temp.split(",");
            sb.append("<Placemark>\n");
            sb.append("<name>" + tempData[4] + "</name>\n");
            sb.append("<description>" + tempData[3] + "</description>\n");
            sb.append("<styleUrl>#style1</styleUrl>\n");
            sb.append("<Point>\n");
            sb.append("<coordinates>"+ tempData[8] + "," + tempData[7] + ",0.000000</coordinates>\n");
            sb.append("</Point>\n");
            sb.append("</Placemark>\n");
            dummyHead  = dummyHead.getNext();
        }
        sb.append("</Document>");
        sb.append("</kml>");

        return sb.toString();
    }

    /**
     * saved the nodes into linkedlist
     * @param node
     * @postcondition
     *    node will be inserted into the linkedlist
     */
    public void addCrimes(TwoDTreeNode node){
        list.addCharAtEnd(node.getData());
    }

    /**
     * create the list as a String
     * @return
     *    return the list as a String
     */
    public String toString(){
        return list.toString();
    }

    /**
     * count how many nodes in the linkedlist.
     * @return
     *     node's size
     */
    public int countNode(){
        return list.countNode();
    }
}
