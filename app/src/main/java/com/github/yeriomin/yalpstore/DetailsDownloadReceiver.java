package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class DetailsDownloadReceiver extends DownloadReceiver {

    private WeakReference<DetailsActivity> activityRef = new WeakReference<>(null);
    private String packageName;

    public DetailsDownloadReceiver(DetailsActivity activity, String packageName) {
        activityRef = new WeakReference<>(activity);
        this.packageName = packageName;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DELTA_PATCHING_COMPLETE);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        DetailsActivity activity = activityRef.get();
        if (null == activity || !ContextUtil.isAlive(activity)) {
            return;
        }
        if (null == state) {
            if (actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
                cleanup();
            }
        }
    }

    @Override
    protected void process(Context context, Intent intent) {
        if (!state.getApp().getPackageName().equals(packageName)) {
            return;
        }
        if (actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE) && isDelta(state.getApp())) {
            return;
        }
        state.setFinished(downloadId);
        if (DownloadManagerFactory.get(context).success(downloadId) && !actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
            state.setSuccessful(downloadId);
        }
        if (!state.isEverythingFinished()) {
            return;
        }
        draw(context, state);
    }

    private void draw(Context context, DownloadState state) {
        cleanup();
        if (!state.isEverythingSuccessful()) {
            return;
        }
        Button buttonDownload = activityRef.get().findViewById(R.id.download);
        buttonDownload.setVisibility(View.GONE);
        Button buttonInstall = activityRef.get().findViewById(R.id.install);
        buttonInstall.setVisibility(View.VISIBLE);
        if (PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL)
            && !state.getTriggeredBy().equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
        ) {
            buttonInstall.setEnabled(false);
            buttonInstall.setText(R.string.details_installing);
        } else {
            buttonInstall.setEnabled(true);
            buttonInstall.setText(R.string.details_install);
        }
    }

    private void cleanup() {
        ProgressBar progressBar = activityRef.get().findViewById(R.id.download_progress);
        if (null != progressBar) {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(0);
        }
        ImageButton buttonCancel = activityRef.get().findViewById(R.id.cancel);
        if (null != buttonCancel) {
            buttonCancel.setVisibility(View.GONE);
        }
        Button buttonDownload = activityRef.get().findViewById(R.id.download);
        buttonDownload.setText(R.string.details_download);
        buttonDownload.setEnabled(true);
    }
}
