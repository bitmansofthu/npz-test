package hu.docler.pizzaorder.model;

import android.content.Context;
import android.util.LongSparseArray;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hu.docler.pizzaorder.util.RetrofitFactory;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public final class PizzaManager {

    class PizzaResponse {
        @SerializedName("basePrice")
        @Expose
        public Double basePrice;

        @SerializedName("pizzas")
        @Expose
        public List<Pizza> pizzas;
    }

    interface PizzaManagerService {
        @GET("bins/ozt3z")
        Single<List<Ingredient>> fetchIngredients();

        @GET("bins/dokm7")
        Single<PizzaResponse> fetchPizzas();
    }

    private Context context;

    private double basePrice;
    private LongSparseArray<Ingredient> ingredientsMap = new LongSparseArray<>();
    private List<Pizza> pizzas = new ArrayList<>();

    public PizzaManager(Context context) {
        this.context = context;
    }

    public void download(final RemoteOperationCallback<PizzaManager> callback) {
        Retrofit retrofit = RetrofitFactory.create(context);
        final PizzaManagerService service = retrofit.create(PizzaManagerService.class);

        service.fetchIngredients()
                .flatMap(ingredients ->  {
                    buildIngredientsMap(ingredients);
                    return service.fetchPizzas();
                })
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pzr -> {
                    basePrice = pzr.basePrice;
                    pizzas = pzr.pizzas;

                    for (Pizza p : pizzas) {
                        p.setPrice(calcPizzaPrice(p));
                    }

                    if (callback != null) {
                        callback.onCompleted(PizzaManager.this);
                    }
                }, throwable ->  {
                    if (callback != null) {
                        callback.onFailure(throwable);
                    }
                });
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public Pizza createPizza(String name, List<Ingredient> ingredients) {
        Pizza p = new Pizza();
        List<Long> ingrints = new ArrayList<>();

        p.setName(name);

        for (Ingredient ing : ingredients) {
            ingrints.add(ing.getId());
        }
        p.setIngredients(ingrints);

        return p;
    }

    public List<Ingredient> getIngredientsSorted() {
        ArrayList<Ingredient> list = new ArrayList();
        for (int i = 0; i < ingredientsMap.size(); i++) {
            list.add(ingredientsMap.valueAt(i));
        }

        Collections.sort(list, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient i1, Ingredient i2) {
                return i1.getId().compareTo(i2.getId());
            }
        });

        return list;
    }

    public Ingredient getIngredient(long id) {
        return ingredientsMap.get(id);
    }

    public double calcPizzaPrice(Pizza p) {
        double price = this.basePrice;

        for (long iid : p.getIngredients()) {
            price += getIngredient(iid).getPrice();
        }

        return price;
    }

    public List<Ingredient> getPizzaIngredients(Pizza p) {
        ArrayList<Ingredient> list = new ArrayList();

        for (Long id : p.getIngredients()) {
            list.add(getIngredient(id));
        }

        return list;
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    private void buildIngredientsMap(List<Ingredient> ings) {
        for (Ingredient ing : ings) {
            ingredientsMap.put(ing.getId(), ing);
        }
    }

}
