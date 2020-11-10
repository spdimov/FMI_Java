package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.AbstractDevice;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.util.*;

public class SmartCityHub {

    private final Map<String, AbstractDevice> devices;

    public SmartCityHub() {
        devices = new LinkedHashMap<String, AbstractDevice>();
    }

    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException();
        }

        if (devices.containsValue(device)) {
            throw new DeviceAlreadyRegisteredException();
        }

        devices.put(device.getId(), (AbstractDevice) device);
    }

    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException();
        }

        if (!devices.containsValue(device)) {
            throw new DeviceNotFoundException();
        }

        devices.remove(device.getId());
    }

    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        if (!devices.containsKey(id)) {
            throw new DeviceNotFoundException();
        }

        return devices.get(id);
    }

    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }

        int devicesPerType = 0;

        Collection<AbstractDevice> deviceValues = devices.values();

        for (AbstractDevice device : deviceValues) {
            if (device.getType() == type) {
                devicesPerType++;
            }
        }

        return devicesPerType;
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        PowerConsumptionComparator cmp = new PowerConsumptionComparator();
        List<Map.Entry<String, AbstractDevice>> sortedDevices = new LinkedList<Map.Entry<String, AbstractDevice>>(devices.entrySet());
        Collections.sort(sortedDevices, cmp);

        Collection<String> topIds = new HashSet<String>();

        n = (n < sortedDevices.size() ? n : sortedDevices.size());

        for (int i = 0; i < n; i++) {
            topIds.add(sortedDevices.get(i).getKey());
        }

        return topIds;
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {

        if (n < 0) {
            throw new IllegalArgumentException();
        }
        Collection<SmartDevice> listOfDevices = new LinkedList<SmartDevice>(devices.values());

        for (int i = n; i < listOfDevices.size(); i++) {
            listOfDevices.remove(i);
        }

        return listOfDevices;
    }

}