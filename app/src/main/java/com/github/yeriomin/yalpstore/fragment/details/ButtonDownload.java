package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.OnDownloadProgressListener;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PurchaseTask;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SelfUpdateChecker;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;

import java.io.File;

import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON;

public class ButtonDownload extends Button {

    private ImageButton cancelButton;

    public ButtonDownload(final DetailsActivity activity, final App app) {
        super(activity, app);
        cancelButton = activity.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCancel = new Intent(activity.getApplicationContext(), CancelDownloadService.class);
                intentCancel.putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName());
                activity.startService(intentCancel);
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.download);
    }

    @Override
    protected boolean shouldBeVisible() {
        return (!Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
                || !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
            )
            && (app.isInPlayStore() || app.getPackageName().equals(BuildConfig.APPLICATION_ID))
            && (getInstalledVersionCode() != app.getVersionCode() || activity instanceof ManualDownloadActivity)
        ;
    }

    @Override
    protected void onButtonClick(View v) {
        if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
            activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
        } else if (activity.checkPermission()) {
            download();
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            activity.requestPermission();
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
            && !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
        ) {
            disableButton(R.id.download, R.string.details_downloading);
        }
    }

    public void download() {
        if (app.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            new Downloader(button.getContext()).download(
                app,
                AndroidAppDeliveryData.newBuilder().setDownloadUrl(SelfUpdateChecker.getUrlString(app.getVersionCode())).build(),
                getDownloadProgressListener()
            );
        } else if (prepareDownloadsDir()) {
            getPurchaseTask().execute();
        } else {
            ContextUtil.toast(this.activity.getApplicationContext(), R.string.error_downloads_directory_not_writable);
        }
    }

    private boolean prepareDownloadsDir() {
        File dir = Paths.getYalpPath(activity);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    private OnDownloadProgressListener getDownloadProgressListener() {
        ProgressBar progressBar = activity.findViewById(R.id.download_progress);
        progressBar.setVisibility(View.VISIBLE);
        return new OnDownloadProgressListener(progressBar, DownloadState.get(app.getPackageName()));
    }

    private PurchaseTask getPurchaseTask() {
        PurchaseTask purchaseTask = new LocalPurchaseTask(this);
        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.download_progress);
        progressBar.setVisibility(View.VISIBLE);
        purchaseTask.setOnDownloadProgressListener(new OnDownloadProgressListener(progressBar, DownloadState.get(app.getPackageName())));
        purchaseTask.setApp(app);
        purchaseTask.setContext(activity);
        purchaseTask.setTriggeredBy(activity instanceof ManualDownloadActivity ? MANUAL_DOWNLOAD_BUTTON : DOWNLOAD_BUTTON);
        purchaseTask.prepareDialog(
            R.string.dialog_message_purchasing_app,
            R.string.dialog_title_purchasing_app
        );
        return purchaseTask;
    }

    private int getInstalledVersionCode() {
        try {
            return activity.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    static class LocalPurchaseTask extends PurchaseTask {

        private ButtonDownload fragment;

        public LocalPurchaseTask(ButtonDownload fragment) {
            this.fragment = fragment;
        }

        @Override
        protected void onPostExecute(Throwable e) {
            super.onPostExecute(e);
            if (null == e) {
                fragment.disableButton(R.id.download, R.string.details_downloading);
            }
        }
    }
}
