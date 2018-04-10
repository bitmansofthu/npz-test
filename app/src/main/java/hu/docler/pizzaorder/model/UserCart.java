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
    public static final String EXTRA_ITEM_NAME = "name";

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

        Intent i = new Intent(ACTION_CART_UPDATED);
        i.putExtra(EXTRA_ITEM_NAME, pizza.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    public void add(Drink drink) {
        drinks.add(drink);

        Intent i = new Intent(ACTION_CART_UPDATED);
        i.putExtra(EXTRA_ITEM_NAME, drink.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    public int getAllItemCount() {
        return pizzas.size() + drinks.size();
    }

    public void clear() {
        pizzas.clear();
        drinks.clear();
    }

    public void send(RemoteOperationCallback<UserCart> callback) {

    }
}
