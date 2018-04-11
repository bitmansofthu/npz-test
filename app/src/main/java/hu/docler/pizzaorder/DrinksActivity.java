package hu.docler.pizzaorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.docler.pizzaorder.model.CartItem;
import hu.docler.pizzaorder.model.DrinkManager;
import hu.docler.pizzaorder.model.RemoteOperationCallback;
import hu.docler.pizzaorder.model.UserCart;
import hu.docler.pizzaorder.util.Utils;

public class DrinksActivity extends AppCompatActivity {

    private BroadcastReceiver cartUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.cart_layout),
                    getString(R.string.added_to_cart, intent.getStringExtra(UserCart.EXTRA_ITEM_NAME)),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    };

    @BindView(R.id.drinks_list) RecyclerView drinksList;

    private DrinkManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(cartUpdatedReceiver, new IntentFilter(UserCart.ACTION_CART_UPDATED));

        setContentView(R.layout.activity_drinks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        manager = new DrinkManager(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        drinksList.addItemDecoration(dividerItemDecoration);
        drinksList.setLayoutManager(mLayoutManager);
        drinksList.setItemAnimator(new DefaultItemAnimator());
        drinksList.setAdapter(drinksListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        manager.download(new RemoteOperationCallback<DrinkManager>() {
            @Override
            public void onCompleted(DrinkManager caller) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private RecyclerView.Adapter drinksListAdapter = new RecyclerView.Adapter<DrinksActivity.ViewHolder>(){

        @Override
        public DrinksActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_listitem,
                    parent, false);

            return new DrinksActivity.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DrinksActivity.ViewHolder holder, int position) {
            List<CartItem> items = UserCart.getInstance(DrinksActivity.this).getItems();

            CartItem item = items.get(position);
            holder.title.setText(item.getName());
            holder.price.setText(Utils.formatCurrency(item.getPrice()));
            holder.addItem.setOnClickListener(new UserCart.CartItemAction(item, UserCart.CartItemAction.ADD));
        }

        @Override
        public int getItemCount() {
            return UserCart.getInstance(DrinksActivity.this).getItems().size();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cart_item_name)
        TextView title;
        @BindView(R.id.cart_item_price) TextView price;
        @BindView(R.id.cart_item_delete)
        ImageButton addItem;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }
}
