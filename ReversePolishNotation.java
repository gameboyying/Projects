import java.math.BigInteger;
import java.util.Scanner;

/**
 * Created by yinningliu on 17/2/26.
 */
public class ReversePolishNotation {
    /**
     * create stack to save the numbers
     */
    private Stack numbers = new Stack();

    /**
     * create tree to save the key
     */
    private RedBlackTree variables = new RedBlackTree();


    /**
     * calculate bigInteger from input
     * @param expression
     * @return
     * @throws Exception
     */

    public BigInteger calculate(String expression) throws Exception
    {
        Scanner scanner = new Scanner(expression);
        String next;
        // read String by String
        while(scanner.hasNext()){
            next = scanner.next();
            // if string is an operator
            switch (next.charAt(0)){
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '~':
                case '=':
                case '#': calculate(next.charAt(0));continue;
            }

            // if string is a number
            if(isNumeric(next)) {
                numbers.push(new BigInteger(next));
            }
            // if string is a variable
            else{
                numbers.push(next);
            }
        }



        // when stack is empty
        if(numbers.isEmpty()){
            return null;
        }
        // pop the result
        else {
            return findValue(numbers.pop());
        }
    }

    private BigInteger findValue(Object obj) throws Exception{

        // if object is BigInteger
        if(obj instanceof BigInteger){

            // return BigInteger
            return (BigInteger)obj;
        }
        // if object is String
        else{
                // to find variable's bigInteger
                BigInteger res = variables.lookUp((String) obj);

                // if not found, throw an exception
                if(res ==null) {
                    throw new Exception("Error: no variable " + (String) obj);
                }

                // return BigInteger
                return res;
        }
    }

    private boolean isNumeric(String next){
        // input begin with a letter. Thus, if first char is in range of a-z or A-Z, this is variable
        if((next.charAt(0)>='a' && next.charAt(0)<='z') || (next.charAt(0)>='A' && next.charAt(0)<='Z')){
            return false;
        }
        else{
            return true;
        }
    }

    private void calculate(char operations) throws Exception{

        if(operations == '#'){
                BigInteger s3 = findValue(numbers.pop());
                BigInteger s2 = findValue(numbers.pop());
                BigInteger s1 = findValue(numbers.pop());
                BigInteger res = s1.pow(s2.intValue()).mod(s3);
                numbers.push(res);
                return;
        }

        if(operations == '~'){
                BigInteger s1 = findValue(numbers.pop());
                BigInteger res = s1.negate();
                numbers.push(res);
                return;
        }

        if(operations == '+') {
                BigInteger s2 = findValue(numbers.pop());
                BigInteger s1 = findValue(numbers.pop());
                BigInteger res= s1.add(s2);
                numbers.push(res);
                return;
        }

        if(operations == '-') {
                BigInteger s2 = findValue(numbers.pop());
                BigInteger s1 = findValue(numbers.pop());
                BigInteger res = s1.subtract(s2);
                numbers.push(res);
                return;
        }

        if(operations == '*') {
                BigInteger s2 = findValue(numbers.pop());
                BigInteger s1 = findValue(numbers.pop());
                BigInteger res = s1.multiply(s2);
                numbers.push(res);
                return;
        }

        if(operations == '/') {
            BigInteger s2 = findValue(numbers.pop());
            BigInteger s1 = findValue(numbers.pop());
            BigInteger res = s1.divide(s2);
            numbers.push(res);
            return;
        }

        if(operations == '%') {
            BigInteger s2 = findValue(numbers.pop());
            BigInteger s1 = findValue(numbers.pop());
            BigInteger res = s1.mod(s2);
            numbers.push(res);
            return;
        }

        if(operations == '='){
            BigInteger s2 = findValue(numbers.pop());
            Object s1 = numbers.pop();
            // if s1 is BigInteger, throw exception
            if(s1 instanceof String == false){
                throw new Exception(s1.toString() + " is not an lvalue");
            }
            String s3 = (String)s1;
            numbers.push(s3);
            variables.insert(s3,s2);
            return;
        }

    }


    public static void main(String[] args){
        ReversePolishNotation RPN = new ReversePolishNotation();

        BigInteger res = null;
        while(true)
        {
            // get input
            Scanner scanner = new Scanner(System.in);

            // get input
            String str = scanner.nextLine();

            try {
                // pass to the calculate method
                res = RPN.calculate(str);
            }
            catch (Exception ex){

                // find exception and break this program
                System.out.println(ex.toString());
                break;
            }

            // when no input, stop program
            if(res == null)
                break;

            // print out the result
            System.out.println(res);
        }

        System.out.println("terminating");
    }
}
