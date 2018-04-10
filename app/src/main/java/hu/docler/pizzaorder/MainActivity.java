package hu.docler.pizzaorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.docler.pizzaorder.util.PicassoHelper;
import hu.docler.pizzaorder.model.Pizza;
import hu.docler.pizzaorder.model.PizzaManager;
import hu.docler.pizzaorder.model.RemoteOperationCallback;
import hu.docler.pizzaorder.model.UserCart;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pizza_list) RecyclerView pizzaList;

    private PizzaManager pizzaManager;

    private BroadcastReceiver cartUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.home_layout),
                    getString(R.string.added_to_cart, intent.getStringExtra(UserCart.EXTRA_ITEM_NAME)),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();

            invalidateOptionsMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        pizzaManager = new PizzaManager(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        pizzaList.setLayoutManager(mLayoutManager);
        pizzaList.setItemAnimator(new DefaultItemAnimator());
        pizzaList.setAdapter(pizzaListAdapter);

        fetch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.cart_item_count);
        item.setActionView(R.layout.action_cart_count);
        TextView cartCount = (TextView) item.getActionView();
        cartCount.setText(String.valueOf(UserCart.getInstance(this).getAllItemCount()));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_cart) {
            showCart();

            return true;
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(cartUpdatedReceiver, new IntentFilter(UserCart.ACTION_CART_UPDATED));
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(cartUpdatedReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @OnClick(R.id.add_pizza)
    public void addPizza() {
        Intent i = new Intent(this, CreatePizzaActivity.class);
        //startActivityForResult(i, CreatePizzaActivity.REQUEST_CODE);
    }

    public void showCart() {
        Intent i = new Intent(this, UserCartActivity.class);
        startActivity(i);
    }

    private void updatePizzaList() {
        pizzaListAdapter.notifyDataSetChanged();
    }

    private void fetch() {
        pizzaManager.download(new RemoteOperationCallback<PizzaManager>() {
            @Override
            public void onCompleted(PizzaManager manager) {
                updatePizzaList();
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO show alert
            }
        });
    }

    private RecyclerView.Adapter pizzaListAdapter = new RecyclerView.Adapter<ViewHolder>(){

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_pizza_listitem,
                    parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Pizza pizza = pizzaManager.getPizzas().get(position);

            holder.title.setText(pizza.getName());
            if (pizza.getImageUrl() != null) {
                PicassoHelper.downloadIntoResized(pizza.getImageUrl(), holder.image, R.dimen.home_item_image_size, R.dimen.home_item_image_size);
            }
            holder.addToCart.setText(String.format("%.2f", pizza.getPrice()));
            holder.addToCart.setOnClickListener(new AddToCartClickListener(pizza));
        }

        @Override
        public int getItemCount() {
            return pizzaManager.getPizzas().size();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.ingredients) TextView ingedients;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.addtocart) Button addToCart;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }

    class AddToCartClickListener implements View.OnClickListener {

        Pizza pizza;

        public AddToCartClickListener(Pizza p) {
            pizza = p;
        }

        @Override
        public void onClick(View view) {
            UserCart.getInstance(view.getContext()).add(pizza);
        }
    }

}
