/**
 * Created by yinningliu on 10/2/17.
 */


public class Producer implements Runnable {
    private final Broker<Integer> broker;
    private final String name;

    public Producer(Broker<Integer> broker, String name){
        this.broker = broker;
        this.name = name;
    }

    public void run(){
        try{
            for(int i=0;i<5;i++){
                broker.put(i);
                System.out.format("%s produced: %s%n", name, i);
                Thread.sleep(1000);
            }
            broker.put(-1);
            System.out.println("produced termination signal");

        }
        catch (InterruptedException ex){
            ex.printStackTrace();
            return;
        }

    }
}
