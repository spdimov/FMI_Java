package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.validation.Validation;

public class Revolut implements RevolutAPI {
    private final Account[] accounts;
    private final Card[] cards;

    public Revolut(Account[] accounts, Card[] cards) {
        this.accounts = new Account[accounts.length];
        this.cards = new Card[cards.length];
        System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);
        System.arraycopy(cards, 0, this.cards, 0, cards.length);
    }

    @Override
    public boolean pay(Card card, int pin, double amount, String currency) {
        if (!card.getType().equals("PHYSICAL")) {
            return false;
        }

        Account acc = Validation.validatePayment(accounts, cards, card, pin, amount, currency);
        if (acc != null) {
            acc.makePayment(amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if (!Validation.validateURL(shopURL)) {
            return false;
        }
        Account acc = Validation.validatePayment(accounts, cards, card, pin, amount, currency);
        if (acc != null) {
            acc.makePayment(amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean addMoney(Account account, double amount) {
        Account acc = Validation.existingAccount(accounts, account);
        if (acc == null) {
            return false;
        }
        acc.addMoney(amount);
        return true;
    }

    public boolean transferMoney(Account from, Account to, double amount) {
        if (Validation.validateTransfer(accounts, from, to, amount)) {
            if (from.getCurrency().equals(to.getCurrency())) {
                from.makePayment(amount);
                to.addMoney(amount);
            } else if (from.getCurrency().equals("BGN")) {
                from.makePayment(amount);
                to.addMoney(amount / 1.95583);
            } else {
                from.makePayment(amount);
                to.addMoney(amount * 1.95583);
            }
            return true;
        }
        return false;
    }

    public double getTotalAmount() {
        double totalAmount = 0;
        for (Account acc : accounts) {
            if (acc.getCurrency().equals("EUR")) {
                totalAmount += acc.getAmount() * 1.95583;
            } else {
                totalAmount += acc.getAmount();
            }
        }
        return totalAmount;
    }
}
