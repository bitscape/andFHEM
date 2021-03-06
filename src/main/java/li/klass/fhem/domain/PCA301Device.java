package li.klass.fhem.domain;

import li.klass.fhem.domain.core.DeviceFunctionality;
import li.klass.fhem.domain.core.ToggleableDevice;
import li.klass.fhem.domain.core.XmllistAttribute;
import li.klass.fhem.domain.genericview.ShowField;
import li.klass.fhem.resources.ResourceIdMapper;
import li.klass.fhem.util.ValueDescriptionUtil;

public class PCA301Device extends ToggleableDevice<PCA301Device> {

    @ShowField(description = ResourceIdMapper.energy_consumption)
    private String consumption;
    @ShowField(description = ResourceIdMapper.energy_power)
    private String power;

    @XmllistAttribute("CONSUMPTION")
    public void readCONSUMPTION(String value) {
        consumption = ValueDescriptionUtil.append(value, "kWh");
    }

    @Override
    public String formatTargetState(String targetState) {
        return super.formatTargetState(targetState).replace("set-", "");
    }

    @XmllistAttribute("POWER")
    public void readPOWER(String value) {
        power = ValueDescriptionUtil.append(value, "W");
    }

    public String getConsumption() {
        return consumption;
    }

    public String getPower() {
        return power;
    }

    @Override
    public DeviceFunctionality getDeviceGroup() {
        return DeviceFunctionality.SWITCH;
    }

    @Override
    public boolean isSensorDevice() {
        return true;
    }

    @Override
    public long getTimeRequiredForStateError() {
        return OUTDATED_DATA_MS_DEFAULT;
    }
}
