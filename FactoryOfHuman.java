package design.pattern;

public class FactoryOfHuman {

    public  Human gethuman(String Type){
        if(Type == null){
            return null;
         }     
         if(Type.equalsIgnoreCase("BOY")){
            return new Boy();
            
         } else if(Type.equalsIgnoreCase("GIRL")){
            return new Girl();
            
         }
        return null;
}
    }
