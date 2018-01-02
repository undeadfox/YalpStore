package com.github.yeriomin.yalpstore;

import android.content.Context;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.InstallTask;

public class InstallerRoot extends InstallerBackground {

    public InstallerRoot(Context context) {
        super(context);
    }

    @Override
    protected void install(App app) {
        InstallationState.setInstalling(app.getPackageName());
        getTask(app).execute(Paths.getApkPath(context, app.getPackageName(), app.getVersionCode()).toString());
    }

    private InstallTask getTask(final App app) {
        return new InstallTask() {

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    InstallationState.setSuccess(app.getPackageName());
                } else {
                    InstallationState.setFailure(app.getPackageName());
                }
                sendBroadcast(app.getPackageName(), true);
                postInstallationResult(app, success);
            }
        };
    }
}
