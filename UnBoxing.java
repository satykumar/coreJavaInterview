package wrapper.classs;

public class UnBoxing {
    public static void main(String[] args)
    {
        Byte B = new Byte((byte) 10);   //Byte Object
        byte b = B.byteValue();         //Unwrapping Byte object to byte data
 
        Short S = new Short((short) 20);   //Short Object
        short s = S.shortValue();          //Unwrapping Short object to short data
 
        Integer I = new Integer(15);    //Integer Object
        int i = I.intValue();           //Unwrapping Integer object to int data
 
        Long L = new Long(50);     //Long Object
        long l = L.longValue();    //Unwrapping Long object to long data
 
        Float F = new Float(20);      //Float Object
        float f = F.floatValue();    //Unwrapping Float object to float data
 
        Double D = new Double(20.5);   //Double Object
        double d = D.doubleValue();    //Unwrapping Double object to double data
 
        Boolean BLN = new Boolean(true);      //Boolean Object
        boolean bln = BLN.booleanValue();    //Unwrapping Boolean object to boolean data
 
        Character C = new Character('C');    //Character Object
        char c = C.charValue();              //Unwrapping Character object to char data
    }
}
