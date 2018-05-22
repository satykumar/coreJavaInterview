package design.pattern;

import java.io.Serializable;

public class SingleTonClone implements Cloneable, Serializable{
    private static SingleTonClone instanceClone;
    private int value;
    private SingleTonClone(){
        
    }
public static SingleTonClone getInstance(){
    if(instanceClone==null){
        instanceClone= new SingleTonClone();
    }
    return instanceClone;
}
/*@Override
protected Object clone() throws CloneNotSupportedException {
  
    return super.clone();
}*/
/* Restrict cloning of object */
/*@Override
protected Object clone() throws CloneNotSupportedException 
{
  throw new CloneNotSupportedException();
}*/
/* Restrict cloning of object and return same object  */
@Override
protected Object clone() throws CloneNotSupportedException 
{
  return instanceClone;
}

public int getValue() {
    return value;
}

public void setValue(int value) {
    this.value = value;
}
protected Object readResolve() {
    return getInstance();
}
}
