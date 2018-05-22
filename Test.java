package design.pattern;

public class Test {

    public static void main(String[] args) {
        FactoryOfHuman human= new FactoryOfHuman();
       Human h= human.gethuman("BOY");
       h.eat();
    }
}
