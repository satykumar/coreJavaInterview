package dead.lock;

public class B implements Runnable {

    @Override
    public void run() {
     synchronized (Object.class) {
         System.out.println(Thread.currentThread().getName() + " has acquired "
                 + "lock on Object class and waiting to acquire lock on String class...");  
    }
      try{
          Thread.sleep(100);
      }
      catch(Exception e){
          e.printStackTrace();
      }
      synchronized (String.class) {
          System.out.println(Thread.currentThread().getName() + " has acquired "
                  + "lock on Object class and waiting to acquire lock on String class...");
    }
    }

}
