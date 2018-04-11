package hu.docler.pizzaorder.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public class UserCart {

    public static final String ACTION_CART_UPDATED = "action_cart_updated";
    public static final String EXTRA_ITEM_NAME = "name";

    private static volatile UserCart instance;

    public static class CartItemAction implements View.OnClickListener {
        public static final int ADD = 1;
        public static final int REMOVE = -1;

        CartItem item;
        int operation;

        public CartItemAction(CartItem item, int operation) {
            this.item = item;
            this.operation = operation;
        }

        @Override
        public void onClick(View view) {
            if (operation == ADD) {
                UserCart.getInstance(view.getContext()).add(item);
            } else if (operation == REMOVE) {
                UserCart.getInstance(view.getContext()).remove(item);
            }
        }
    }

    public static synchronized UserCart getInstance(Context c) {
        if (instance == null) {
            instance = new UserCart(c);
        }

        return instance;
    }

    private Context context;

    private List<CartItem> items = new ArrayList<>();

    private UserCart(Context context) {
        this.context = context.getApplicationContext();
    }

    public void add(CartItem item) {
        items.add(item);

        notifyChanged(item);
    }

    public void remove(CartItem item) {
        items.remove(item);

        notifyChanged(item);
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getSumPrice() {
        double sum = 0;

        for (CartItem item : items) {
            sum += item.getPrice();
        }

        return sum;
    }

    public void clear() {
        items.clear();
    }

    public void send(RemoteOperationCallback<UserCart> callback) {

    }

    private void notifyChanged(CartItem item) {
        Intent i = new Intent(ACTION_CART_UPDATED);
        i.putExtra(EXTRA_ITEM_NAME, item.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }
}
