/**
 * Created by yinningliu on 10/2/17.
 */
public interface Broker<T>{
    T take();
    void put(T obj);
}
