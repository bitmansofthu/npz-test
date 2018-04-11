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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Currency;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.docler.pizzaorder.model.CartItem;
import hu.docler.pizzaorder.model.Pizza;
import hu.docler.pizzaorder.model.RemoteOperationCallback;
import hu.docler.pizzaorder.model.UserCart;
import hu.docler.pizzaorder.util.PicassoHelper;
import hu.docler.pizzaorder.util.Utils;

public class UserCartActivity extends AppCompatActivity {

    private BroadcastReceiver cartUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.cart_layout),
                    getString(R.string.removed_from_cart, intent.getStringExtra(UserCart.EXTRA_ITEM_NAME)),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();

            cartListAdapter.notifyDataSetChanged();
            updateSum();
        }
    };

    @BindView(R.id.cart_list) RecyclerView cartList;
    @BindView(R.id.checkout) Button checkoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(cartUpdatedReceiver, new IntentFilter(UserCart.ACTION_CART_UPDATED));

        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        cartList.setLayoutManager(mLayoutManager);
        cartList.setItemAnimator(new DefaultItemAnimator());
        cartList.setAdapter(cartListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateSum();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(cartUpdatedReceiver);
    }

    @OnClick(R.id.checkout)
    public void checkoutClicked() {
        UserCart.getInstance(this).send(new RemoteOperationCallback<UserCart>() {
            @Override
            public void onCompleted(UserCart caller) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void updateSum() {
        checkoutButton.setText(getString(R.string.cart_checkout, Utils.formatCurrency(UserCart.getInstance(this).getSumPrice())));
    }

    private RecyclerView.Adapter cartListAdapter = new RecyclerView.Adapter<UserCartActivity.ViewHolder>(){

        @Override
        public UserCartActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_listitem,
                    parent, false);

            return new UserCartActivity.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserCartActivity.ViewHolder holder, int position) {
            List<CartItem> items = UserCart.getInstance(UserCartActivity.this).getItems();

            CartItem item = items.get(position);
            holder.title.setText(item.getName());
            holder.price.setText(Utils.formatCurrency(item.getPrice()));
            holder.deleteItem.setOnClickListener(new DeleteCartItemClickListener(item));
        }

        @Override
        public int getItemCount() {
            return UserCart.getInstance(UserCartActivity.this).getItems().size();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cart_item_name) TextView title;
        @BindView(R.id.cart_item_price) TextView price;
        @BindView(R.id.cart_item_delete) ImageButton deleteItem;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }

    class DeleteCartItemClickListener implements View.OnClickListener {

        CartItem item;

        public DeleteCartItemClickListener(CartItem item) {
            this.item = item;
        }

        @Override
        public void onClick(View view) {
            UserCart.getInstance(view.getContext()).remove(item);
        }
    }
}
