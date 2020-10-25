Обектно-ориентирано програмиране с Java (част I)
Revolut 💳
Ще създадем приложение за банкови услуги, подобно на Revolut, което ще менажира сметки, карти и разплащания. Приложението ще има следните основни функционалности:

менажиране на сметки в лева и евро
менажиране на банкови карти
добаване и трансфериране на пари между сметки
плащания
Revolut
В пакета bg.sofia.uni.fmi.mjt.revolut създайте клас Revolut с конструктор Revolut(Account[] accounts, Card[] cards), който имплементира следния интерфейс:

package bg.sofia.uni.fmi.mjt.revolut;

public interface RevolutAPI {

    /**
     * Executes a card payment using a POS terminal
     *
     * @param card the card used for the payment. Only physical cards are accepted
     * @param pin 4-digit PIN
     * @param amount the amount paid
     * @param currency the currency of the payment ("BGN" or "EUR")
     * @return true, if the operation is successful and false otherwise.
     *         Payment is successful, if the card is available in Revolut, valid, unblocked,
     *         the specified PIN is correct and an account with sufficient amount in the specified currency exists.
     *         In case of three consecutive incorrect PIN payment attempts, the card should be blocked.
     **/
    boolean pay(Card card, int pin, double amount, String currency);

    /**
     * Executes an online card payment
     *
     * @param card the card used for the payment. Any type of card is accepted
     * @param pin 4-digit PIN
     * @param amount the amount paid
     * @param currency the currency of the payment ("BGN" or "EUR")
     * @param shopURL the shop's domain name. ".biz" top level domains are currently banned and payments should be rejected
     * @return true, if the operation is successful and false otherwise.
     *         Payment is successful, if the card is available in Revolut, valid, unblocked,
     *         the specified PIN is correct and an account with sufficient amount in the specified currency exists.
     *         In case of three consecutive incorrect PIN payment attempts, the card should be blocked.
     **/
    boolean payOnline(Card card, int pin, double amount, String currency, String shopURL);

    /**
     * Adds money to a Revolut account
     *
     * @param account the account to debit
     * @param amount the amount to add to the account, in the @account's currency
     * @return true, if the acount exists in Revolut and false otherwise
     **/
    boolean addMoney(Account account, double amount);

    /**
     * Transfers money between accounts, doing currency conversion, if needed.
     * The official fixed EUR to BGN exchange rate is 1.95583.
     *
     * @param from the account to credit
     * @param to the account to debit
     * @param amount the amount to transfer, in the @from account's currency
     * @return true if both accounts exist and are different (with different IBANs) and false otherwise
     **/
    boolean transferMoney(Account from, Account to, double amount);

    /**
     * Returns the total available amount
     *
     * @return The total available amount (the sum of amounts for all accounts), in BGN
     **/
    double getTotalAmount();

}
Карти
Revolut поддържа три типа карти - физически, виртуални и виртуални за еднократна употреба

физическите карти могат да се ползват за всякакъв вид плащания (online и с POS терминал)
виртуалните карти могат се ползват само за онлайн плащания
виртуалната карта за еднократна употреба е валидна за едно-единствено плащане, след което се блокира.
Трите типа карти са реализирани съответно от конкретните класове PhysicalCard, VirtualPermanentCard и VirtualOneTimeCard, имплементиращи интерфейса Card:

package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public interface Card {

    /**
     * @return the type of the card: "PHYSICAL", "VIRTUALPERMANENT" or "VIRTUALONETIME"
     **/
    String getType();

    /**
     * @return the card's expiration date
     **/
    LocalDate getExpirationDate();

    /**
     * @return true if the PIN is correct and false otherwise. Correct means, equal to the PIN, set in the card upon construction (i.e. passed in its constructor). You can check it for validity, e.g. that it is a 4-digit number, but you can assume the input is valid.
     **/
    boolean checkPin(int pin);

    /**
     * @return true if the card is blocked and false otherwise
     **/
    boolean isBlocked();

    /**
     * Blocks the card
     **/
    void block();

}
И трите имплементации на Card трябва да имат публичен конструктор с параметри (String number, int pin, LocalDate expirationDate)

Подсказка
👉 За работа с дати, може да използвате java.time API, обръщайки по-специално внимание на LocalDate класа и неговите методи.

Сметки
Поддържат се сметки в лева и евро, имплементирани съответно от конкретните класове BGNAccount и EURAccount, наследяващи абстрактния клас Account:

package bg.sofia.uni.fmi.mjt.revolut.account;

public abstract class Account {

    private double amount;
    private String IBAN;

    public Account(String IBAN) {
        this(IBAN, 0);
    }

    public Account(String IBAN, double amount) {
        this.IBAN = IBAN;
        this.amount = amount;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    // complete the implementation

}
В класическите банки, всяка карта е свързана с конкретна сметка. В Revolut обаче, и картите, и сметките, са свързани със самия Revolut, а не директно една с друга. Когато осъществяваме плащане, валидираме подадената карта, и при успешна валидация, обхождаме сметките и търсим такава в исканата валута и с необходимата минимална наличност.

Пакети
Спазвайте имената на пакетите на всички по-горе описани класове и интерфейси, тъй като в противен случай, решението ви няма да може да бъде тествано от грейдъра.

src
╷
└─ bg/sofia/uni/fmi/mjt/revolut
   └─ account/
      └─ Account.java
      └─ BGNAccount.java
      └─ EURAccount.java
      └─ (...)
   └─ card/
      └─ Card.java
      └─ PhysicalCard.java
      └─ VirtualOneTimeCard.java
      └─ VirtualPermanentCard.java
      └─ (...)
   ├─ Revolut.java
   ├─ RevolutAPI.java
   └─ (...)
Забележки
За да тествате решението си в grader.sapera.org, архивирайте в zip цялата src папка на проекта.