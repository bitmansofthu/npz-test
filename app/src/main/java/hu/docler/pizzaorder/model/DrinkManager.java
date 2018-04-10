package hu.docler.pizzaorder.model;


import android.content.Context;

import java.util.List;

import hu.docler.pizzaorder.util.RetrofitFactory;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public class DrinkManager {

    interface DrinkService {
        @GET("bins/150da7")
        Single<List<Drink>> fetchDrinks();
    }

    private Context context;

    private List<Drink> drinks;

    public DrinkManager(Context context) {
        this.context = context;
    }

    public void download(final RemoteOperationCallback<DrinkManager> callback) {
        Retrofit retrofit = RetrofitFactory.create(context);
        DrinkService service = retrofit.create(DrinkService.class);

        service.fetchDrinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> list) throws Exception {
                        setDrinks(list);

                        if (callback != null) {
                            callback.onCompleted(DrinkManager.this);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null) {
                            callback.onFailure(throwable);
                        }
                    }
                });
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }
}