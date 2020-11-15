package bg.sofia.uni.fmi.mjt.warehouse.exceptions;

public class CapacityExceededException extends Throwable {
    public CapacityExceededException(String msg) {
        super(msg);
    }
}
