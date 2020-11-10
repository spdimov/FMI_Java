package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartTrafficLight extends AbstractDevice {
    private static int number = 0;

    public SmartTrafficLight(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        id = id.concat(DeviceType.TRAFFIC_LIGHT.getShortName()).concat("-").concat(name).concat("-").concat(String.valueOf(number));
        number++;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.TRAFFIC_LIGHT;
    }
}
