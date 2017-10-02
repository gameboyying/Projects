/**
 * Created by yinningliu on 10/2/17.
 */
public class Consumer implements Runnable {

    private final Broker<Integer> broker;
    private final String name;

    public Consumer(Broker<Integer> broker, String name){
        this.broker = broker;
        this.name = name;
    }

    public void run(){
        try{
            for(Integer message = broker.take();message !=-1;message = broker.take()){
                System.out.format("%s produced: %s%n", name, message);
                Thread.sleep(1000);
            }
            System.out.println("Received Terminated");
        }
        catch(InterruptedException ex){
            ex.printStackTrace();
            return;
        }
    }
}
