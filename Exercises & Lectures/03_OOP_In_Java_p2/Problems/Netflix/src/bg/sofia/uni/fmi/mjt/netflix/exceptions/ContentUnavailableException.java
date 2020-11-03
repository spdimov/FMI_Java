package bg.sofia.uni.fmi.mjt.netflix.exceptions;

public class ContentUnavailableException extends Exception {
    public ContentUnavailableException(){
        super("You are not allowed to view this content");
    }
}
