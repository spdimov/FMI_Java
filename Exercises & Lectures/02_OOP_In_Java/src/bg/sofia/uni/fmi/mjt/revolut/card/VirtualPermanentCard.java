package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualPermanentCard implements Card {
    private final String number;
    private final int pin;
    private final LocalDate expirationDate;
    private boolean isBlocked;
    private int incorrectPin;

    public VirtualPermanentCard() {
        number = "-";
        pin = -1;
        expirationDate = LocalDate.now();
        isBlocked = true;
        incorrectPin = 0;
    }

    public VirtualPermanentCard(String number, int pin, LocalDate expirationDate) {
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
        isBlocked = false;
        incorrectPin = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof VirtualPermanentCard)) {
            return false;
        }

        return this.number.equals((((VirtualPermanentCard) obj).number));
    }

    @Override
    public String getType() {
        return "VIRTUALPERMANENT";
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        return this.pin == pin;
    }

    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public void block() {
        isBlocked = true;
    }

    public void wrongPin() {
        incorrectPin++;
        if (incorrectPin == 3) {
            this.block();
        }
    }

    public void correctPin() {
        incorrectPin = 0;
    }
}
