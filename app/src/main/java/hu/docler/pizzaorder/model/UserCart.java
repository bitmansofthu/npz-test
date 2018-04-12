package hu.docler.pizzaorder.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import hu.docler.pizzaorder.util.RetrofitFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public class UserCart {

    class CartRequest {
        @SerializedName("pizzas")
        @Expose
        List<Pizza> pizzas;

        @SerializedName("drinks")
        @Expose
        List<Long> drinks;
    }

    interface CartService {
        @POST("post")
        Call<String> post(@Body CartRequest body);
    }

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
        Retrofit retrofit = RetrofitFactory.create(context, "http://httpbin.org/");
        final CartService service = retrofit.create(CartService.class);

        CartRequest req = createRequest();

        service.post(req).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // TODO alert

                Log.d(UserCart.class.getSimpleName(), "Cart response: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // TODO alert

                Log.w(UserCart.class.getSimpleName(), "Failed to send cart", t);
            }
        });
    }

    private CartRequest createRequest() {
        CartRequest req = new CartRequest();
        req.pizzas = new ArrayList<>();
        req.drinks = new ArrayList<>();

        for (CartItem item : items) {
            if (item instanceof Pizza) {
                req.pizzas.add((Pizza)item);
            } else if (item instanceof Drink) {
                req.drinks.add(((Drink) item).getId());
            }
        }

        return req;
    }

    private void notifyChanged(CartItem item) {
        Intent i = new Intent(ACTION_CART_UPDATED);
        i.putExtra(EXTRA_ITEM_NAME, item.getName());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }
}
