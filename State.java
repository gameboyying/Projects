/**
 * Created by yinningliu on 5/4/17.
 */
public class State {

    private int value;
    private Transition[] transitions;

    public State(int value){
        this.value = value;
        this.transitions = new Transition[255];
    }

    public int getValue(){
        return value;
    }
    // assign a symbol with transition. for example: '1' will use transitons[1] to save transitons information
    public void addTransition(char Symbol, Transition transition){
        transitions[Symbol-'0'] = transition;
    }

    // get transition information
    public Transition getTransition(char Symbol){
        return transitions[Symbol-'0'];
    }
}
