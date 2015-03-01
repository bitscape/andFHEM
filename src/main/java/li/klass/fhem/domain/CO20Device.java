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

import android.content.Context;

import java.util.List;

import li.klass.fhem.R;
import li.klass.fhem.domain.core.DeviceChart;
import li.klass.fhem.domain.core.DeviceFunctionality;
import li.klass.fhem.domain.core.FhemDevice;
import li.klass.fhem.domain.core.XmllistAttribute;
import li.klass.fhem.domain.genericview.ShowField;
import li.klass.fhem.resources.ResourceIdMapper;
import li.klass.fhem.service.graph.description.ChartSeriesDescription;
import li.klass.fhem.service.graph.description.SeriesType;

import static li.klass.fhem.util.ValueDescriptionUtil.appendPpm;
import static li.klass.fhem.util.ValueExtractUtil.extractLeadingInt;

public class CO20Device extends FhemDevice<CO20Device> {
    @ShowField(description = ResourceIdMapper.VOC, showInOverview = true)
    private String voc;

    @Override
    public DeviceFunctionality getDeviceGroup() {
        return DeviceFunctionality.WEATHER;
    }

    @XmllistAttribute("voc")
    public void setVoc(String text) {
        this.voc = appendPpm(extractLeadingInt(text));
    }

    public String getVoc() {
        return voc;
    }

    @Override
    protected void fillDeviceCharts(List<DeviceChart> chartSeries, Context context) {
        super.fillDeviceCharts(chartSeries, context);

        addDeviceChartIfNotNull(new DeviceChart(R.string.VOCGraph,
                new ChartSeriesDescription.Builder()
                        .withFileLogSpec("4:voc")
                        .withDbLogSpec("voc::int1")
                        .withSeriesType(SeriesType.VOC)
                        .withColumnName(R.string.VOC, context)
                        .withShowRegression(true)
                        .build()), voc);
    }
}
