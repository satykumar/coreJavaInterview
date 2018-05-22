package design.pattern;

public class SingleTonInThreadSafe {
private static SingleTonInThreadSafe instance;

private SingleTonInThreadSafe(){
    
}
//sysnchronize
public static synchronized SingleTonInThreadSafe getInstance(){
    //doubly check of object
    if(instance==null){
        synchronized (SingleTonInThreadSafe.class) {
            if (instance == null) {
                instance= new SingleTonInThreadSafe();
            }
        }
        
    }
    return instance;
    
}
}
