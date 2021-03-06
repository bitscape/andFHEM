/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.domain;

import li.klass.fhem.domain.core.DeviceFunctionality;
import li.klass.fhem.domain.core.FhemDevice;
import li.klass.fhem.domain.core.XmllistAttribute;
import li.klass.fhem.domain.genericview.ShowField;
import li.klass.fhem.resources.ResourceIdMapper;
import li.klass.fhem.util.ValueDescriptionUtil;

public class RevoltDevice extends FhemDevice<RevoltDevice> {

    @ShowField(description = ResourceIdMapper.power, showInOverview = true)
    private String power;

    @ShowField(description = ResourceIdMapper.energy_power, showInOverview = true)
    private String energy;

    @ShowField(description = ResourceIdMapper.voltage)
    private String voltage;

    @ShowField(description = ResourceIdMapper.energy_frequency)
    private String frequency;

    @ShowField(description = ResourceIdMapper.energy_powerFactor)
    @XmllistAttribute("pf")
    private String powerFactor;

    @Override
    public DeviceFunctionality getDeviceGroup() {
        return DeviceFunctionality.USAGE;
    }

    public String getPower() {
        return power;
    }

    public String getEnergy() {
        return energy;
    }

    public String getVoltage() {
        return voltage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getPowerFactor() {
        return powerFactor;
    }

    @XmllistAttribute("power")
    public void setPower(String power) {
        this.power = ValueDescriptionUtil.appendW(power);
    }

    @XmllistAttribute("energy")
    public void setEnergy(String energy) {
        this.energy = ValueDescriptionUtil.appendKWh(energy);
    }

    @XmllistAttribute("voltage")
    public void setVoltage(String voltage) {
        this.voltage = ValueDescriptionUtil.appendV(voltage);
    }

    @XmllistAttribute("frequency")
    public void setFrequency(String frequency) {
        this.frequency = ValueDescriptionUtil.appendHz(frequency);
    }

}
