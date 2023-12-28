package banking;

import java.util.Scanner;

import static banking.Utils.verifyCardNumber;

public class SystemProcessor {
    SQLManager sqlManager;
    private Account loggedInUser;
    private final Scanner scanner = new Scanner(System.in);
    private static final String BALANCE_PROMPT = "1. Balance";
    private static final String INCOME_PROMPT = "2. Add income";
    private static final String TRANSFER_PROMPT = "3. Do transfer";
    private static final String CLOSE_ACC_PROMPT = "4. Close account";
    private static final String CREATE_PROMPT = "1. Create an account";
    private static final String LOG_IN_PROMPT = "2. Log into account";
    private static final String LOG_OUT_PROMPT = "2. Log out";
    private static final String EXIT_PROMPT = "0. Exit";

    public SystemProcessor(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public void start() {
        while (true) {
            printMenu();
            boolean isLogged = loggedInUser != null;
            int userInput = scanner.nextInt();
            switch (userInput) {
                case 1:
                    if (isLogged) {
                        getAccountBalance();
                    } else {
                        createNewAccount();
                    }
                    break;
                case 2:
                    if (isLogged) {
                        addIncome();
                    } else {
                        scanner.nextLine();
                        logIn();
                    }
                    break;
                case 3:
                    if (isLogged) {
                        doTransfer();
                    }
                    break;
                case 4:
                    if (isLogged) {
                        closeAccount();
                    }
                    break;
                case 5:
                    if (isLogged) {
                        logOut();
                        System.out.println("You have successfully logged out!");
                    }
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
            }
        }

    }

    private void closeAccount() {
        sqlManager.closeAccount(loggedInUser.getCardNumber());
        loggedInUser = null;
        System.out.println("The account has been closed!");
    }

    private void doTransfer() {
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String selectedCardNumber = scanner.next();
        if(!verifyCardNumber(selectedCardNumber)){
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }
        if(selectedCardNumber.equals(loggedInUser.getCardNumber())){
            System.out.println("You can't transfer money to the same account!");
            return;
        }
        if(!sqlManager.findAccount(selectedCardNumber)){
            System.out.println("Such a card does not exist.");
            return;
        }
        System.out.println("Enter how much money you want to transfer:");
        String desiredTransfer = scanner.next();
        if(!sqlManager.transferFromBalance(loggedInUser,Integer.parseInt(desiredTransfer))){
            System.out.println("Not enough money!");
            return;
        }
        sqlManager.addToBalance(selectedCardNumber,Integer.parseInt(desiredTransfer));
        System.out.println("Success!");
    }

    private void addIncome() {
        System.out.println("Enter income:");
        String desiredIncome = scanner.next();
        sqlManager.addToBalance(loggedInUser.getCardNumber(), Integer.parseInt(desiredIncome));

    }

    private void getAccountBalance() {
        double balance = sqlManager.getAccountBalance(loggedInUser.getCardNumber(), loggedInUser.getPIN());
        if (balance != -1) {
            System.out.println("Balance: " + balance);
        }
    }

    private void logOut() {
        loggedInUser = null;
    }

    private void logIn() {
        System.out.println("Enter your card number:");
        String userCardInput = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String userPINInput = scanner.nextLine();

        if (sqlManager.correctAccountCredentials(userCardInput, userPINInput)) {
            System.out.println("You have successfully logged in!");
            loggedInUser = new Account(userCardInput, userPINInput);
        }
        System.out.println("Wrong card number or PIN!");

    }

    private void createNewAccount() {
        Account account = new Account(Utils.generateNewCardNumber(), Utils.generateNewPIN());
        sqlManager.insertAccount(account);
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(account.getPIN());
    }

    private void printMenu() {
        if (loggedInUser != null) {
            System.out.println(BALANCE_PROMPT);
            System.out.println(INCOME_PROMPT);
            System.out.println(TRANSFER_PROMPT);
            System.out.println(CLOSE_ACC_PROMPT);
            System.out.println(LOG_OUT_PROMPT);
        } else {
            System.out.println(CREATE_PROMPT);
            System.out.println(LOG_IN_PROMPT);
        }
        System.out.println(EXIT_PROMPT);
    }
}
