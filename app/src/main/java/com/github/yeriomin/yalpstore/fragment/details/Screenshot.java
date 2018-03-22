package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.FullscreenImageActivity;
import com.github.yeriomin.yalpstore.ImageAdapter;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

public class Screenshot extends Abstract {

    public Screenshot(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        activity.findViewById(R.id.screenshots_panel).setVisibility(app.getScreenshotUrls().size() > 0 ? View.VISIBLE : View.GONE);
        if (app.getScreenshotUrls().size() > 0) {
            drawGallery();
        }
    }

    private void drawGallery() {
        Gallery gallery = activity.findViewById(R.id.screenshots_gallery);
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        gallery.setAdapter(new ImageAdapter(activity, app.getScreenshotUrls(), screenWidth));
        gallery.setSpacing(10);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, FullscreenImageActivity.class);
                intent.putExtra(FullscreenImageActivity.INTENT_SCREENSHOT_NUMBER, position);
                activity.startActivity(intent);
            }
        });
    }
}
