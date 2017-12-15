import java.math.BigInteger;
import java.util.Random;

/**
 * Implementation of Merkle Hellman Knapsack Encryption algorithm
 * Created by yinningliu on 17/2/1.
 */
abstract class MerkleHellmanKnapsack {
    /**
     *   private value to Save the increasing Number
     */
    private BigInteger sumOfSuperIncreasingNumber;

    /**
     *   protected value to save the input length
     */
    protected int inputLength;

    /**
     * protected value to save the r value
     */
    protected BigInteger r;

    /**
     * protected value to save the q value
     */
    protected BigInteger q;

    /**
     * define the maximum length of elements.
     */
    protected final int NUMBER_OF_NODE = 640;

    /**
     * construct method
     * initialized the sumOfSuperIncreasingNumber
     * @postcondition
     *      sumOfSuperIncreasingNumber value is 0
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     */

    public MerkleHellmanKnapsack() {
        sumOfSuperIncreasingNumber = new BigInteger("0");
    }

    /**
     * calculate the next super increasing number
     * @return
     *      next super increasing number
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     */

    protected BigInteger generateNextSuperIncreasingNumber(){
        // define a random object
        Random random = new Random();
        // Random to select a value based on the previous values + [1,4)
        sumOfSuperIncreasingNumber = sumOfSuperIncreasingNumber.add(new BigInteger(random.nextInt(3) + 1 + ""));

        // double values means to guarantee the next value is greater than the sum of previous Numbers
        sumOfSuperIncreasingNumber = sumOfSuperIncreasingNumber.add(sumOfSuperIncreasingNumber);

        return sumOfSuperIncreasingNumber;
    }

    /**
     * set up q value
     * @bigthetavalues
     *      worst case Θ(1) and best case is Θ(1)
     * @postcondition
     *      set up a new q value
     */

    protected void setQ(){
        q = generateNextSuperIncreasingNumber();
    }

    /**
     * set up r value
     * @bigthetavalues
     *      best case is Θ(1) and worst case is Θ(n)
     * @postcondition
     *      set up a new q value
     */

    protected void setR(){
        // create a new random object
        Random random = new Random();
        // Generate a value of r and (r and q are coprimes)

        int check = q.subtract(new BigInteger("1")).compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE)));

        // set up n to control the following loops to avoid infinite loop
        int n = check>=0? Integer.MAX_VALUE: q.subtract(new BigInteger("1")).intValue();
        do {
            // r must be less than the q and greater than 1
            r = q.subtract(new BigInteger(String.valueOf(random.nextInt(n--))));
        } while ((r.compareTo(new BigInteger("0")) > 0) && (q.gcd(r).compareTo(new BigInteger("1"))!=0)); // check whether r is valid value, if not, continue to find a R
    }

    /**
     * define abstract method and child class must implement it
     */
    protected abstract void generateKeys();


    /**
     * define abstract method and child class must implement it
     * @param index
     *      position of private key
     * @return
     *      return BigInteger value
     */

    protected abstract BigInteger getPrivateKeyData(int index);

    /**
     * define abstract method and child class must implement it
     * @param index
     *      position of private key
     * @return
     *      return BigInteger value
     */

    protected abstract BigInteger getPublicKeyData(int index);

    /**
     * define abstract method and child class must implement it
     */

    protected abstract void setPrivateKey();

    /**
     * define abstract method and child class must implement it
     */


    protected abstract void setPublicKey();

    /**
     * encrypt the message
     * @precondition
     *      input length must be less than 80 and input cannot be null
     * @postcondition
     *      save the inputlength into the variable of inputlength
     * @param input
     *      input char[] need to be encrypted
     * @return
     *      BigInteger of Encrypted message
     * @bigthetavalues
     *      best case and worst case is based on how to implement getPublicKeyData. Thus, I put this into child class
     */

    protected BigInteger encrypt(char[] input) {

        //precondition check
        if (input==null && input.length > 80) {
            return null;
        }

        //save the input length
        inputLength = input.length * 8;

        // message will hold the encrypted number
        BigInteger message = new BigInteger("0");

        // for loop to iterator each char in the char[]
        for (int i = 0; i < input.length; i++) {
            //get one of the chars
            char c = input[i];
            // k to control the bit from highest to lowest
            int k = 7;
            // for each char, we need to 8 times loop
            int j = 0;
            while(j < 8){
                // get corresponding publick key value
                BigInteger temp = getPublicKeyData(i * 8 + j);
                // when 1s, then add this public key into the sum
                if ((c & (1 << k--)) != 0) {
                    message = message.add(temp);
                }
                j++;
            }
        }

        return message;
    }

    /**
     * encrypte the message from biginteger to original string
     * @param message
     *      encrypted message
     * @return
     *      original input string
     * @precondition
     *      message cannot be null
     * @bigthetavalues
     *      best case and worst case is based on how to implement getPrivateKeyData. Thus, I put this into child class
     */

    public String decrypt(BigInteger message) {

        if(message == null)
                return "This message is empty. Cannot be encrypted";

        BigInteger bi = r;
        BigInteger bi2 = q;
        BigInteger bi3 = message;
        BigInteger bi4 = bi.modInverse(bi2);

        // This value is the result after performing the modular inverse
        BigInteger bi5 = bi3.multiply(bi4).mod(bi2);

        // hold the bits and later converted to a char array
        int[] bits = new int[inputLength];

        // create i to iterator from end of bit to the begin of bit
        int i = inputLength - 1;

        while(bi5.compareTo(new BigInteger("0")) != 0){

            // get private key value
            BigInteger temp = getPrivateKeyData(i);

            // if value is greater than current temp, then need to be substracted.
            if (bi5.compareTo(temp) >= 0) {
                bi5 = bi5.subtract(temp);
                bits[i] = 1;
            }
            i--;
        }


        // The bits array is moved into char array which is encrypted string
        char[] returnWords= new char[inputLength / 8];

        int singleChar = 0;
        int j = 7;
        int index_returnWords = 0;
        for(int k = 0;k<bits.length;k++){

            // each 8 bits to become one real char, then move to the next char
            if(j==-1){
                returnWords[index_returnWords++] = (char) singleChar;
                // reset up bit index
                j=7;
                singleChar = 0;
            }

            if (bits[k] == 1) {
                singleChar  = singleChar | (1 << j);
            }

            j--;
        }

        // set up last round
        returnWords[index_returnWords] = (char) singleChar;

        return new String(returnWords);
    }
}
