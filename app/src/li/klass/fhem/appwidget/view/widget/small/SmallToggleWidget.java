/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2012, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLICLICENSE, as published by the Free Software Foundation.
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
 */

package li.klass.fhem.appwidget.view.widget.small;

import android.content.Context;
import android.widget.RemoteViews;
import li.klass.fhem.R;
import li.klass.fhem.appwidget.WidgetConfiguration;
import li.klass.fhem.appwidget.view.widget.medium.ToggleWidgetView;
import li.klass.fhem.domain.core.Device;

public class SmallToggleWidget extends ToggleWidgetView {
    @Override
    protected void fillWidgetView(Context context, RemoteViews view, Device<?> device, WidgetConfiguration widgetConfiguration) {
        super.fillWidgetView(context, view, device, widgetConfiguration);

        view.setTextViewText(R.id.toggleOff, device.getAliasOrName());
        view.setTextViewText(R.id.toggleOn, device.getAliasOrName());
    }

    @Override
    protected int getContentView() {
        return R.layout.appwidget_toggle_small;
    }

    @Override
    public boolean shouldSetDeviceName() {
        return false;
    }
}
