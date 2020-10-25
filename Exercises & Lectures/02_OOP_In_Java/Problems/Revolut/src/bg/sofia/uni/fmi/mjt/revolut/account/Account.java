package bg.sofia.uni.fmi.mjt.revolut.account;

public abstract class Account {

    private final String IBAN;
    private double amount;


    public Account() {
        amount = 0;
        IBAN = "-";
    }

    public Account(String IBAN) {
        this(IBAN, 0);
    }

    public Account(String IBAN, double amount) {
        this.IBAN = IBAN;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        return this.IBAN == ((Account) obj).IBAN;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    public void makePayment(double paymentAmount) {
        amount -= paymentAmount;
    }

    public void addMoney(double amount) {
        this.amount += amount;
    }
}