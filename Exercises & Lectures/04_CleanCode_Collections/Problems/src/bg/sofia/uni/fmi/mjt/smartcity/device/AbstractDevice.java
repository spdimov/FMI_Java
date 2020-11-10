package bg.sofia.uni.fmi.mjt.smartcity.device;

import java.time.LocalDateTime;

public abstract class AbstractDevice implements SmartDevice, Comparable<AbstractDevice> {
    String id = "";
    String name;
    double powerConsumption;
    LocalDateTime installationDateTime;

    AbstractDevice(String name, double powerConsumption, LocalDateTime installationDateTime) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public int compareTo(AbstractDevice o) {
        return this.id.equals(o.id) ? 1 : 0;
    }
}
