package design.pattern;

public class SingleTon {
    
    private static SingleTon instance;
    
    private SingleTon(){
        
    }

    /* Lazy initialization, creating object on first use */
    public static SingleTon getInstance(){
        if(instance==null){
        instance= new SingleTon();
        }
        
        return instance;
        
    }
    
    public void display() {
        System.out.println("Hurray! I am display from Singleton!");
    }
}
