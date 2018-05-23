package dead.lock;

public class DeadlockCreation {
public static void main(String[] args) throws InterruptedException {
    Thread t= new Thread(new A());
    Thread t1= new Thread(new B());
    t.start();
    t.join();
    t1.start();
}
}
