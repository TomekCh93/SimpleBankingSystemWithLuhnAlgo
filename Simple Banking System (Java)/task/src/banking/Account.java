package banking;

public class Account {
    private String cardNumber;
    private String PIN;
    private int balance = 0;


    public Account(String cardNum, String PIN) {
        this.cardNumber = cardNum;
        this.PIN = PIN;
    }


    public String getPIN() {
        return PIN;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getBalance() {
        return balance;
    }

}
