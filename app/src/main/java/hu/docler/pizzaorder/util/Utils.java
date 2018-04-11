package hu.docler.pizzaorder.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Utils {

    private static NumberFormat currencyFormat;

    static {
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormat.setCurrency(Currency.getInstance(Locale.US));
    }

    public static final String formatCurrency(double currency) {
        return currencyFormat.format(currency);
    }

}
