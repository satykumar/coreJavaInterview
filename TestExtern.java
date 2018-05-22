package jav.serilization.test;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TestExtern implements Externalizable {

    private String name;
    private String pass;
    private Integer age;
    private Boolean genger;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Boolean getGenger() {
        return genger;
    }
    public void setGenger(Boolean genger) {
        this.genger = genger;
    }
    public TestExtern(String name, String pass, Integer age, Boolean genger) {
        super();
        this.name = name;
        this.pass = pass;
        this.age = age;
        this.genger = genger;
    }
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
        out.writeBoolean(true);

        
    }
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
     name=   in.readUTF();
     age=   in.readInt();
     genger= in.readBoolean();
        
    }
    @Override
    public String toString() {
        return "TestExtern [name=" + name + ", pass=" + pass + ", age=" + age + ", genger=" + genger + "]";
    }
  
  public TestExtern() {
    // TODO Auto-generated constructor stub
}  
}
