package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class BetaToggleTask extends PlayStorePayloadTask<Void> {

    private App app;

    public BetaToggleTask(App app) {
        this.app = app;
    }

    @Override
    protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
        api.testingProgram(app.getPackageName(), !app.isTestingProgramOptedIn());
        return null;
    }
}
