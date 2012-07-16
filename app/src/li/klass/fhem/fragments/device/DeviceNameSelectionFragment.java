/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 *  server.
 *
 *  Copyright (c) 2012, Matthias Klass or third-party contributors as
 *  indicated by the @author tags or express copyright attribution
 *  statements applied by the authors.  All third-party contributions are
 *  distributed under license by Red Hat Inc.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLICLICENSE, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 *  for more details.
 *
 *  You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 *  along with this distribution; if not, write to:
 *    Free Software Foundation, Inc.
 *    51 Franklin Street, Fifth Floor
 */

package li.klass.fhem.fragments.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import li.klass.fhem.R;
import li.klass.fhem.constants.Actions;
import li.klass.fhem.constants.BundleExtraKeys;
import li.klass.fhem.constants.ResultCodes;
import li.klass.fhem.domain.RoomDeviceList;
import li.klass.fhem.domain.core.Device;
import li.klass.fhem.domain.core.DeviceType;
import li.klass.fhem.util.DialogUtil;

public class DeviceNameSelectionFragment extends DeviceNameListFragment {
    private ResultReceiver resultReceiver;

    @SuppressWarnings("unused")
    public DeviceNameSelectionFragment(Bundle bundle) {
        super(bundle);
        resultReceiver = (ResultReceiver) bundle.getParcelable(BundleExtraKeys.RESULT_RECEIVER);
    }

    @SuppressWarnings("unused")
    public DeviceNameSelectionFragment() {
    }

    @Override
    protected void onDeviceNameClick(DeviceType parent, Device<?> child) {
        if (child == null) return;

        if (resultReceiver != null) {
            Bundle result = new Bundle();
            result.putSerializable(BundleExtraKeys.CLICKED_DEVICE, (Device<?>) child);
            resultReceiver.send(ResultCodes.SUCCESS, result);
        }

        Intent intent = new Intent(Actions.BACK);
        intent.putExtra(BundleExtraKeys.CLICKED_DEVICE, (Device<?>) child);
        getActivity().sendBroadcast(intent);
    }

    @Override
    protected void deviceListReceived(RoomDeviceList roomDeviceList) {
        super.deviceListReceived(roomDeviceList);
        if (roomDeviceList.isEmptyOrOnlyContainsDoNotShowDevices()) {
            DialogUtil.showAlertDialog(getActivity(), R.string.error, R.string.widget_devicelist_empty, new DialogUtil.AlertOnClickListener() {
                @Override
                public void onClick() {
                    getActivity().finish();
                }
            });
        }
    }
}
