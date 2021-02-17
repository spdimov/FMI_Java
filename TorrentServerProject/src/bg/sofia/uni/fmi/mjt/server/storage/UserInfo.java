package bg.sofia.uni.fmi.mjt.server.storage;

public class UserInfo {

    String addressIP;
    String miniServerPort;

    public UserInfo(String addressIP, String miniServerPort) {
        this.addressIP = addressIP;
        this.miniServerPort = miniServerPort;
    }

    public String getAddressIP() {
        return addressIP;
    }

    public String getMiniServerPort() {
        return miniServerPort;
    }

    @Override
    public String toString() {
        return addressIP + ":" + miniServerPort;
    }
}
