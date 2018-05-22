package overridiing.overloading;

public class OverLoadded {

    public void m(String s){
        System.out.println("String overloaded");
    }
    public void m(Object s){
        System.out.println("Object overloaded");
    }
    public void m(Integer s){
        System.out.println("Integer overloaded");
    }
    public void m(Exception s){
        System.out.println("Exception overloaded");
    }
    public void m(StringBuffer s){
        System.out.println("StringBuffer overloaded");
    }
    public void m(StringBuilder s){
        System.out.println("StringBuilder overloaded");
    }
    public int m(int a){
        System.out.println("test return");
        return 1;
    }
    public String m()
    {
        return "hello";
    }
    public static void main(String[] args) {
        
 
        OverLoadded o = new OverLoadded();
        o.m(1);
        o.m("test");
        o.m(-1);
        }
}
