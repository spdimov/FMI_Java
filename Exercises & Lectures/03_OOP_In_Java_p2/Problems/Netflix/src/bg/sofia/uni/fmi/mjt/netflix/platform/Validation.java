package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

final public class Validation {

    private Validation() {
    }

    public static boolean isRegistered(Account account, Account[] accounts) {
        for (Account acc : accounts) {
            if (acc.username().equals(account.username())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContentPresent(String name, Streamable[] content) {
        for (Streamable c : content) {
            if (c.getTitle().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContentAvailable(Streamable content, Account acc, Streamable[] streamableContent) {
        long userAge = ChronoUnit.YEARS.between(acc.birthdayDate(), LocalDateTime.now());
        if (content.getRating() == PgRating.PG13 && userAge < 13) {
            return false;
        } else if (content.getRating() == PgRating.NC17 && userAge < 17) {
            return false;
        } else {
            return true;
        }
    }
}

