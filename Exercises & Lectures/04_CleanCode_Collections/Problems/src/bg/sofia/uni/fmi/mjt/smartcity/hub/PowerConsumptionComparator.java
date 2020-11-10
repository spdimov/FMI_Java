package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.AbstractDevice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;

public class PowerConsumptionComparator implements Comparator<Map.Entry<String, AbstractDevice>> {

    @Override
    public int compare(Map.Entry<String, AbstractDevice> o1, Map.Entry<String, AbstractDevice> o2) {
        long firstDeviceDuration = Duration.between(o1.getValue().getInstallationDateTime(), LocalDateTime.now()).toHours();
        long secondDeviceDuration = Duration.between(o2.getValue().getInstallationDateTime(), LocalDateTime.now()).toHours();

        return (int) (o1.getValue().getPowerConsumption() * firstDeviceDuration - o2.getValue().getPowerConsumption() * secondDeviceDuration);
    }
}
