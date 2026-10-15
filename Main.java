package Banking;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final BankService bank = new BankService();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("   BANKING MANAGEMENT SYSTEM");
        System.out.println("====================================");

        while (true) {
            System.out.println("\n1. User Registration");
            System.out.println("2. User Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        register();
                        break;
                    case 2:
                        loginUser();
                        break;
                    case 3:
                        loginAdmin();
                        break;
                    case 4:
                        System.out.println("Thank you for using our system.");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());

                if (!Double.isFinite(value)) {
                    System.out.println("Enter a finite number.");
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private static String readText(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private static void register() {
        String username = readText("Enter username: ");
        String password = readText("Enter password (min 6 chars): ");

        if (bank.registerUser(username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Username already exists.");
        }
    }

    private static void loginUser() {
        String username = readText("Username: ");
        String password = readText("Password: ");

        if (bank.login(username, password) == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        System.out.println("Login successful!");
        userMenu();
    }

    private static void loginAdmin() {
        String username = readText("Admin username: ");
        String password = readText("Admin password: ");

        if (bank.login(username, password) == null
                || !username.equals("admin")) {
            System.out.println("Invalid admin credentials.");
            return;
        }

        System.out.println("Admin login successful!");
        adminMenu();
    }

    private static void userMenu() {
        while (true) {
            System.out.println("\n========== USER MENU ==========");
            System.out.println("1. Create Bank Account");
            System.out.println("2. View Account");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Transfer Money");
            System.out.println("6. Update Account Details");
            System.out.println("7. View All Accounts");
            System.out.println("8. View Reports");
            System.out.println("9. View Transaction History");
            System.out.println("10. Logout");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        viewAccount();
                        break;
                    case 3:
                        deposit();
                        break;
                    case 4:
                        withdraw();
                        break;
                    case 5:
                        transfer();
                        break;
                    case 6:
                        updateAccount();
                        break;
                    case 7:
                        bank.displayAllAccounts();
                        break;
                    case 8:
                        bank.displayReport();
                        break;
                    case 9:
                        bank.displayTransactions();
                        break;
                    case 10:
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. Create Bank Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Search Account");
            System.out.println("4. Update Account");
            System.out.println("5. Delete Account");
            System.out.println("6. Change Account Status");
            System.out.println("7. View Bank Report");
            System.out.println("8. View Transaction History");
            System.out.println("9. View Registered Users");
            System.out.println("10. Logout");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        bank.displayAllAccounts();
                        break;
                    case 3:
                        viewAccount();
                        break;
                    case 4:
                        updateAccount();
                        break;
                    case 5:
                        deleteAccount();
                        break;
                    case 6:
                        changeStatus();
                        break;
                    case 7:
                        bank.displayReport();
                        break;
                    case 8:
                        bank.displayTransactions();
                        break;
                    case 9:
                        bank.displayUsers();
                        break;
                    case 10:
                        System.out.println("Admin logged out.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException
                     | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void createAccount() {
        String name = readText("Customer name: ");
        String contact = readText("Contact number: ");
        String category = readText("Category (SAVINGS/CURRENT): ");
        double balance = readDouble("Initial deposit: ");

        BankAccount account = bank.createAccount(
            name, contact, category, balance);

        System.out.println("Account created successfully!");
        System.out.println(account);
    }

    private static void viewAccount() {
        int id = readInt("Enter account number: ");
        BankAccount account = bank.findAccount(id);

        if (account == null) {
            System.out.println("Account not found.");
        } else {
            System.out.println(account);
        }
    }

    private static void deposit() {
        int id = readInt("Account number: ");
        double amount = readDouble("Deposit amount: ");

        bank.deposit(id, amount);
        System.out.println("Deposit successful!");
    }

    private static void withdraw() {
        int id = readInt("Account number: ");
        double amount = readDouble("Withdrawal amount: ");

        bank.withdraw(id, amount);
        System.out.println("Withdrawal successful!");
    }

    private static void transfer() {
        int from = readInt("Source account number: ");
        int to = readInt("Destination account number: ");
        double amount = readDouble("Transfer amount: ");

        bank.transfer(from, to, amount);
        System.out.println("Transfer successful!");
    }

    private static void updateAccount() {
        int id = readInt("Account number: ");
        String name = readText("New customer name: ");
        String contact = readText("New contact number: ");
        String category = readText("New category (SAVINGS/CURRENT): ");

        if (bank.updateAccount(id, name, contact, category)) {
            System.out.println("Account updated successfully!");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void deleteAccount() {
        int id = readInt("Account number to delete: ");
        String confirmation = readText(
            "Confirm deletion (yes/no): ");

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        if (bank.deleteAccount(id)) {
            System.out.println("Account deleted successfully!");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void changeStatus() {
        int id = readInt("Account number: ");
        String status = readText("Enter status (ACTIVE/INACTIVE): ");

        bank.changeStatus(id, status);
        System.out.println("Account status updated.");
    }
}