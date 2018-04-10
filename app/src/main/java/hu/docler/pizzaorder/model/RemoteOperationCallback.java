package hu.docler.pizzaorder.model;

/**
 * Created by aquajava on 2018. 03. 31..
 */

public interface RemoteOperationCallback<T> {

    void onCompleted(T caller);

    void onFailure(Throwable t);

}
