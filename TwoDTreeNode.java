/**
 * Created by yinningliu on 17/2/12.
 */
public class TwoDTreeNode {
    /**
     * private data to store corresponding data from X,Y
     */
    private String data;

    /**
     * save X and Y
     */
    private Points points;

    /**
     *  save left Node
     */
    private TwoDTreeNode leftNode;

    /**
     *  save right Node
     */
    private TwoDTreeNode rightNode;

    /**
     *  check horizontal or vertical comparasion for the current node.
     *  0 is horizontal and 1 is vertical
     */
    private int isVertical;

    /**
     * contruct a empty TreeNode
     */

    public TwoDTreeNode(){
        this.points = null;
        this.data = null;
        this.leftNode = null;
        this.rightNode = null;
    };

    /**
     *
     * @param x
     *    point's x
     * @param y
     *    point's y
     * @param data
     *    corresponding data
     * @param isVertical
     *    0 or 1 (horizontal or vertical)
     * @postcondition
     *    initialize the X, Y, data and axis orientation
     */
    public TwoDTreeNode(double x, double y, String data, int isVertical){
        this.points = new Points(x,y);
        this.data = data;
        this.isVertical = isVertical;
    }

    /**
     * @return
     *      axis orientation
     */

    public int isVertical(){
        return isVertical;
    }

    /**
     * @return
     *     get left Node
     */

    public TwoDTreeNode getLeftNode(){
        return leftNode;
    }

    /**
     *
     * @return
     *    get right Node
     */

    public TwoDTreeNode getRightNode(){
        return rightNode;
    }

    /**
     *
     * @return
     *    get point which contain X and Y
     */

    public Points getPoints(){
        return points;
    }

    /**
     *
     * @return
     *    get corresponding data
     */

    public String getData(){
        return data;
    }

    /**
     *
     * @param leftNode
     * @postcondition
     *      set up left node
     */

    public void setLeftNode(TwoDTreeNode leftNode){
        this.leftNode = leftNode;
    }

    /**
     *
     * @param rightNode
     * @postcondition
     *      set up right node
     */

    public void setRightNode(TwoDTreeNode rightNode){
        this.rightNode = rightNode;
    }

    /**
     *
     * @param isVertical
     * @postcondition
     *      set up axis (horizontal or vertical)
     */

    public void setVertical(int isVertical){
        this.isVertical = isVertical;
    }

    /**
     *
     * @param data
     * @postcondition
     *     set up data
     */
    public void setData(String data){
        this.data = data;
    }

    /**
     *
     * @param points
     * @postcondition
     *     set up points
     */
    public void setPoints(Points points){
        this.points = points;
    }
}
