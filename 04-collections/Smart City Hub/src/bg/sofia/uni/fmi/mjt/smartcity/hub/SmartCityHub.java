package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SmartCityHub {

    private Map<String, SmartDevice> smartDevices;

    public SmartCityHub() {
        smartDevices = new LinkedHashMap<>();
    }

    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        validate(device);

        String id = device.getId();
        if (smartDevices.containsKey(id)) {
            throw new DeviceAlreadyRegisteredException(String.format("Device %s already exists", id));
        }
        smartDevices.put(id, device);
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        validate(device);

        if (smartDevices.remove(device.getId()) == null) {
            throw new DeviceNotFoundException("Device does not exist");
        }
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        SmartDevice device = smartDevices.get(id);
        if (device == null) {
            throw new DeviceNotFoundException(String.format("Device %s not found", id));
        }
        return device;
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return Math.toIntExact(smartDevices.values()
                .stream()
                .filter(smartDevice -> type.equals(smartDevice.getType()))
                .count());
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s multiplied by the stated power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        return smartDevices.values()
                .stream()
                .sorted(Comparator.comparing(
                        (SmartDevice s) -> s.getPowerConsumption() * Duration.between(s.getInstallationDateTime(), LocalDateTime.now()).toHours())
                        .reversed())
                .limit(n)
                .map(SmartDevice::getId)
                .collect(Collectors.toCollection(ArrayList::new));
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

        return smartDevices.values()
                .stream()
                .limit(n)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void validate(SmartDevice smartDevice) {
        if (smartDevice == null) {
            throw new IllegalArgumentException();
        }
    }
}