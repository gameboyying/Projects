/**
 * Created by yinningliu on 4/22/17.
 */
public class Transition {

    // move left
    public static final boolean LEFT = false;
    // move right
    public static final boolean RIGHT = true;

    private boolean moveDirection; //true is right, false is left

    // replaced symbol
    private char replacement;
    // to next state
    private int toState;

    // get replaced symbol
    public char getReplacement(){
        return replacement;
    }

    // get next state
    public int getState(){
        return toState;
    }

    // get moving direction
    public boolean getMoveDirection(){
        return moveDirection;
    }

    //inialized the transition
    public Transition(char writeSymbol, boolean direction ,int toState){
            this.moveDirection = direction;
            this.replacement = writeSymbol;
            this.toState = toState;
    }
}
