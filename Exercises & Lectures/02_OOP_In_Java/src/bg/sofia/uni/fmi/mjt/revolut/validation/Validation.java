package bg.sofia.uni.fmi.mjt.revolut.validation;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.PhysicalCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualOneTimeCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualPermanentCard;

import java.time.LocalDate;

public class Validation {


    public static boolean validateCard(Card[] cards, Card card, int pin) {

        for (Card c : cards) {
            if (c.equals(card)) {
                if (!c.isBlocked()) {
                    if (c.getExpirationDate().isAfter(LocalDate.now())) {
                        if (c.checkPin(pin)) {
                            if (card instanceof PhysicalCard) {
                                ((PhysicalCard) card).correctPin();
                            } else if (card instanceof VirtualOneTimeCard) {
                                card.block();
                            } else if (card instanceof VirtualPermanentCard) {
                                ((VirtualPermanentCard) card).correctPin();
                            }
                            return true;
                        } else {
                            if (card instanceof PhysicalCard) {
                                ((PhysicalCard) card).wrongPin();
                            } else if (card instanceof VirtualOneTimeCard) {
                                ((VirtualOneTimeCard) card).wrongPin();
                            } else if (card instanceof VirtualPermanentCard) {
                                ((VirtualPermanentCard) card).wrongPin();
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Account existingAccount(Account[] accounts, Account account) {
        for (Account acc : accounts) {
            if (acc.equals(account)) {
                return acc;
            }
        }
        return null;
    }

    public static Account validateAccount(Account[] accounts, double amount, String currency) {
        for (Account acc : accounts) {
            if (acc.getAmount() >= amount && acc.getCurrency().equals(currency)) {
                return acc;
            }
        }
        return null;
    }

    public static boolean validateURL(String shopURL) {
        return !shopURL.matches(".*\\.biz$");
    }

    public static Account validatePayment(Account[] accounts, Card[] cards, Card card, int pin, double amount, String currency) {

        if (validateCard(cards, card, pin)) {
            return validateAccount(accounts, amount, currency);
        }
        return null;
    }

    public static boolean validateTransfer(Account[] accounts, Account from, Account to, double amount) {
        if (from.getAmount() < amount) {
            return false;
        }
        if (existingAccount(accounts, from) != null && existingAccount(accounts, to) != null) {
            return !from.equals(to);
        }
        return false;
    }
}
