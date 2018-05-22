package overridiing.overloading;

public class Parent {
    
    static {
        System.out.println("parent static block");
    }
    
    {
        System.out.println("parent Instatant Block");
    }
    
    public Parent() {
       System.out.println("parent constructor");
    }
    
public static void m(){
    System.out.println("parent static");
}

protected void p(){
    System.out.println("parent protected");
}
    
private void pr(){
    System.out.println("parent private");
}

final void f(){
    System.out.println("parent final");
}
void d(){
    System.out.println("parent defalut");
}
public void pao(){
    System.out.println("parent only method");
}

public void pao1(){
    System.out.println("parent only method1");
}
}
