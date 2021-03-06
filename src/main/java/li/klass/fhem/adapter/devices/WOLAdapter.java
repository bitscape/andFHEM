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

package li.klass.fhem.adapter.devices;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import javax.inject.Inject;

import li.klass.fhem.R;
import li.klass.fhem.adapter.devices.core.GenericDeviceAdapterWithSwitchActionRow;
import li.klass.fhem.adapter.devices.core.UpdatingResultReceiver;
import li.klass.fhem.adapter.devices.genericui.DeviceDetailViewAction;
import li.klass.fhem.adapter.devices.genericui.DeviceDetailViewButtonAction;
import li.klass.fhem.adapter.uiservice.StateUiService;
import li.klass.fhem.constants.Actions;
import li.klass.fhem.constants.BundleExtraKeys;
import li.klass.fhem.domain.WOLDevice;
import li.klass.fhem.service.intent.DeviceIntentService;

public class WOLAdapter extends GenericDeviceAdapterWithSwitchActionRow<WOLDevice> {
    @Inject
    StateUiService stateUiService;

    public WOLAdapter() {
        super(WOLDevice.class);
    }

    @Override
    protected List<DeviceDetailViewAction<WOLDevice>> provideDetailActions() {
        List<DeviceDetailViewAction<WOLDevice>> detailActions = super.provideDetailActions();

        detailActions.add(new DeviceDetailViewButtonAction<WOLDevice>(R.string.wake) {
            @Override
            public void onButtonClick(Context context, WOLDevice device) {
                stateUiService.setState(device, "on", context);
            }
        });

        detailActions.add(new DeviceDetailViewButtonAction<WOLDevice>(R.string.shutdown) {
            @Override
            public void onButtonClick(Context context, WOLDevice device) {
                stateUiService.setState(device, "off", context);
            }

            @Override
            public boolean isVisible(WOLDevice device) {
                return device.getShutdownCommand() != null;
            }
        });
        detailActions.add(new DeviceDetailViewButtonAction<WOLDevice>(R.string.requestRefresh) {
            @Override
            public void onButtonClick(Context context, WOLDevice device) {
                context.startService(new Intent(Actions.DEVICE_REFRESH_STATE)
                        .setClass(context, DeviceIntentService.class)
                        .putExtra(BundleExtraKeys.DEVICE_NAME, device.getName())
                        .putExtra(BundleExtraKeys.RESULT_RECEIVER, new UpdatingResultReceiver(context)));
            }
        });

        return detailActions;
    }
}
