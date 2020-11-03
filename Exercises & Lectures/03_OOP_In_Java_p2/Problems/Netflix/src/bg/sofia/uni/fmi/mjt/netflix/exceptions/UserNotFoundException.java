package bg.sofia.uni.fmi.mjt.netflix.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(){
        super("This user is not present in the platform");
    }
}
