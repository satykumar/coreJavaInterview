package wrapper.classs;

public class IsNanAndIsInfinite {
    public static void main(String[] args)
    {
        //Assigning Not-A-Number value of type double to d1
        double d1 = Double.NaN;
 
        System.out.println(d1);            //Output : NaN
 
        //Assigning positive infinity value of type double to d2
        double d2 = Double.POSITIVE_INFINITY;
 
        System.out.println(d2);           //Output : Infinity
 
        //Assigning negative infinity value of type double to d3
        double d3 = Double.NEGATIVE_INFINITY;
 
        System.out.println(d3);           //Output : -Infinity
 
        //Assigning Not-A-Number value of type float to f1
        float f1 = Float.NaN;
 
        System.out.println(f1);         //Output : NaN
 
        //Assigning positive infinity value of type float to f2
        float f2 = Float.POSITIVE_INFINITY;
 
        System.out.println(f2);        //Output : Infinity
 
        //Assigning negative infinity value of type float to f3
        float f3 = Float.NEGATIVE_INFINITY;
 
        System.out.println(f3);       //Output : -Infinity
        
        
        //------------------------------------------
        
        double d = 0.0/0.0;
        
        System.out.println(Double.isNaN(d));    //Output : true
 
        d = Math.sqrt(-1.2);
 
        System.out.println(Double.isNaN(d));    //Output : true
 
        float f = 0.0f/0.0f;
 
        System.out.println(Float.isNaN(f));            //Output : true
 
        f = 0 * Float.POSITIVE_INFINITY;
 
        System.out.println(Float.isNaN(f));          //Output : true
        double d11 = Double.POSITIVE_INFINITY / 0.0;
        
        System.out.println(Double.isInfinite(d11));    //Output : true
 
        d11 = Double.NEGATIVE_INFINITY / 0.0;
 
        System.out.println(Double.isInfinite(d11));    //Output : true
 
        float f11 = Float.POSITIVE_INFINITY * 2.2f;
 
        System.out.println(Float.isInfinite(f11));            //Output : true
 
        f11 = Float.NEGATIVE_INFINITY * 4.12f;
 
        System.out.println(Float.isInfinite(f11));
 
        double dd= 0.0/0.0;
        System.out.println("DD value "+dd);
        double dd1= -1.0/0.0;
        System.out.println("DD value "+dd1);
        double dd2= 1.0/-0.0;
        System.out.println("DD value "+dd2);
        double dd3= -1.0/-0.0;
        System.out.println("DD value "+dd3);
        double dd4= 0.0/-0.0f;
        System.out.println("DD value "+dd4);
        float ff=1.0f/0.0f;
        System.out.println("DDFF value "+ff);
        float ff1=-1.0f/0.0f;
        System.out.println("DDFF value "+ff1);
        float ff2=1.0f/-0.0f;
        System.out.println("DDFF value "+ff2);
        float ff3=-0.0f/-0.0f;
        System.out.println("DDFF value "+ff3);
        
    }
}
