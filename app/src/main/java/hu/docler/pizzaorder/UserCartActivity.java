package hu.docler.pizzaorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.docler.pizzaorder.model.CartItem;
import hu.docler.pizzaorder.model.RemoteOperationCallback;
import hu.docler.pizzaorder.model.UserCart;
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
            updateCart();
        }
    };

    @BindView(R.id.cart_list) RecyclerView cartList;
    @BindView(R.id.checkout) Button checkoutButton;
    @BindView(R.id.cart_empty) TextView cartEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(cartUpdatedReceiver, new IntentFilter(UserCart.ACTION_CART_UPDATED));

        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_cart);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        cartList.addItemDecoration(dividerItemDecoration);
        cartList.setLayoutManager(mLayoutManager);
        cartList.setItemAnimator(new DefaultItemAnimator());
        cartList.setAdapter(cartListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        cartListAdapter.notifyDataSetChanged();
        updateCart();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_drinks) {
            startActivity(new Intent(this, DrinksActivity.class));

            return true;
        }

        return false;
    }

    @OnClick(R.id.checkout)
    public void checkoutClicked() {
        UserCart.getInstance(this).send(new RemoteOperationCallback<String>() {
            @Override
            public void onCompleted(String response) {
                new AlertDialog.Builder(UserCartActivity.this)
                    .setMessage(R.string.cart_checkout_success)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

                UserCart.getInstance(UserCartActivity.this).clear();
                cartListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                new AlertDialog.Builder(UserCartActivity.this)
                        .setMessage(R.string.cart_checkout_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }
        });
    }

    private void updateCart() {
        checkoutButton.setText(getString(R.string.cart_checkout, Utils.formatCurrency(UserCart.getInstance(this).getSumPrice())));

        if (!UserCart.getInstance(this).getItems().isEmpty()) {
            cartEmpty.setVisibility(View.GONE);
        } else {
            cartEmpty.setVisibility(View.VISIBLE);
        }
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
            holder.deleteItem.setOnClickListener(new UserCart.CartItemAction(item, UserCart.CartItemAction.REMOVE));
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
}
