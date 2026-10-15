package Banking;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BankService {
    private HashMap<Integer, BankAccount> accounts;
    private HashMap<String, User> users;
    private ArrayList<Transaction> transactions;

    private int nextAccountNumber = 1001;

    private static final String ACCOUNT_FILE = "accounts.dat";
    private static final String USER_FILE = "users.dat";
    private static final String TRANSACTION_FILE = "transactions.dat";

    @SuppressWarnings("unchecked")
    public BankService() {
        Object accountData = FileManager.load(ACCOUNT_FILE);
        Object userData = FileManager.load(USER_FILE);
        Object transactionData = FileManager.load(TRANSACTION_FILE);

        accounts = accountData instanceof HashMap
            ? (HashMap<Integer, BankAccount>) accountData
            : new HashMap<Integer, BankAccount>();

        users = userData instanceof HashMap
            ? (HashMap<String, User>) userData
            : new HashMap<String, User>();

        transactions = transactionData instanceof ArrayList
            ? (ArrayList<Transaction>) transactionData
            : new ArrayList<Transaction>();

        for (Integer id : accounts.keySet()) {
            if (id >= nextAccountNumber) {
                nextAccountNumber = id + 1;
            }
        }

        // Create a default administrator on first run.
        if (!users.containsKey("admin")) {
            users.put("admin",
                new User("admin", "admin123", "ADMIN"));
            saveUsers();
        }
    }

    private void saveAccounts() {
        FileManager.save(accounts, ACCOUNT_FILE);
    }

    private void saveUsers() {
        FileManager.save(users, USER_FILE);
    }

    private void saveTransactions() {
        FileManager.save(transactions, TRANSACTION_FILE);
    }

    private void record(String message) {
        transactions.add(new Transaction(message));
        saveTransactions();
    }

    public boolean registerUser(String username, String password) {
        username = username.trim();

        if (username.isEmpty() || password.length() < 6) {
            throw new IllegalArgumentException(
                "Username is required and password must have "
                + "at least 6 characters.");
        }

        if (users.containsKey(username)) {
            return false;
        }

        users.put(username, new User(username, password, "USER"));
        saveUsers();
        record("User registered: " + username);
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);

        if (user != null && user.authenticate(password)) {
            record("Login successful: " + username);
            return user;
        }

        record("Failed login attempt: " + username);
        return null;
    }

    public BankAccount createAccount(String name, String contact,
                                     String category, double balance) {
        if (name.trim().isEmpty() || contact.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Name and contact number are required.");
        }

        if (balance < 0 || !Double.isFinite(balance)) {
            throw new IllegalArgumentException(
                "Initial balance cannot be negative.");
        }

        if (!category.equalsIgnoreCase("SAVINGS")
                && !category.equalsIgnoreCase("CURRENT")) {
            throw new IllegalArgumentException(
                "Category must be SAVINGS or CURRENT.");
        }

        BankAccount account = new BankAccount(
            nextAccountNumber++, name.trim(), contact.trim(),
            category.toUpperCase(), balance);

        accounts.put(account.getAccountNumber(), account);
        saveAccounts();

        record("Account created: " + account.getAccountNumber());
        return account;
    }

    public BankAccount findAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean updateAccount(int id, String name,
                                String contact, String category) {
        BankAccount account = accounts.get(id);

        if (account == null) {
            return false;
        }

        if (name.trim().isEmpty() || contact.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Name and contact number are required.");
        }

        if (!category.equalsIgnoreCase("SAVINGS")
                && !category.equalsIgnoreCase("CURRENT")) {
            throw new IllegalArgumentException(
                "Category must be SAVINGS or CURRENT.");
        }

        account.setHolderName(name.trim());
        account.setContactNumber(contact.trim());
        account.setCategory(category.toUpperCase());

        saveAccounts();
        record("Account updated: " + id);
        return true;
    }

    public boolean deleteAccount(int id) {
        BankAccount account = accounts.get(id);

        if (account == null) {
            return false;
        }

        if (account.getBalance() != 0) {
            throw new IllegalStateException(
                "Withdraw or transfer the remaining balance first.");
        }

        accounts.remove(id);
        saveAccounts();
        record("Account deleted: " + id);
        return true;
    }

    public void deposit(int id, double amount) {
        BankAccount account = requireAccount(id);
        account.deposit(amount);
        saveAccounts();
        record("Deposit of " + amount + " to account " + id);
    }

    public void withdraw(int id, double amount) {
        BankAccount account = requireAccount(id);
        account.withdraw(amount);
        saveAccounts();
        record("Withdrawal of " + amount + " from account " + id);
    }

    public void transfer(int fromId, int toId, double amount) {
        if (fromId == toId) {
            throw new IllegalArgumentException(
                "Source and destination accounts must differ.");
        }

        BankAccount source = requireAccount(fromId);
        BankAccount destination = requireAccount(toId);

        // Validate before changing either account.
        if (amount <= 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero.");
        }

        if (!"ACTIVE".equals(source.getStatus())
                || !"ACTIVE".equals(destination.getStatus())) {
            throw new IllegalStateException(
                "Both accounts must be active.");
        }

        if (source.getBalance() < amount) {
            throw new IllegalArgumentException(
                "Insufficient balance.");
        }

        source.withdraw(amount);
        destination.deposit(amount);

        saveAccounts();
        record("Transfer of " + amount + " from " + fromId
            + " to " + toId);
    }

    public void changeStatus(int id, String status) {
        BankAccount account = requireAccount(id);
        status = status.toUpperCase();

        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException(
                "Status must be ACTIVE or INACTIVE.");
        }

        account.setStatus(status);
        saveAccounts();
        record("Account " + id + " status changed to " + status);
    }

    private BankAccount requireAccount(int id) {
        BankAccount account = accounts.get(id);

        if (account == null) {
            throw new IllegalArgumentException(
                "Account not found: " + id);
        }

        return account;
    }

    public void displayAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (BankAccount account : accounts.values()) {
            System.out.println("----------------------------");
            System.out.println(account);
        }
    }

    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    public void displayReport() {
        double totalBalance = 0;
        int activeCount = 0;

        for (BankAccount account : accounts.values()) {
            totalBalance += account.getBalance();

            if ("ACTIVE".equals(account.getStatus())) {
                activeCount++;
            }
        }

        System.out.println("\n========== BANK REPORT ==========");
        System.out.println("Total accounts: " + accounts.size());
        System.out.println("Active accounts: " + activeCount);
        System.out.printf("Total bank balance: %.2f%n", totalBalance);
        System.out.println("Total transactions: " + transactions.size());
        System.out.println("=================================");
    }

    public void displayUsers() {
        System.out.println("\nRegistered users:");

        for (Map.Entry<String, User> entry : users.entrySet()) {
            System.out.println("Username: " + entry.getKey()
                + ", Role: " + entry.getValue().getRole());
        }
    }
}