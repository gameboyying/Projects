import java.math.BigInteger;

/**
 * Created by yinningliu on 17/2/25.
 */

public class RedBlackNode {

    /**
     * private value to save the key
     */
    private String key;

    /**
     * private value to save the value
     */
    private BigInteger value;
    //black is 0, red is 1
    /**
     * private value to save the color, 0 is black, 1 is red
     */
    private int color;

    /**
     * point to the parent
     */
    private RedBlackNode p;

    /**
     * point to the left child
     */
    private RedBlackNode lc;

    /**
     * point to the right child
     */
    private RedBlackNode rc;

    /**
     * inialized the one Red Black Node
     * @param key
     * @param value
     * @param p
     * @param color
     * @param lc
     * @param rc
     */

    public RedBlackNode(String key, BigInteger value, RedBlackNode p, int color, RedBlackNode lc, RedBlackNode rc){
        this.key = key;
        this.value = value;
        this.p = p;
        this.color = color;
        this.lc = lc;
        this.rc = rc;
    }

    /**
     * return color
     * @return
     */
    public int getColor(){
        return color;
    }

    /**
     * return key
     * @return
     */

    public String getKey(){
        return key;
    }

    /**
     * return value
     * @return
     */
    public BigInteger getValue(){
        return value;
    }

    /**
     * return left child
     * @return
     */

    public RedBlackNode getLc(){
        return lc;
    }

    /**
     * return right child
     * @return
     */

    public RedBlackNode getRc(){
        return rc;
    }

    /**
     * return parent node
     * @return
     */

    public RedBlackNode getP(){
        return p;
    }

    /**
     * set up key value
     * @param key
     */

    public void setKey(String key){
        this.key = key;
    }

    /**
     * set up value's value
     * @param value
     */

    public void setValue(BigInteger value){
        this.value = value;
    }

    /**
     * set up left child
     * @param lc
     */

    public void setLc(RedBlackNode lc){
        this.lc = lc;
    }

    /**
     * set up right child
     * @param rc
     */

    public void setRc(RedBlackNode rc){
        this.rc = rc;
    }

    /**
     * set up parent node
     * @param p
     */

    public void setP(RedBlackNode p){
        this.p = p;
    }

    /**
     * set up color
     * @param color
     */

    public void setColor(int color){
        this.color = color;
    }
}
