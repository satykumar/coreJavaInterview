package wrapper.classs;

public class WrapperClasses {
   /* static void overloadedMethod(Integer I)
    {
        System.out.println("Integer Wrapper Class Type");
    }*/
 
//    static void overloadedMethod(long l)
//    {
//        System.out.println("long primitive type");
//    }
 
    static void overloadedMethod(Long L)
    {
        System.out.println("Long Wrapper Class Type");
    }
    
    static void overloadedMethod(Double D)
    {
        System.out.println("Double Wrapper Class Type");
    }
    static void overloadedMethod(Number N)
    {
        System.out.println("Number Class Type");
    }
    public static void main(String[] args)
    {
        int i = 2;
 
        overloadedMethod(i);
    }
}
