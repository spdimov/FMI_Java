package bg.sofia.uni.fmi.mjt.client;

public class UserInfo {
    String addressIP;
    String miniServerPort;

    public UserInfo(String addressIP, String miniServerPort) {
        this.addressIP = addressIP;
        this.miniServerPort = miniServerPort;
    }

    @Override
    public String toString() {
        return addressIP + ":" + miniServerPort;
    }
}
