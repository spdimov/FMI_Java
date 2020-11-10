package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartLamp extends AbstractDevice {
    private static int number = 0;

    public SmartLamp(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        id = id.concat(DeviceType.LAMP.getShortName()).concat("-").concat(name).concat("-").concat(String.valueOf(number));
        number++;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.LAMP;
    }
}
