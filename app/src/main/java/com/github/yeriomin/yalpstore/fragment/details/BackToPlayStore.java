package com.github.yeriomin.yalpstore.fragment.details;

import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.PurchaseTask;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class BackToPlayStore extends Abstract {

    static private final String PLAY_STORE_PACKAGE_NAME = "com.android.vending";

    public BackToPlayStore(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (!isPlayStoreInstalled() || !app.isInPlayStore()) {
            return;
        }
        TextView toPlayStore = activity.findViewById(R.id.to_play_store);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(new UriOnClickListener(activity, PurchaseTask.URL_PURCHASE + app.getPackageName()));
    }

    private boolean isPlayStoreInstalled() {
        try {
            return null != activity.getPackageManager().getPackageInfo(PLAY_STORE_PACKAGE_NAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
