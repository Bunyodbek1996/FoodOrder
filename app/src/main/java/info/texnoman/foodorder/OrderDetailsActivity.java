package info.texnoman.foodorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.texnoman.foodorder.adapters.CartAdapter;
import info.texnoman.foodorder.models.CartProduct;
import info.texnoman.foodorder.models.Order;

import static info.texnoman.foodorder.HomeActivity.progressBar;

public class OrderDetailsActivity extends AppCompatActivity {
    CheckBox status_created, status_before_created, status_received, status_on_way, received_by_delivery_man, status_delivered;
    Order order;
    TextView delivery_time, delivery_price, delivery_price2, min_order_amount, res_name, total_price, cancalled_tv;
    int id, status = -1;
    public Handler handler = null;
    public static Runnable runnable = null;
    LinearLayout checkboxs_linear;
    ProgressDialog dialog;
    RequestQueue queue;
    private ImageView image_restaurant;
    private String image_url;
    TextView order_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Yuklanmoqda");
        dialog.setCanceledOnTouchOutside(false);

        id = getIntent().getExtras().getInt("id");
        queue = Volley.newRequestQueue(this);
        initViews();


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getOrderStatus();
                handler.postDelayed(runnable, 5000);
            }
        };

        handler.postDelayed(runnable, 2000);

    }

    private void initViews() {

        order_info = findViewById(R.id.order_info);
        checkboxs_linear = findViewById(R.id.checkboxs_linear);
        status_before_created = findViewById(R.id.status_before_created);
        total_price = findViewById(R.id.total_price);
        status_created = findViewById(R.id.status_created);
        status_received = findViewById(R.id.status_received);
        status_on_way = findViewById(R.id.status_on_way);
        received_by_delivery_man = findViewById(R.id.received_by_delivery_man);
        status_delivered = findViewById(R.id.status_delivered);
        res_name = findViewById(R.id.name);

        cancalled_tv = findViewById(R.id.cancalled_tv);
        delivery_price = findViewById(R.id.delivery_price);
        delivery_price2 = findViewById(R.id.delivery_price2);
        delivery_time = findViewById(R.id.delivery_time);
        min_order_amount = findViewById(R.id.min_order_price);
        image_restaurant = findViewById(R.id.image_restaurant);

        getOrder();
        setStatusBarColor();
    }

    private void getOrderStatus() {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_SINGLE_ORDER_URL + id + "/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject orderJson = jsonObject.getJSONObject("data");
                                status = orderJson.getInt("status");
                                setStatusOrder();
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
                return map;
            }
        };
        queue.add(request);
    }

    private void getOrder() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_SINGLE_ORDER_URL + id + "/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject orderJson = jsonObject.getJSONObject("data");
                                status = orderJson.getInt("status");
                                String name;
                                if (SplashActivity.LANG.equals("uz")){
                                    name = orderJson.getJSONObject("restaurant").getString("name_uz");
                                }else{
                                    name = orderJson.getJSONObject("restaurant").getString("name_ru");
                                }
                                image_url = orderJson.getJSONObject("restaurant").getString("image");
                                int tot_price = orderJson.getInt("total_price")+orderJson.getInt("delivery_price");
                                int del_price = orderJson.getInt("delivery_price");
                                int del_min = orderJson.getJSONObject("restaurant").getInt("delivery_time_min");
                                int del_max = orderJson.getJSONObject("restaurant").getInt("delivery_time_max");
                                int min_ord = orderJson.getJSONObject("restaurant").getInt("min_order_amount");
                                res_name.setText(name);

                                total_price.setText(getString(R.string.total_price)+" "+tot_price + " "+getString(R.string.sum));
                                delivery_price2.setText(del_price + " "+getString(R.string.sum));
                                delivery_price.setText(del_price + " "+getString(R.string.sum));
                                delivery_time.setText("[" + del_min + "-" + del_max + "]");
                                min_order_amount.setText(min_ord + " "+getString(R.string.sum));
                                loadImages();
                                setStatusOrder();
                                setOrderInfo(orderJson.getJSONArray("pockets"));
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OrderDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getOrder();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
                return map;
            }
        };
        queue.add(request);
    }

    private void setOrderInfo(JSONArray pockets) {
        StringBuffer buffer = new StringBuffer();
        int all_price = 0;
        for (int i=0;i<pockets.length();i++){
            try {
                JSONObject jsonObject = pockets.getJSONObject(i);
                int quantity = jsonObject.getInt("quantity");
                String name;
                if (SplashActivity.LANG.equals("ru")){
                    name = jsonObject.getJSONObject("product").getString("name_ru");
                }else{
                    name = jsonObject.getJSONObject("product").getString("name_uz");
                }
                int price = jsonObject.getJSONObject("product").getInt("price");
                int all = price*quantity;
                all_price = all_price+all;
                buffer.append(i+1+". "+quantity+" * "+price+" = "+all+"\t\t\t\t"+name+"\n");
            } catch (JSONException e) {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        buffer.append("\n"+getString(R.string.total_price)+" "+all_price+" "+getString(R.string.sum));
        order_info.setText(buffer);
    }

    private void loadImages() {
        Glide.with(OrderDetailsActivity.this)
                .load(SplashActivity.BASE_URL + "/storage/restaurant_images/" + image_url)
                .into(image_restaurant);
    }

    private void setStatusOrder() {
        if (status == -1) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
        } else if (status == 1) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
        } else if (status == 2) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
            status_received.setTextColor(getResources().getColor(R.color.blackText));
            status_received.setChecked(true);
        } else if (status == 22) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
            status_received.setTextColor(getResources().getColor(R.color.blackText));
            status_received.setChecked(true);
        } else if (status == 3) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
            status_received.setTextColor(getResources().getColor(R.color.blackText));
            status_received.setChecked(true);
            received_by_delivery_man.setTextColor(getResources().getColor(R.color.blackText));
            received_by_delivery_man.setChecked(true);
        } else if (status == 4) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
            status_received.setTextColor(getResources().getColor(R.color.blackText));
            status_received.setChecked(true);
            received_by_delivery_man.setTextColor(getResources().getColor(R.color.blackText));
            received_by_delivery_man.setChecked(true);
            status_on_way.setTextColor(getResources().getColor(R.color.blackText));
            status_on_way.setChecked(true);
        } else if (status == 5) {
            status_before_created.setTextColor(getResources().getColor(R.color.blackText));
            status_before_created.setChecked(true);
            status_created.setTextColor(getResources().getColor(R.color.blackText));
            status_created.setChecked(true);
            status_received.setTextColor(getResources().getColor(R.color.blackText));
            status_received.setChecked(true);
            received_by_delivery_man.setTextColor(getResources().getColor(R.color.blackText));
            received_by_delivery_man.setChecked(true);
            status_on_way.setTextColor(getResources().getColor(R.color.blackText));
            status_on_way.setChecked(true);
            status_delivered.setTextColor(getResources().getColor(R.color.blackText));
            status_delivered.setChecked(true);
        } else if (status == -2) {

            checkboxs_linear.setVisibility(View.GONE);
            cancalled_tv.setText(getString(R.string.cancelled_by_restaurant));
            cancalled_tv.setVisibility(View.VISIBLE);

        } else if (status == -4) {

            checkboxs_linear.setVisibility(View.GONE);
            cancalled_tv.setText(getString(R.string.cancelled_by_customer));
            cancalled_tv.setVisibility(View.VISIBLE);

        } else if (status == -5) {

            checkboxs_linear.setVisibility(View.GONE);
            cancalled_tv.setText(getString(R.string.cancelled_by_customer));
            cancalled_tv.setVisibility(View.VISIBLE);

        }
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