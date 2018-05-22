package jav.serilization.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ExternalizableExample {
public static void main(String[] args) {
    TestExtern test= new TestExtern();
    test.setName("Ruby");
    test.setPass("Rails");
    test.setAge(4);
    test.setGenger(true);
    
    System.out.println("before serilization "+test);
    storeTestExtern(test);
    TestExtern loadedSettings = loadSettings();
    System.out.println(loadedSettings);
}
    private static void storeTestExtern(TestExtern tes) 
    {
        try {
            FileOutputStream fos = new FileOutputStream("object.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tes);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static TestExtern loadSettings() {
        try {
            FileInputStream fis = new FileInputStream("object.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TestExtern settings =  (TestExtern) ois.readObject();
            ois.close();
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}

