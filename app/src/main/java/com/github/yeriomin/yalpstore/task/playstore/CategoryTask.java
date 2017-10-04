package com.github.yeriomin.yalpstore.task.playstore;

import android.net.Uri;
import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.BrowseLink;
import com.github.yeriomin.playstoreapi.BrowseResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.CategoryManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CategoryTask extends PlayStorePayloadTask<Void> implements CloneableTask {

    private CategoryManager manager;

    public void setManager(CategoryManager manager) {
        this.manager = manager;
    }

    @Override
    public CloneableTask clone() {
        CategoryTask task = new CategoryTask();
        task.setManager(manager);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
        Map<String, String> topCategories = buildCategoryMap(api.categories());
        manager.save(CategoryManager.TOP, topCategories);
        for (String categoryId: topCategories.keySet()) {
            manager.save(categoryId, buildCategoryMap(api.categories(categoryId)));
        }
        return null;
    }

    private Map<String, String> buildCategoryMap(BrowseResponse response) {
        Map<String, String> categories = new HashMap<>();
        for (BrowseLink category: response.getCategoryContainer().getCategoryList()) {
            String categoryId = Uri.parse(category.getDataUrl()).getQueryParameter("cat");
            if (TextUtils.isEmpty(categoryId)) {
                continue;
            }
            categories.put(categoryId, category.getName());
        }
        return categories;
    }
}
