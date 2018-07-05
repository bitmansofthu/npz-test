package hu.docler.pizzaorder.util;

import android.view.View;
import android.widget.TextView;

public class StatusLine {

    private TextView textView;

    public StatusLine(TextView textView) {
        this.textView = textView;
    }

    public void show(String text, int color) {
        textView.setText(text);
        textView.setBackgroundColor(color);

        textView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        textView.setVisibility(View.GONE);
    }

}
