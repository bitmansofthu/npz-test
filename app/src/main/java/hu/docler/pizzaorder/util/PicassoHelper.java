package hu.docler.pizzaorder.util;

import android.content.Context;
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
        create(imageView.getContext())
                .load(url)
                .into(imageView);
    }

    public static final void downloadIntoResized(String url, ImageView imageView, int dimenWidth, int dimenHeight) {
        create(imageView.getContext())
                .load(url)
                .resizeDimen(dimenWidth, dimenHeight)
                .into(imageView);
    }

    public static Picasso create(Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.w(TAG, "Image download has failed", exception);
            }
        });
        return builder.build();
    }

}
