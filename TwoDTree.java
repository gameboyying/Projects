/**
 * Created by yinningliu on 17/2/12.
 */
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class TwoDTree {

    /**
     * define root node
     */
    private TwoDTreeNode root;
    /**
     * count how many nodes
     */
    private int countNode;
    /**
     * count how many looked nodes
     */
    private int countExaminedNode;

    /**
     * construct method
     * @param filepath
     * @throws FileNotFoundException
     * @precondition
     *     The String filepath contains the path to a file formatted in the exact same way as CrimeLatLonXY.csv
     * @postcondition
     *     The 2-d tree is constructed and may be printed or queried.
     */
    public TwoDTree(String filepath) throws FileNotFoundException {

      if(filepath.indexOf("CrimeLatLonXY.csv")<0)
            throw new FileNotFoundException("The String filepath contains the path to a file formatted in the exact same way as CrimeLatLonXY.csv");
        File f = new File(filepath);
        Scanner scanner = new Scanner(f);
        scanner.nextLine();
        while(scanner.hasNext()){
            String str = scanner.nextLine();
            String[] input = str.split(",");
            double x = Double.valueOf(input[0]);
            double y = Double.valueOf(input[1]);
            TwoDTreeNode newNode = new TwoDTreeNode(x,y,str,1);
            countNode++;
            insertNode(newNode);
        }
    }

    /**
     *
     * @return
     *    count node
     */

    public int getCountNode(){
        return countNode;
    }

    /**
     * check whether TwoDTree is empty
     * @return
     *    if empty, return true, otherwise return false;
     */


    public boolean isEmpty(){
        if(root==null)
            return true;
        return false;
    }

    /**
     *
     * @param newNode
     * @postcondition
     *    insert node into correct position of the tree.
     */

    public void insertNode(TwoDTreeNode newNode){

        // if tree is empty, just set up newNode to root.
        if(isEmpty()){
            root = newNode;
            root.setVertical(1);
            return;
        }

        TwoDTreeNode parent = root;

        //loop to look for next inserted place
        while(true)
        {
            //get current parent axis. 0 is horizontal and 1 is vertical
            int axis = parent.isVertical();

            // compare current node with newNode
            double compare = TwoDTree.compareNodes(parent,newNode,axis);

            // newNode is either above or right of the current node
            if(compare <= 0) {
                // if right node is not empty, do next loop
                if (parent.getRightNode() != null) {
                    parent = parent.getRightNode();
                    continue;
                }
                // find inserted place
                else{
                    // set up node
                    parent.setRightNode(newNode);
                    // move to next node
                    parent = parent.getRightNode();
                    // set up axis
                    parent.setVertical(axis==0?1:0);
                    // stop loop
                    break;
                }
            }
            // newNode is either below or left of the current node
            else {
                // if left node is not empty, do next loop
                if(parent.getLeftNode()!=null){
                    parent = parent.getLeftNode();
                    continue;
                }
                // find inserted place
                else{
                    // set up node
                    parent.setLeftNode(newNode);
                    // move to next node
                    parent = parent.getLeftNode();
                    // set up axis
                    parent.setVertical(axis==0?1:0);
                    // stop loop
                    break;
                }
            }
        }
    }

    /**
     * compare 2 nodes based on the axis. If axis is 0, compare Ys. Otherwise, compare Xs.
     * @param n1
     * @param n2
     * @param axis
     * @return
     *    double value;
     */

    public static double compareNodes(TwoDTreeNode n1, TwoDTreeNode n2, int axis){
        //compare X;
        if(axis == 1){
            return n1.getPoints().getX() - n2.getPoints().getX();
        }
        //compare Y;
        else{
            return n1.getPoints().getY() - n2.getPoints().getY();
        }
    }

    /**
     * @precondition
     *      The 2-d tree has been constructed.
     * @postcondition
     *      The 2-d tree is displayed with a pre-order traversal.
     */

    public void preOrderPrint(){
        if(isEmpty())
            return;

        preOrderPrint(root);
    }

    /**
     *
     * @param root
     * @postcondition
     *     print out all the nodes in the tree by using pre-order traversal
     */

    public void preOrderPrint(TwoDTreeNode root){
        if(root==null)
            return;
        System.out.println(root.getData());
        preOrderPrint(root.getLeftNode());
        preOrderPrint(root.getRightNode());
    }

    /**
     * @precondition
     *      The 2-d tree has been constructed.
     * @postcondition
     *      The 2-d tree is displayed with a in-order traversal.
     */

    public void inOrderPrint(){
        if(isEmpty())
            return;
        inOrderPrint(root);
    }

    /**
     * @param root
     * @postcondition
     *     print out all the nodes in the tree by using in-order traversal
     */
    private void inOrderPrint(TwoDTreeNode root){
        if(root==null)
            return;
        inOrderPrint(root.getLeftNode());
        System.out.println(root.getData());
        inOrderPrint(root.getRightNode());
    }


    /**
     * @precondition
     *      The 2-d tree has been constructed.
     * @postcondition
     *      The 2-d tree is displayed with a post-order traversal.
     */

    public void postOrderPrint(){
        if(isEmpty())
            return;
        postOrderPrint(root);
    }

    /**
     * @param root
     * @postcondition
     *     print out all the nodes in the tree by using post-order traversal
     */
    private void postOrderPrint(TwoDTreeNode root){
        if(root==null)
            return;
        postOrderPrint(root.getLeftNode());
        postOrderPrint(root.getRightNode());
        System.out.println(root.getData());
    }

    /**
     * @precondition
     *      The 2-d tree has been constructed.
      *@postcondition
     *       The 2-d tree is displayed with a level-order traversal.
     */
    public void levelOrderPrint(){
        if(isEmpty())
            return;
        Queue print = new Queue();
        print.add(root);
        while(!print.isEmpty()){
            TwoDTreeNode cur = print.remove();;
            System.out.println(cur.getData());
            if(cur.getLeftNode()!=null){
                print.add(cur.getLeftNode());
            }
            if(cur.getRightNode()!=null){
                print.add(cur.getRightNode());
            }
        }
    }

    /**
     * @param x1  left bottom x of the rectangle.
     * @param y1  left bottom y of the rectangle.
     * @param x2  top right x of the rectangle.
     * @param y2  top right y of the rectangle.
     * @return
     *      variable of ListOfCrimes
     * @precondition
     *      The 2-d tree has been constructed. Input shall be a rectangular.
     * @postcondition
     *      A list of 0 or more crimes is returned.
     *      These crimes occurred within the rectangular range specified by the four parameters.
     */
    public ListOfCrimes findPointsInRange(double x1, double y1, double x2, double y2){

        // define a return
        ListOfCrimes lof = new ListOfCrimes();

        // check if empty
        if(isEmpty())
            return lof;
        // Input shall be a rectangular.
        if(x1==x2||y1==y2)
            return lof;

        //initialize countExaminedNode to 0
        countExaminedNode = 0;

        //if first point is right, just switch
        if(x1>x2 && y1>y2){
            findPointsInRange(lof, root, x2, y2, x1, y1);
        }
        else {
            findPointsInRange(lof, root, x1, y1, x2, y2);
        }

        // return ListOfCrimes
        return lof;
    }

    /**
     *
     * @return
     *   return countExaminedNode
     */

    public int getCountExaminedNode(){
        return countExaminedNode;
    }

    /**
     *
     * @param lof
     * @param root
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @precondition
     *     current node is not null
     * @postcondition
     *     if current node is in the range, add it into listOfCrimes
     */

    private void findPointsInRange(ListOfCrimes lof, TwoDTreeNode root, double x1, double y1, double x2, double y2){
        //when current root is null, terminate recursion.
        if(root ==null){
            return;
        }
        // call once, visited node plus 1
        countExaminedNode++;

        // get x and y
        double x = root.getPoints().getX();
        double y = root.getPoints().getY();

        // get axis, 0 is for horizontal and 1 is for vertical
        int axis = root.isVertical();

        // if current node is the range, add into list of crimes.
        if(x>=x1 && y>=y1 && x<=x2 && y<=y2){
            lof.addCrimes(root);
        }

        // if horizontal
        if(axis==0){

            // current node is in the middle of rectangle, check left and right node
            if(y1<= y && y<=y2){
                findPointsInRange(lof,root.getLeftNode(),x1,y1,x2,y2);
                findPointsInRange(lof,root.getRightNode(),x1,y1,x2,y2);
            }
            // current node is above the rectangle, check left node
            else if (y1< y && y2< y){
                findPointsInRange(lof,root.getLeftNode(),x1,y1,x2,y2);
            }
            // current node is below the rectangle, check right node
            else{
                findPointsInRange(lof,root.getRightNode(),x1,y1,x2,y2);
            }
        }

        // if vertical
        else{
            // current node is in the middle of rectangle, check left and right node
            if(x1<= x && x<=x2){
                findPointsInRange(lof,root.getLeftNode(),x1,y1,x2,y2);
                findPointsInRange(lof,root.getRightNode(),x1,y1,x2,y2);
            }
            // current node is right the rectangle, check left node
            else if (x1< x && x2<x){
                findPointsInRange(lof,root.getLeftNode(),x1,y1,x2,y2);
            }
            // current node is left the rectangle, check right node
            else{
                findPointsInRange(lof,root.getRightNode(),x1,y1,x2,y2);
            }
        }
    }

    /**
     *
     * @param x1
     *    The (x1,y1) pair represents a point in space near Pittsburgh and in the state plane coordinate system.
     * @param y1
     *    The (x1,y1) pair represents a point in space near Pittsburgh and in the state plane coordinate system.
     * @param nearest
     *    The parameter “nearest” holds a reference to the result node (with no data)
     * @return
     *    the distance in feet to the nearest node is returned.
     * @precondition
     *    the 2-d tree has been constructed.
     * @postcondition
     *     The reference parameter now has the nearest neighbor's data
     */
    public double nearestNeighbor(double x1, double y1, TwoDTreeNode nearest){
        //check if tree is empty.
        if(isEmpty())
            return 0.0d;

        //initialize the countExaminedNode to 0
        countExaminedNode = 0;

        //look for nearest node
        nearestNeighbor(x1,y1,nearest,root);

        //get x, y of nearest node
        double x = nearest.getPoints().getX();
        double y = nearest.getPoints().getY();

        //the distance in feet to the nearest node
        return Math.sqrt((y-y1)*(y-y1)+(x-x1)*(x-x1));
    }

    private void nearestNeighbor(double x1, double y1, TwoDTreeNode nearest, TwoDTreeNode root){
        // when current node is null, stop recursion.
        if(root==null){
            return;
        }

        // call once, visited times plus 1
        countExaminedNode++;

        // update neareset parameter
        if(nearest.getPoints() == null){
            nearest.setVertical(root.isVertical());
            nearest.setLeftNode(root.getLeftNode());
            nearest.setRightNode(root.getRightNode());
            nearest.setData(root.getData());
            nearest.setPoints(root.getPoints());
        }


        // get current node x, y and axis
        double rX = root.getPoints().getX();
        double rY = root.getPoints().getY();
        double axis = root.isVertical();


        // get nearest node x, y
        double nX = nearest.getPoints().getX();
        double nY = nearest.getPoints().getY();

        //calculate distance
        double distanceNearest = Math.sqrt((nY-y1) * (nY-y1) +(nX-x1) * (nX-x1));
        double distanceRoot = Math.sqrt((rY-y1) * (rY-y1) + (rX-x1) * (rX-x1));

        //if found new nearest node, update nearest parameter
        if(distanceRoot<distanceNearest){
            nearest.setVertical(root.isVertical());
            nearest.setLeftNode(root.getLeftNode());
            nearest.setRightNode(root.getRightNode());
            nearest.setData(root.getData());
            nearest.setPoints(root.getPoints());
        }

        // if horizontal
        if(axis==0){

            // if query point is above the node
            if(rY<=y1) {
                //check right first
                nearestNeighbor(x1, y1, nearest, root.getRightNode());
                // if current node is nearest node, then check left node. Otherwise, prune
                if (nearest.getPoints().getX() == root.getPoints().getX() && nearest.getPoints().getY() == root.getPoints().getY()) {
                    nearestNeighbor(x1, y1,nearest,root.getLeftNode());
                }
            }
            // if query point is below the node
            else{
                //check left first
                nearestNeighbor(x1,y1,nearest,root.getLeftNode());
                // if current node is nearest node, then check right node. Otherwise, prune
                if(nearest.getPoints().getX() == root.getPoints().getX() && nearest.getPoints().getY() == root.getPoints().getY()){
                    nearestNeighbor(x1,y1,nearest,root.getRightNode());
                }
            }
        }
        // if vertical
        else{
            // if query point is right of current node
            if(rX<=x1){
                // check right first.
                nearestNeighbor(x1,y1,nearest,root.getRightNode());
                // if current node is nearest node, then check left node. Otherwise, prune
                if(nearest.getPoints().getX() == root.getPoints().getX() && nearest.getPoints().getY() == root.getPoints().getY()){
                    nearestNeighbor(x1,y1,nearest,root.getLeftNode());
                }
            }
            else{
                // check left first.
                nearestNeighbor(x1,y1,nearest,root.getLeftNode());
                // if current node is nearest node, then check right node. Otherwise, prune
                if(nearest.getPoints().getX() == root.getPoints().getX() && nearest.getPoints().getY() == root.getPoints().getY()){
                    nearestNeighbor(x1,y1,nearest,root.getRightNode());
                }
            }
        }
    }
}
