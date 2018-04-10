package hu.docler.pizzaorder.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public class UserCart {

    public static final String ACTION_CART_UPDATED = "action_cart_updated";

    private static volatile UserCart instance;

    public static synchronized UserCart getInstance(Context c) {
        if (instance == null) {
            instance = new UserCart(c);
        }

        return instance;
    }

    private Context context;

    private List<Pizza> pizzas = new ArrayList<>();
    private List<Drink> drinks = new ArrayList<>();

    private UserCart(Context context) {
        this.context = context.getApplicationContext();
    }

    public void add(Pizza pizza) {
        pizzas.add(pizza);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_CART_UPDATED));
    }

    public void add(Drink drink) {
        drinks.add(drink);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_CART_UPDATED));
    }

    public void clear() {
        pizzas.clear();
        drinks.clear();
    }

    public void send(RemoteOperationCallback<UserCart> callback) {

    }
}
