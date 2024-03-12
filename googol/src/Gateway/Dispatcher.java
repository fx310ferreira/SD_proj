package Gateway;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Dispatcher {
    ConcurrentLinkedQueue<String> url_queue;

    public Dispatcher(){
        url_queue = new ConcurrentLinkedQueue<>();
    }

    synchronized void push(String url) {
        url_queue.add(url);
        System.out.println(url_queue);
        notify();
    }

    synchronized String pop(){
        while(url_queue.isEmpty())
            try {
                wait();
            } catch (Exception e) {
                System.out.println("interruptedException caught");
            }
        String url = url_queue.poll();
        notify();
        return url;
    }

    @Override
    public String toString(){
        return url_queue.toString();
    }
    
}
