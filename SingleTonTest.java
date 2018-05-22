package design.pattern;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

public class SingleTonTest {
public static void main(String[] args) throws CloneNotSupportedException {
   // SingleTon instance= new SingleTon();  the constructor singleton is not visible
       SingleTon object = SingleTon.getInstance();
       object.display();
       System.out.println(object.hashCode());
       System.out.println("cloniing");
      // SingleTon instance2 = (SingleTon) object.clone();
        SingleTonClone objcetclone = SingleTonClone.getInstance();
       System.out.println(objcetclone.hashCode());
       System.out.println("clonning of object");
       
     //  SingleTonClone test = (SingleTonClone)objcetclone.clone();
      // System.out.println(test.hashCode());
       
       System.out.println("Serilaization case ");
       objcetclone.setValue(30);
       try {
           // Serialize to a file
           ObjectOutput out = new ObjectOutputStream(new FileOutputStream("filename.txt"));
           out.writeObject(objcetclone);
           out.close();

           objcetclone.setValue(20);
           
           // Serialize to a file
           ObjectInput in = new ObjectInputStream(new FileInputStream("filename.txt"));
           SingleTonClone instanceTwo = (SingleTonClone) in.readObject();
           in.close();

           System.out.println("Instance One Value= " + objcetclone.getValue());
           System.out.println("Instance Two Value= " + instanceTwo.getValue());

       } catch (IOException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }
System.out.println("How To Break SingleTon Using Reflection ");
SingleTonClone objcetclone1=null;
try {
    // Below code will change constructor access level from private to public
    Constructor constructor = SingleTonClone.class.getDeclaredConstructor();
    constructor.setAccessible(true);
     
    // Creating second instance
    objcetclone1 = (SingleTonClone) constructor.newInstance();

} catch (Exception e) {
    e.printStackTrace();
}
System.out.println("instanceOne HashCode : " + objcetclone.hashCode());
System.out.println("instanceTwo HashCode : " + objcetclone1.hashCode());

}
}


