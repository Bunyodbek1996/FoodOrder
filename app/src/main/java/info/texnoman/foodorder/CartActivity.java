package info.texnoman.foodorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import info.texnoman.foodorder.adapters.CartAdapter;
import info.texnoman.foodorder.models.CartProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static info.texnoman.foodorder.HomeActivity.progressBar;

public class CartActivity extends AppCompatActivity {
    public static List<CartProduct> cartList;
    RecyclerView recycler_cart;
    RelativeLayout createOrder;
    int delivery_price;
    String restaurant_id;
    public static TextView products_price_textview;
    public static ImageView empty_cart;
    public static int products_price = 0;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        products_price = 0;
        initViews();

        setStatusBarColor();

    }
    private void initViews() {
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);

        products_price_textview = findViewById(R.id.products_price_textview);
        createOrder = findViewById(R.id.create_order);
        empty_cart = findViewById(R.id.empty_cart);
        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setHasFixedSize(true);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        Paper.init(this);
        restaurant_id = Paper.book().read("restaurant_id");


        getCartList();

        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (cartList.size()>0){
                   Intent intent = new Intent(CartActivity.this,PlaceOrderActivity.class);
                   intent.putExtra("order_quantity",cartList.size()+"");
                   intent.putExtra("total_price",products_price+"");
                   intent.putExtra("delivery_price",delivery_price+"");
                   startActivity(intent);
                   CartActivity.this.finish();
               }else{

               }
            }
        });
    }
    private void getCartList() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_CART_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                progressBar.setVisibility(View.GONE);
                                JSONObject jsonData = jsonObject.getJSONObject("data");
                                delivery_price = jsonData.getInt("delivery_price");
                                JSONArray jsonArray = jsonData.getJSONArray("purchase");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject orderJson = jsonArray.getJSONObject(i);
                                    int quantity = orderJson.getInt("quantity");
                                    JSONObject productJson = orderJson.getJSONObject("product");
                                    int id = productJson.getInt("id");
                                    int foodtype_id = productJson.getInt("foodtype_id");
                                    int parent_id = productJson.getInt("parent_id");
                                    String name;
                                    String description;
                                    if (SplashActivity.LANG=="ru"){
                                        name = productJson.getString("name_uz");
                                        description = productJson.getString("description_uz");
                                    }else{
                                        name = productJson.getString("name_ru");
                                        description = productJson.getString("description_ru");
                                    }
                                    String image = productJson.getString("image");
                                    int price = productJson.getInt("price");
                                    products_price += price * quantity;
                                    CartProduct cartProduct = new CartProduct(id,foodtype_id,parent_id,name,description,image,price,quantity);
                                    cartList.add(cartProduct);
                                }
                                CartAdapter adapter = new CartAdapter(CartActivity.this,cartList);
                                recycler_cart.setAdapter(adapter);
                                products_price_textview.setText(products_price+" "+getString(R.string.sum));
                                if (cartList.size()<1){
                                    empty_cart.setVisibility(View.VISIBLE);
                                    if (SplashActivity.LANG == "ru"){
                                        empty_cart.setImageDrawable(getResources().getDrawable(R.drawable.empty_cart_ru));
                                    }
                                }
                            }else{
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CartActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getCartList();
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String,String> id = new HashMap<>();
                id.put("restaurant_id",restaurant_id);
                return new JSONObject(id).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Content-Type","application/json");
                map.put("auth_token",SplashActivity.TOKEN);
                return map;
            }
        };
        queue.add(request);
    }

    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 23){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}