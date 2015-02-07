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

package li.klass.fhem.ui;

import android.content.Context;
import android.os.Build;

import li.klass.fhem.R;
import li.klass.fhem.util.DialogUtil;

public class AndroidBug {
    /**
     * Android contains a bug causing an {@link java.lang.ArrayIndexOutOfBoundsException} when
     * inflating {@link android.widget.TimePicker}s.
     * <p/>
     * <code>
     * android.view.InflateException: Binary XML file line #47: Error inflating class android.widget.TimePicker
     * at android.view.LayoutInflater.createView(LayoutInflater.java:633)
     * ...
     * Caused by: java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
     * at android.content.res.ColorStateList.addFirstIfMissing(ColorStateList.java:356)
     * </code>
     *
     * Current status:
     * Google has acknowledged the bug but has not yet released a fix (see <a href="https://code.google.com/p/android/issues/detail?id=78984">Google Code</a>)
     *
     * @return true if the bug was handled
     */
    public static boolean handleColorStateBugIfRequired(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DialogUtil.showAlertDialog(context, R.string.androidBugDialogDatePickerTitle, R.string.androidBugDialogDatePickerContent);
            return true;
        }
        return false;
    }
}