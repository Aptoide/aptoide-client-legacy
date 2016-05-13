/**
 * EnumDownloadFailReason,		part of aptoide
 * Copyright (C) 2012  Duarte Silveira
 * duarte.silveira@caixamagica.pt
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.aptoide.amethyst.downloadmanager;

import android.content.Context;
import android.os.Build;

import com.aptoide.amethyst.R;


/**
 * EnumDownloadFailReason, typeSafes Downloads fail reasons when status equals FAIL in Aptoide
 *
 * @author dsilveira
 *
 */
public enum EnumDownloadFailReason {
    NO_REASON,
    TIMEOUT,
    IP_BLACKLISTED,
    CONNECTION_ERROR,
    NOT_FOUND,
    MD5_CHECK_FAILED,
    PAIDAPP_NOTFOUND,
    NO_FREE_SPACE,
    SD_ERROR;

    public static EnumDownloadFailReason reverseOrdinal(int ordinal) {
        return values()[ordinal];
    }

    public String toString(Context context) {
        switch (this) {
            case TIMEOUT:
                return context.getString(R.string.timeout);
            case IP_BLACKLISTED:
                return context.getString(R.string.ip_blacklisted);
            case CONNECTION_ERROR:
                return context.getString(R.string.connection_error);
            case NOT_FOUND:
                return context.getString(R.string.apk_not_found);
            case MD5_CHECK_FAILED:
                return context.getString(R.string.invalid_apk);
            case PAIDAPP_NOTFOUND:
                return context.getString(R.string.paidapp_not_found);
            case NO_FREE_SPACE:
                return context.getString(R.string.remote_in_nospace);
            case SD_ERROR:
                return (!Build.DEVICE.equals("alien_jolla_bionic")) ?
                        context.getString(R.string.sd_error) :
                        context.getString(R.string.sd_error_jolla);
            default:
                return context.getString(R.string.server_error);
        }
    }
}
