package hu.docler.pizzaorder.util;

import android.view.View;
import android.widget.TextView;

public class StatusLine {

    private TextView textView;

    public StatusLine(TextView textView) {
        this.textView = textView;
    }

    public void show(String text, int colorRes) {
        textView.setText(text);
        textView.setBackgroundColor(colorRes);

        textView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        textView.setVisibility(View.GONE);
    }

}
