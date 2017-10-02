import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yinningliu on 10/2/17.
 */
public class WaitNotifyClass<T> implements Broker<T> {
    public final BlockingQueue<T> q;

    public WaitNotifyClass(){
        this.q = new LinkedBlockingQueue<>();
    }

    public void put(T obj){
        try{
            q.put(obj);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public T take(){
        try{
            return q.take();
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
