package info.texnoman.foodorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import info.texnoman.foodorder.models.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class ProductDetailActivity extends AppCompatActivity {
    LinearLayout add_to_cart, radioContainer;
    ImageView product_image;
    ProgressBar progressBar;
    public static ProgressDialog dialog;
    TextView btn_plus, btn_minus, product_name, product_description,
            product_price, product_count, total_price, badge_count;
    Product product, sendProduct;
    List<Product> childProducts;
    int count = 1;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.sending));

        initViews();
        setStatusBarColor();

    }

    private void initViews() {
        radioContainer = findViewById(R.id.radioContainer);
        product = (Product) getIntent().getSerializableExtra("product");
        sendProduct = (Product) getIntent().getSerializableExtra("product");
        childProducts = new ArrayList<>();
        childProducts = product.getChildren();
        if (childProducts.size() > 0) {
            createRadio();
        } else {
            radioContainer.setVisibility(View.GONE);
        }
        progressBar = findViewById(R.id.progressBar);
        add_to_cart = findViewById(R.id.add_to_cart);

        product_image = findViewById(R.id.product_image);
        imageLoad();

        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        product_name = findViewById(R.id.product_name);
        product_description = findViewById(R.id.product_description);
        product_price = findViewById(R.id.product_price);
        product_count = findViewById(R.id.product_count);
        total_price = findViewById(R.id.total_price);
        badge_count = findViewById(R.id.badge_count);

        product_name.setText(product.getName());
        product_description.setText(product.getDescription());
        product_price.setText(product.getPrice() +" "+getString(R.string.sum));
        total = product.getPrice();
        total_price.setText(product.getPrice() +" "+getString(R.string.sum));

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionPlus();
            }
        });
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                functionMinus();
            }
        });
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.init(ProductDetailActivity.this);
                String res_id = Paper.book().read("restaurant_id");
                if (res_id != null) {
                    if (res_id.equals(RestaurantDetailsActivity.res_id)) {
                        add_to_cart_function();
                    } else {
                        add_to_cart_other_res();
                    }
                } else {
                    add_to_cart_function();
                }
            }
        });


        Paper.init(this);
        String count = Paper.book().read("badge");
        if (count != null) {
            int countNext = Integer.parseInt(count);
            if (countNext > 0) {
                badge_count.setVisibility(View.VISIBLE);
                badge_count.setText(countNext + "");
            }
        }

    }

    private void imageLoad() {
        Glide.with(this)
                .load(SplashActivity.BASE_URL + "/storage/product_images/" + product.getImage()).into(product_image);

    }

    private void createRadio() {
        List<Product> childProducts = product.getChildren();
        int k = childProducts.size();
        final RadioButton[] radioButton = new RadioButton[k + 1];
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(getResources().getDrawable(android.R.drawable.divider_horizontal_textfield, this.getTheme()));//create the RadioGroup
        radioGroup.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for (int i = 0; i < k; i++) {
            radioButton[i] = new RadioButton(this);
            radioButton[i].setText(childProducts.get(i).getName() + "  (" + childProducts.get(i).getPrice() +" "+getString(R.string.sum)+")");
            radioButton[i].setId(i + 100);
            radioButton[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton[i].setPadding(0, 20, 0, 20);
            radioGroup.addView(radioButton[i]);
        }
        RadioButton radioBtn = new RadioButton(this);
        radioBtn.setText(product.getName() + "  (" + product.getPrice()+" "+getString(R.string.sum)+")");
        radioBtn.setId(49 + 1);
        radioBtn.setChecked(true);
        radioBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioBtn.setPadding(0, 20, 0, 20);
        radioGroup.addView(radioBtn);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == 50) {
                    sendProduct = product;
                    int price = product.getPrice();
                    product_price.setText(price +" "+getString(R.string.sum));
                    total_price.setText(count * price + "");
                    product_description.setText(sendProduct.getDescription());
                } else {
                    int position = i - 100;
                    int price = product.getChildren().get(position).getPrice();
                    product_price.setText(price +" "+getString(R.string.sum));
                    total_price.setText(count * price + "");
                    sendProduct = product.getChildren().get(i - 100);
                    product_description.setText(sendProduct.getDescription());
                }
            }
        });
        radioContainer.addView(radioGroup);//you add the wh
    }

    private void add_to_cart_other_res() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.notification)
                .setMessage(R.string.cart_delete_other_res)
                .setPositiveButton(getString(R.string.positive_button_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        sendRequestForClearCart();
                    }
                }).setNegativeButton(getString(R.string.negative_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void sendRequestForClearCart() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.CLEAR_CART_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                add_to_cart_function();
                                progressBar.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                changeBadge(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductDetailActivity.this, "error: " + error, Toast.LENGTH_SHORT).show();
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

    private void changeBadge(int i) {
        if (i != 0) {
            int countNext = i;
            RestaurantDetailsActivity.badge_count.setVisibility(View.VISIBLE);
            RestaurantDetailsActivity.badge_count.setText(countNext + "");
            RestaurantDetailsActivity.cart_linear.setVisibility(View.VISIBLE);
            RestaurantDetailsActivity.bottom_badge_count.setText(getString(R.string.cart) + "(" + countNext + ")");
            Paper.book().write("badge", "" + countNext);
            this.finish();
        } else {
            RestaurantDetailsActivity.badge_count.setVisibility(View.GONE);
            RestaurantDetailsActivity.badge_count.setText("");
            RestaurantDetailsActivity.cart_linear.setVisibility(View.GONE);
            Paper.book().write("badge", "0");
            this.finish();
        }
    }

    private void add_to_cart_function() {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.ADD_TO_CART_URL + sendProduct.getId() + "/add_to_cart",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                int cart_counter = data.getInt("cart_counter");
                                Paper.init(ProductDetailActivity.this);
                                Paper.book().write("restaurant_id", "" + RestaurantDetailsActivity.res_id);
                                Paper.book().write("res_lat", "" + RestaurantDetailsActivity.latitude);
                                Paper.book().write("res_lon", "" + RestaurantDetailsActivity.longitude);
                                progressBar.setVisibility(View.GONE);
                                changeBadge(cart_counter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(ProductDetailActivity.this, "error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> quantity = new HashMap<>();
                quantity.put("quantity", count + "");
                return new JSONObject(quantity).toString().getBytes();
            }

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

    private void functionPlus() {
        count = count + 1;
        total = sendProduct.getPrice() * count;
        total_price.setText(total + "");
        product_count.setText(count + "");
    }

    private void functionMinus() {
        if (count > 1) {
            count = count - 1;
            total = product.getPrice() * count;
            total_price.setText(total + "");
            product_count.setText(count + "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onBackPress(View view) {
        onBackPressed();
    }

    public void passCartActivity(View view) {
        startActivity(new Intent(this, CartActivity.class));
        this.finish();
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