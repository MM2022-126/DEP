import java.util.Scanner;

// Method to Calculate Factorial
public class Factorial_Using_Loops {

    static long factorial(int num) {
        long fact = 1;
// Loop to Calculate the Factorial       
        for (int i = 1; i <= num; i++) {
            fact *= i;  // Multiplying the fact with i in each iteration
        }
        return fact; // Return the calculated factorial
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
// Infinite loop to continue prompt the user for input        
        while (true) {
            System.out.println("Enter the Number (-1 to stop) =");
            int num = sc.nextInt(); // read the user input number
            
            // Checking if the input number is -1 So exist the loop
            if (num == -1) {
                break; // Exist from the loop
            }

            // Calculate factorial and print the result
            long result = factorial(num);
            System.out.println("Factorial of " + num + " is => " + result);
        }
        
        sc.close(); // Close the scanner to release source
    }
}
