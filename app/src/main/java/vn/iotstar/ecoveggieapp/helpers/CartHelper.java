package vn.iotstar.ecoveggieapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import vn.iotstar.ecoveggieapp.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class CartHelper {

    private static final String PREF_NAME = "ecoveggie_cart_data";
    private static final String KEY_CART_ITEMS = "cart_items";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CartHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveCartItems(List<CartItem> cartItems) {
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString(KEY_CART_ITEMS, json);
        editor.apply();
    }

    public List<CartItem> getCartItems() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_CART_ITEMS, "");
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void clearCart() {
        editor.clear();
        editor.apply();
    }
}
