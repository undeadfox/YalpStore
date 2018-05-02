/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.task.playstore;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.R;

public class DownloadLinkTask extends DeliveryDataTask implements CloneableTask {

    @Override
    public CloneableTask clone() {
        DownloadLinkTask task = new DownloadLinkTask();
        task.setApp(app);
        task.setContext(context);
        return task;
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData result) {
        super.onPostExecute(result);
        if (null != deliveryData && !TextUtils.isEmpty(deliveryData.getDownloadUrl())) {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(deliveryData.getDownloadUrl());
            Toast.makeText(context.getApplicationContext(), R.string.about_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }
}
