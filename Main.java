import javax.sound.midi.Receiver;

/**
 * Created by yinningliu on 10/2/17.
 */
public class Main {

    public static void main(String[] strs){
        Broker<Integer> broker = new WaitNotifyClass<Integer>();
        new Thread(new Producer(broker,"prod 1")).start();
        new Thread(new Consumer(broker,"consumer 1")).start();
        new Thread(new Consumer(broker,"consumer 2")).start();
    }

}
