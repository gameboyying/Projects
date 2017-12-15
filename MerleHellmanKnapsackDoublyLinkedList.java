import java.math.BigInteger;

/**
 * Implementation of Merkle Hellman Knapsack Encryption algorithm(Linkedlist)
 * Created by yinningliu on 17/2/1.
 */
public class MerleHellmanKnapsackDoublyLinkedList extends MerkleHellmanKnapsack {

    /**
     * define array of private key
     */
    private DoublyLinkedList privateKey;

    /**
     * define array of public key
     */
    private DoublyLinkedList publicKey;

    /**
     * construct method
     * @postcondition
     *      initialize the private key and public key and run generatekey method
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */
    public MerleHellmanKnapsackDoublyLinkedList() {
        privateKey = new DoublyLinkedList();
        publicKey = new DoublyLinkedList();
        generateKeys();
    }

    /**
     * implement parent's abstract method
     * @param index
     *      position of private Key
     * @return
     *      return particular big Integer value from the private key linkedlist
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(1)
     */

    protected BigInteger getPrivateKeyData(int index){
        return privateKey.get(index).getData();
    }

    /**
     * implement parent's abstract method
     * @param index
     *      position of public Key
     * @return
     *      return particular big Integer value from the public key linkedlist
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(1)
     */

    protected BigInteger getPublicKeyData(int index){
        return publicKey.get(index).getData();
    }

    /**
     * implement parent's abstract method
     *
     * @postcondition
     *      set up value and fill in the linkedlist of privatekey
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */

    protected void setPrivateKey(){
        int i = 0;
        while(i < super.NUMBER_OF_NODE) {
            privateKey.addNodeAtEnd(super.generateNextSuperIncreasingNumber());
            i++;
        }
    }

    /**
     * implement parent's abstract method
     *
     * @postcondition
     *      set up value and fill in the linkedlist of publickey
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */


    protected void setPublicKey(){
        int i = 0;
        while(i < super.NUMBER_OF_NODE) {
            publicKey.addNodeAtEnd(getPrivateKeyData(i).multiply(r).mod(q));
            i++;
        }
    }

    /**
     * Generates keys based on input data size
     *
     * @postcondition
     *      set up private key, q, r and public key
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */

    protected void generateKeys() {

        // Generating values for w
        setPrivateKey();
        // Generate value for q
        super.setQ();
        // Generate value for r
        super.setR();
        // Generate public key
        setPublicKey();

    }

    /**
     * encrypt the message
     * @bigthetavalues
     *      best case is Θ(n^2) and worst case is Θ(n^2)
     * @note
     *      use parent class method. Thus pre and post condition is the same as the parent class method
     */

    public BigInteger encrypt(char[] input) {
        return super.encrypt(input);
    }

    /**
     * decrypt the message
     * @bigthetavalues
     *      best case is Θ(n^2) and worst case is Θ(n^2)
     * @note
     *      use parent class method. Thus pre and post condition is the same as the parent class method
     */

    public String decrypt(BigInteger message) {
        return super.decrypt(message);
    }
}
