import java.util.*;
/**
 * Created by yinningliu on 4/22/17.
 */
public class Turing {
    // define a tapes
    private char[] tapes;
    // start with index
    private int start;
    // define state label;
    private int currentStateLabel;
    // Save States Information
    private State[] states;

    // initialize turing machine
    public Turing(int size){
        // initialize states array
        states = new State[size];
    }

    public void initialize(String inTape){
        //initial tapes array
        tapes = new char[40];
        //initial state label
        currentStateLabel = 0;
        //start from 20;
        start = 20;
        // initialize tapes, a small array filled with B's
        Arrays.fill(tapes,'B');
        // copy inTape to this tapes array
        char[] input = inTape.toCharArray();
        // laying down the input into the middle of this array before processing the input
        System.arraycopy(input,0,tapes,20,input.length);
    }

    public String execute(String inTape){
        //initialzed values
        initialize(inTape);

        //Start loop. When find halt state, then stop
        while(true){
            // state within the range
            if(currentStateLabel<states.length){
                // read first symbol
                char readSymbol = tapes[start];
                //get State information
                State curState = states[currentStateLabel];
                //check whether state is valid and reachable, if not, stop
                if(curState==null){
                    break;
                }
                // get transition
                Transition transition = curState.getTransition(readSymbol);
                // check whether we can find symbol with transition, if not, stop
                if(transition==null){
                    break;
                }
                // get next state and set up current state
                currentStateLabel = transition.getState();
                // update tapes symbol
                tapes[start] = transition.getReplacement();
                // check and move the index
                if(transition.getMoveDirection() == true){
                    start++;
                }
                else{
                    start--;
                }
            }
            // state not in the range. I determine this is halt state
            else{
                break;
            }
        }
        // return this tapes
        return String.valueOf(tapes);
    }

    public void addTransition(int stateNum, char original, Transition transition){
        // state in the range, add transition into turing machine. Otherwise, we cannot allow user to add state infomration
        // into the turing machine.
        if(stateNum<states.length){
            if(states[stateNum] == null){
                states[stateNum] = new State(stateNum);
            }
            // add transition into the state
            states[stateNum].addTransition(original,transition);
        }
    }



    public static void main(String args[]) {

        Turing machine = new Turing(1); // This machine will have one state.
        // Note: There is an additional halt state.
        // The values on the input tape are set to
        //  all B’s.

        Transition one =   new Transition('0',Transition.RIGHT, 0);
        Transition two =   new Transition('1',Transition.RIGHT, 0);
        Transition three = new Transition('B', Transition.LEFT,1);

        machine.addTransition(0, '0', two);
        machine.addTransition(0, '1', one);
        machine.addTransition(0, 'B', three);

        String inTape = "11111100010101"; // The leftmost value of inTape will be
        // placed under the read/write head.

        System.out.println(inTape);

        String outTape = machine.execute(inTape);
        System.out.println(outTape);
    }
}
