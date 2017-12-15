import java.math.BigInteger;

/**
 * Implementation of Merkle Hellman Knapsack Encryption algorithm (Arrays)
 * Created by yinningliu on 17/2/1.
 */
public class MerleHellmanKnapsackArrays extends MerkleHellmanKnapsack{

    /**
     * define array of private key
     */
    private BigInteger[] privateKey;

    /**
     * define array of public key
     */
    private BigInteger[] publicKey;


    /**
     * construct method
     * @postcondition
     *      initialize the private key and public key and run generatekey method
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */
    public MerleHellmanKnapsackArrays() {
        privateKey = new BigInteger[NUMBER_OF_NODE];
        publicKey = new BigInteger[NUMBER_OF_NODE];
        generateKeys();
    }

    /**
     * implement parent's abstract method
     * @param index
     *      position of private Key
     * @return
     *      return particular big Integer value from the private key array
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     */
    protected BigInteger getPrivateKeyData(int index){
        return privateKey[index];
    }

    /**
     * implement parent's abstract method
     * @param index
     *      position of private Key
     * @return
     *      return particular big Integer value from the public key array
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     */

    protected BigInteger getPublicKeyData(int index){
        return publicKey[index];
    }

    /**
     * implement parent's abstract method
     *
     * @postcondition
     *      set up value and fill in the array of privatekey
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */

    protected void setPrivateKey(){
        int i = 0;
        while(i < super.NUMBER_OF_NODE) {
            privateKey[i++] = super.generateNextSuperIncreasingNumber();
        }
    }

    /**
     * implement parent's abstract method
     *
     * @postcondition
     *      set up value and fill in the array of publickey
     * @bigthetavalues
     *      worst case Θ(n) and best case is Θ(n)
     */

    protected void setPublicKey(){
        int i = 0;
        while(i < super.NUMBER_OF_NODE) {
            publicKey[i] = getPrivateKeyData(i).multiply(super.r).mod(super.q);
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
     *      best case is Θ(n) and worst case is Θ(n)
     * @note
     *      use parent class method. Thus pre and post condition, best/worst case is the same as the parent class method
     */

    public BigInteger encrypt(char[] input) {
        return super.encrypt(input);
    }

    /**
     * decrypt the message
     * @bigthetavalues
     *      best case is Θ(n) and worst case is Θ(n)
     * @note
     *      use parent class method. Thus pre and post condition, best/worst case is the same as the parent class method
     */

    public String decrypt(BigInteger message) {
        return super.decrypt(message);
    }
}
