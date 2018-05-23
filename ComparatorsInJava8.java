package java8.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ComparatorsInJava8 {
    static List<Employee> employeeList = 
            Arrays.asList(new Employee("Tom Jones", 45), 
              new Employee("Harry Major", 35),
              new Employee("Harry Major", 25), 
              new Employee("Ethan Hardy", 65), 
              new Employee("Nancy Smith", 15),
              new Employee("Deborah Sprightly", 29));
    
    
    static List<Employee> employeeList1 = Arrays.asList(
            new Employee(null, 45),
            new Employee("Harry Major", 35),
            new Employee("Harry Major", 25),
            new Employee(null, 65),
            new Employee("Nancy Smith", 15),
            new Employee("Deborah Sprightly", 29));
    
    
        public static void main(String[] args) {
       /*   Comparator<Employee> empNameComparator = (Employee emp1, Employee emp2) -> {
            return (emp1.getName().compareTo(emp2.getName()));
          };
          Collections.sort(employeeList, empNameComparator);
          employeeList.forEach(System.out::println);*/
          
          System.out.println("employee comapring second approch using java 8");
          Comparator<Employee> comparatorObj=Comparator.comparing( emp -> emp.getName());
          Collections.sort(employeeList, comparatorObj);
          employeeList.forEach(System.out::println);
          System.out.println("new tested approch");
          Comparator empNameComparator2 = Comparator.comparing(Employee::getName);
          Collections.sort(employeeList, empNameComparator2);
          employeeList.forEach(System.out::println);
          System.out.println("third approch");
          Comparator<Employee> empNameComparator1 = Comparator.comparing(Employee::getName).thenComparing(Employee::getAge);
          Collections.sort(employeeList, empNameComparator1);
          employeeList.forEach(System.out::println);
          
          System.out.println("natural sorting order");
          List<String> empNames = employeeList.stream().map(Employee::getName).collect(Collectors.toList());
          empNames.sort(Comparator.naturalOrder());
          empNames.forEach(System.out::println);
          empNames.sort(Comparator.nullsFirst(String::compareTo));
          System.out.println("null first handling");
          
          Comparator<Employee> empNameComparator = Comparator.comparing(Employee::getName, Comparator.nullsFirst(String::compareTo));
          Collections.sort(employeeList1, empNameComparator);
          employeeList.forEach(System.out::println);
          
          System.out.println("reverse order");
          Comparator<Employee> empNameComparator3 = Comparator.comparing(Employee::getName).reversed();
          Collections.sort(employeeList, empNameComparator3);
          employeeList.forEach(System.out::println);
          
          Comparator<Employee> empNameComparator4 =  Comparator.comparing(Employee::getName,Comparator.nullsFirst(String::compareTo));
          Collections.sort(employeeList, empNameComparator4);
          
          //
          Double avgAge = employeeList
                  .stream()
                  .collect(Collectors.averagingInt(Employee::getAge));
               System.out.println("Average age using Collectors.averagingInt: " + avgAge);
           
              //Using Collectors.averagingLong()
             /* Double avgLeaves = employeeList
                  .stream()
                  .collect(Collectors.averagingLong(Employee::getLeaves));
              System.out.println("Average leaves using Collectors.averagingLong: " + avgLeaves);
           
              //Using Collectors.averagingDouble()
              Double avgSalary = employeeList
                  .stream()
                  .collect(Collectors.averagingDouble(Employee::getSalary));
              System.out.println("Average salary using Collectors.averagingDouble: " + avgSalary);*/
          
          
        }
        
       
}
