package Banking;
import java.io.Serializable;

public class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private int accountNumber;
    private String holderName;
    private String contactNumber;
    private String category;
    private String status;
    private double balance;

    public BankAccount(int accountNumber, String holderName,
                       String contactNumber, String category,
                       double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.contactNumber = contactNumber;
        this.category = category;
        this.balance = initialBalance;
        this.status = "ACTIVE";
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public double getBalance() {
        return balance;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void deposit(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero.");
        }
        if (!"ACTIVE".equals(status)) {
            throw new IllegalStateException(
                "Account is not active.");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero.");
        }
        if (!"ACTIVE".equals(status)) {
            throw new IllegalStateException(
                "Account is not active.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException(
                "Insufficient balance.");
        }
        balance -= amount;
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber
            + "\nHolder Name: " + holderName
            + "\nContact Number: " + contactNumber
            + "\nCategory: " + category
            + "\nStatus: " + status
            + "\nBalance: " + String.format("%.2f", balance);
    }
}