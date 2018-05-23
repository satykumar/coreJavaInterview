package remove.dulpilcate.list;

import java.util.Scanner;
import java.util.Stack;

public class ConvertDecimalToBinary {

    public static void main(String[] args) {
        
        Scanner in = new Scanner(System.in);
                 
        // Create Stack object
        Stack<Integer> stack = new Stack<Integer>();
             
        //Take  User input from keyboard
         System.out.println("Enter decimal number: ");
         int num = in.nextInt();
            
        while (num != 0)
        {
           int d = num % 2;
           stack.push(d);
           num /= 2;
         
         } 
             
         System.out.print("\nBinary representation is:");
         
        while (!(stack.isEmpty() ))
         {
           System.out.print(stack.pop());
         }
                
         
        System.out.println();
         
    }
    }

