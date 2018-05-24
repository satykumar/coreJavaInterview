package wrapper.classs;

public class BoxingClass {
    public static void main(String[] args)
    {
        byte b = 10;             //Primitive byte data
        Byte B = new Byte(b);   //Wrapping primitive byte data into Byte Object
 System.out.println(B);
        short s = 15;              //Primitive short data
        Short S = new Short(s);   //Wrapping of primitive short data into Short Object
        System.out.println(S);
        int i = 20;                   //Primitive int Data
        Integer I = new Integer(i);   //Wrapping of primitive int data into Integer Object
        System.out.println(I);
        long l = 25;            //Primitive long data
        Long L = new Long(l);   //Wrapping primitive long data into Long Object
        System.out.println(L);
        float f = 12;            //Primitive float data
        Float F = new Float(f);  //Wrapping primitive float data into Float Object
        System.out.println(F);
        double d = 18.58;           //Primitive double data
        Double D = new Double(d);   //Wrapping primitive double data into Double Object
        System.out.println(D);
        boolean bln = true;               //Primitive boolean data
        Boolean BLN = new Boolean(bln);   //Wrapping primitive boolean data into Boolean Object
        System.out.println(BLN);
        char c = 'C';                     //Primitive char data
        Character C = new Character(c);   //Wrapping primitive char data into Character Object
        System.out.println(C);
 
    }
}
