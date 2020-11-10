package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartCamera extends AbstractDevice {
    private static int number = 0;

    public SmartCamera(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        id = id.concat(DeviceType.CAMERA.getShortName()).concat("-").concat(name).concat("-").concat(String.valueOf(number));
        number++;
    }

    @Override
    public DeviceType getType() {
        return DeviceType.CAMERA;
    }
}
