package li.klass.fhem.domain;

import li.klass.fhem.data.FHEMService;

public class SISPMSDevice extends Device<SISPMSDevice> {
    @Override
    public void onChildItemRead(String keyValue, String nodeContent) {
    }

    public boolean isOn() {
        return state.equalsIgnoreCase("on");
    }

    public void toggleState() {
        if (isOn()) {
            state = "off";
            FHEMService.INSTANCE.executeCommand("set " + getName() + " on");
        } else {
            state = "on";
            FHEMService.INSTANCE.executeCommand("set " + getName() + " off");
        }
    }
}