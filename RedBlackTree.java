import java.math.BigInteger;

/**
 * Created by yinningliu on 17/2/25.
 */
public class RedBlackTree {

    /**
     * set up root Node
     */
    private RedBlackNode root;

    /**
     * set up nil node
     */
    private RedBlackNode nil;

    /**
     * inialized the RedBlackTree
     */
    public RedBlackTree(){
        // inialized the nil node
        // nil node without any values but it has black color properties
        this.nil = new RedBlackNode(null,null,null,0,null,null);
        // when tree is empty, nil is root.
        this.root = nil;
    }

    /**
     * insert key,value pairs in the tree
     * @param key
     * @param value
     */
    public void insert(String key, BigInteger value){

        // y is nil
        RedBlackNode y = nil;

        // x is root
        RedBlackNode x = root;

        // to find last parent node
        while(x!=nil){
             y = x;
             // when key is smaller than x's key, search left
             if(key.compareTo(x.getKey())<0){
                 x = x.getLc();
             }
             // when key is bigger than x's key, search right
             else if(key.compareTo(x.getKey())>0){
                 x = x.getRc();
             }
             // find a node's key is equal to the key, re setup its value, return and stop method
             else{
                 x.setValue(value);
                 return;
             }
        }

        // y is parent
        RedBlackNode parentZ = y;

        // z is new Node
        RedBlackNode z = new RedBlackNode(key, value,parentZ,1,nil,nil);

        // when y is nil, it prove this empty tree, just set up root == z
        if(y == nil){
            root = z;
        }
        //
        else{
            // put into left
            if(key.compareTo(y.getKey())<0){
                y.setLc(z);
            }
            //put into right
            else if(key.compareTo(y.getKey())>0) {
                y.setRc(z);
            }
            // keys are the same, thus, update y's value
            else{
                y.setValue(value);
                return;
            }
        }

        // set up left child
        z.setLc(nil);

        // set up right child
        z.setRc(nil);

        // set up color
        z.setColor(1);

        // check whether to do rotation and change parent and uncle's colors
        RBInsertFixup(z);
    }

    /**
     * rebalance the redblack tree and change the color
     * @param z
     */

    private void RBInsertFixup(RedBlackNode z){

        // loop when z's color is 1
        while(z.getP().getColor() == 1){


            if(z.getP() == z.getP().getP().getLc()){
                // get uncle
                RedBlackNode y = z.getP().getP().getRc();
                // if uncle's color is red, change color only
                if(y.getColor()==1){
                    // change parent color is black,
                    z.getP().setColor(0);
                    // change uncle color is black
                    y.setColor(0);
                    // change parent parent is color is red
                    z.getP().getP().setColor(1);
                    // z move to the parent's parent
                    z = z.getP().getP();
                }
                else{
                    // if zigzag
                    if(z==z.getP().getRc()){
                        z = z.getP();
                        leftRotate(z);
                    }

                    z.getP().setColor(0);
                    z.getP().getP().setColor(1);
                    rightRotate(z.getP().getP());

                }
            }
            else{

                // get uncle
                RedBlackNode y = z.getP().getP().getLc();

                // if uncle is red
                if(y.getColor() == 1){
                    z.getP().setColor(0);
                    y.setColor(0);
                    z.getP().getP().setColor(1);
                    z = z.getP().getP();
                }
                else{

                    // if zigzag
                    if(z==z.getP().getLc()){
                        z = z.getP();
                        rightRotate(z);
                    }
                    z.getP().setColor(0);
                    z.getP().getP().setColor(1);
                    leftRotate(z.getP().getP());

                }
            }
        }

        // set root's color to black
        root.setColor(0);
    }

    /**
     * left rotate
     * @param x
     */
    private void leftRotate(RedBlackNode x){

        // pre condition, right node is not a nil
        if(x.getRc()==nil)
            return;

        //pre condition, root's parent is nil
        if(root.getP()!=nil)
            return;

        // y now points to node to right of x
        RedBlackNode y = x.getRc();

        // y's right subtree becomes x's right subtree
        x.setRc(y.getLc());

        // left subtree of y gets a new parent
        y.getLc().setP(x);

        // y's parent is now x's parent
        y.setP(x.getP());

        // if x is a root then y becomes new root
        if(x.getP()==nil){
            root = y;
        }
        else{
            // if x is a left child then adjust x's parent's left child
            if(x == x.getP().getLc()){
                x.getP().setLc(y);
            }

            // adjust x's parent's right child
            else{
                x.getP().setRc(y);
            }
        }

        // the left child of y is now x
        y.setLc(x);

        // the parent of x is now y
        x.setP(y);
    }

    /**
     * right rotate
     * @param x
     */

    private void rightRotate(RedBlackNode x){

        //pre condition, left node is not a nil
        if(x.getLc()==nil){
            return;
        }

        //pre condition, root's parent is nil
        if(root.getP()!=nil){
            return;
        }

        // y now points to node to left of x
        RedBlackNode y = x.getLc();
        // y's right subtree becomes x's left subtree
        x.setLc(y.getRc());
        // right subtree of y gets a new parent
        y.getRc().setP(x);
        // y's parent is now x's parent
        y.setP(x.getP());

        // if x is a root then y becomes new root
        if(x.getP() == nil){
            root = y;
        }
        else{
            // if x is a left child then adjust x's parent's left child
            if(x==x.getP().getLc()){
                x.getP().setLc(y);
            }
            // adjust x's parent's right child
            else{
                x.getP().setRc(y);
            }
        }

        // the right child of y is now x
        y.setRc(x);

        // the parent of x is now y
        x.setP(y);
    }

    /**
     * look up key, if not found, return null. Otherwise, return value
     * @param key
     * @return
     */
    public BigInteger lookUp(String key){
        // tree is empty
        if(isEmpty()){
            //System.out.println("Tree is Empty");
            return null;
        }

        RedBlackNode dummyNode = root;

        // when reach to the nil, stop while
        while(dummyNode!=nil){
              if(key.compareTo(dummyNode.getKey())<0){
                  dummyNode = dummyNode.getLc();
              }
              else if(key.compareTo(dummyNode.getKey())>0){
                  dummyNode = dummyNode.getRc();
              }
              else{
                  // find key, return key's value
                  return dummyNode.getValue();
              }
        }
        // nothing found, return null
        return null;
    }



    public void	inOrderTraversal(){
        if(isEmpty())
            System.out.println("Tree is Empty");

        inOrderTraversal(root);
    }

    public void inOrderTraversal(RedBlackNode root){

        if(root==nil)
            return;

        inOrderTraversal(root.getLc());
        System.out.println("Key is " + root.getKey() + "; Value is " + root.getValue() + "; color is " + root.getColor());
        inOrderTraversal(root.getRc());
    }

    /**
     * check if tree is empty
     * @return
     */

    public boolean isEmpty(){
        if(root==nil)
            return true;
        return false;
    }

    public static void main(String[] args){
        RedBlackTree tree = new RedBlackTree();

        //Insert Test case;

        System.out.println("Insert: key is liu and value is 10");
        tree.insert("liu",new BigInteger("10"));
        System.out.println("Insert: key is yin and value is 11");
        tree.insert("yin",new BigInteger("11"));
        System.out.println("Insert: key is ning and value is 15");
        tree.insert("ning",new BigInteger("15"));

        // test replace when key is the same
        System.out.println("Insert: key is ning and value is 17");
        tree.insert("ning", new BigInteger("17"));

        System.out.println("Insert: key is professor and value is 9");
        tree.insert("professor",new BigInteger("9"));

        System.out.println("Insert: key is class and value is 13");
        tree.insert("class",new BigInteger("13"));

        // read ning;
        System.out.println("Read from key is ning, value is:" + tree.lookUp("ning"));

        System.out.println("Display in order traversal, 0 is black and 1 is red");

        tree.inOrderTraversal();

    }
}
