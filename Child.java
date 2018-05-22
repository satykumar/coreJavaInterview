package overridiing.overloading;

public class Child extends Parent {

    static {
        System.out.println("child static block");
    }
    
    {
        System.out.println("child Instatant Block");
    }
    public Child() {
        System.out.println("child construtor");
    }
    public static void m(){
        System.out.println("child static");
    }

    public void p(){
        System.out.println("child protected");
    }
        
    private void pr(){
        System.out.println("child private");
    }

//    final void f(){
//        System.out.println("parent final");
//    } final will not override
    
  
  protected void d(){
        System.out.println("child defalut");
    }
    // defalut method can be protected ,public and defalut it self 
  
  public void ch(){
      System.out.println("child only");
  }
  public void ch1(){
      System.out.println("child only1");
  }
}
