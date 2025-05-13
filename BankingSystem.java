import java.util.ArrayList;
import java.util.Scanner;

// Exception classes for specific errors
class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// User class
class User {
    private String name;
    private String accountNumber;
    private String encodedPassword;
    private double balance;
    private ArrayList<String> transactionHistory;

    public User(String name, String accountNumber, String password) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.encodedPassword = encodePassword(password);
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    // Caesar Cipher encryption for password
    private String encodePassword(String password) {
        StringBuilder encoded = new StringBuilder();
        for (char c : password.toCharArray()) {
            encoded.append((char) (c + 3)); // Shift each character by 3
        }
        return encoded.toString();
    }

    private String decodePassword(String encodedPassword) {
        StringBuilder decoded = new StringBuilder();
        for (char c : encodedPassword.toCharArray()) {
            decoded.append((char) (c - 3)); // Reverse shift each character by 3
        }
        return decoded.toString();
    }

    public boolean authenticate(String password) {
        return this.encodedPassword.equals(encodePassword(password));
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: INR " + amount);
    }
    
    public void depositByTransfer(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }
        balance -= amount;
        transactionHistory.add("Withdrew: INR " + amount);
    }

    public void withdrawByTransfer(double amount) {
        balance -= amount;
    }

    public void transfer(User recipient, double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for transfer.");
        }
        this.withdrawByTransfer(amount);
        recipient.depositByTransfer(amount);
        transactionHistory.add("Transferred INR " + amount + " to " + recipient.getAccountNumber());
        recipient.transactionHistory.add("Received INR " + amount + " from " + this.accountNumber);
    }

    public void viewTransactionHistory() {
        System.out.println("Transaction History for " + accountNumber + ":");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }
}

// Banking System class
class BankingSystem {
    private ArrayList<User> users;
    private Scanner sc;

    public BankingSystem() {
        users = new ArrayList<>();
        sc = new Scanner(System.in);
    }

    public User findUser(String accountNumber) {
        for (User user : users) {
            if (user.getAccountNumber().equals(accountNumber)) {
                return user;
            }
        }
        return null;
    }

    public void registerUser() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter a new account number (11 digits): ");
        String accountNumber = sc.nextLine();
	
	// Validating Account Number
	if (accountNumber.length() != 11) {
	    System.out.println("Invalid Account Number ! Please try again.");
	    return;
	}

        System.out.print("Set a password (8 digits): ");
        String password = sc.nextLine();

	// Validating Password
	if (password.length() != 8) {
	    System.out.println("Invalid Password ! Please try again.");
	    return;
	}

        User user = new User(name, accountNumber, password);
        users.add(user);
        System.out.println("Account created successfully!");
    }

    public User login() throws AuthenticationException {
        System.out.print("Enter your account number: ");
        String accountNumber = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();

        User user = findUser(accountNumber);
        if (user != null && user.authenticate(password)) {
            System.out.println("Login successful!");
            return user;
        } else {
            throw new AuthenticationException("Invalid account number or password.");
        }
    }

    public void deposit(User user) {
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // Consume newline
        user.deposit(amount);
        System.out.println("Deposit successful! Current balance: INR " + user.getBalance());
    }

    public void withdraw(User user) {
        try {
            System.out.print("Enter amount to withdraw: ");
            double amount = sc.nextDouble();
            sc.nextLine(); // To consume newline
            user.withdraw(amount);
            System.out.println("Withdrawal successful! Current balance: INR " + user.getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transfer(User user) {
        System.out.print("Enter recipient account number: ");
        String recipientAccount = sc.nextLine();
        User recipient = findUser(recipientAccount);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        try {
            System.out.print("Enter amount to transfer: ");
            double amount = sc.nextDouble();
            sc.nextLine(); // To consume newline
            user.transfer(recipient, amount);
            System.out.println("Transfer successful! Current balance: INR " + user.getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewTransactionHistory(User user) {
        user.viewTransactionHistory();
    }

    public void checkBalance(User user) {
        System.out.println("Current balance: INR " + user.getBalance());
    }

    public void startBanking() {
        while (true) {
            System.out.println("\nWelcome to the Online Banking System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = sc.nextInt();
            sc.nextLine(); // To consume newline

            switch (option) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    try {
                        User user = login();
                        userMenu(user);
                    } catch (AuthenticationException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using the Online Banking System. Have a nice day!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void userMenu(User user) {
        while (true) {
            System.out.println("\nUser Menu");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Check Balance");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");
            int option = sc.nextInt();
            sc.nextLine(); // To consume newline

            switch (option) {
                case 1:
                    deposit(user);
                    break;
                case 2:
                    withdraw(user);
                    break;
                case 3:
                    transfer(user);
                    break;
                case 4:
                    checkBalance(user);
                    break;
                case 5:
                    viewTransactionHistory(user);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        BankingSystem ob = new BankingSystem();
        ob.startBanking();
    }
}
