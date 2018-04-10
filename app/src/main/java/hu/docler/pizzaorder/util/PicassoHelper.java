package hu.docler.pizzaorder.util;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by aquajava on 2018. 04. 08..
 */

public class PicassoHelper {

    static final String TAG = "PicassoHelper";

    public static final void downloadInto(String url, ImageView imageView) {
        Picasso.Builder builder = new Picasso.Builder(imageView.getContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.w(TAG, "Image download has failed", exception);
            }
        });
        builder.build()
                .load(url)
                .into(imageView);
    }

}
