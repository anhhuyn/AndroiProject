package vn.iotstar.ecoveggieapp.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import vn.iotstar.ecoveggieapp.R;

public class FullScreenImageDialog extends Dialog {

    private String imageUrl;

    public FullScreenImageDialog(Context context, String imageUrl) {
        super(context);
        this.imageUrl = imageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_full_screen_image);

        ImageView imgFullScreen = findViewById(R.id.imgFullScreen);
        Glide.with(getContext())
                .load(imageUrl)
                .into(imgFullScreen);
    }
}
