package Gateway;

public class Downloader extends Thread {
    Dispatcher dispatcher;
    int id;

    public Downloader(Dispatcher dispatcher, int id) {
        this.dispatcher = dispatcher;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Downloader " + id + " is ready");
        while (true) {
            try {
            System.out.println("Downloader " + id + " is waiting for a URL to download...");
            String url = dispatcher.pop();
            System.out.println("Downloader pooped " +  id + " url: " + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
