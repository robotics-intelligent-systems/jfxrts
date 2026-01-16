// MainClassEMainClassExamplele.java
import java.util.Scanner;

public class MainClassExample {

    // The main method is the entry point of any Java application
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Java Main Class Example ===");

        try {
            // Prompt user for their name
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            // Validate input
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }

            // Prompt user for their age
            System.out.print("Enter your age: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Age must be a number.");
                return;
            }
            int age = scanner.nextInt();

            // Validate age
            if (age < 0) {
                System.out.println("Age cannot be negative.");
                return;
            }

            // Output result
            System.out.println("Hello, " + name + "! You are " + age + " years old.");

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            scanner.close(); // Always close resources
        }
    }
}
