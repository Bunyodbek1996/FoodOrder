package info.texnoman.foodorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import info.texnoman.foodorder.adapters.FoodTypeAdapter;
import info.texnoman.foodorder.adapters.FoodTypeCategoryAdapter;
import info.texnoman.foodorder.models.FoodType;
import info.texnoman.foodorder.models.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.paperdb.Paper;
import static info.texnoman.foodorder.SplashActivity.LANG;

public class RestaurantDetailsActivity extends AppCompatActivity {
    public static String res_id;
    String name,logo,image,working_hours_from,working_hours_to;
    public static double longitude,latitude;
    int delivery_time_min,delivery_time_max, delivery_price,min_order_amunt,is_top,status;
    List<FoodType> foodTypeList;
    public static RecyclerView recycler_category,recycler_product;
    public static TextView badge_count,bottom_badge_count;
    private ImageView restaurant_image;
    public static ProgressDialog dialog;
    public static LinearLayout cart_linear;
    TextView restaurant_name;
    FloatingActionButton filter_choice;
    List<String> listFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        res_id = getIntent().getExtras().getString("restaurantd_id");
        initViews();
        getRestaurantDeatils(res_id);
        setStatusBarColor();


    }

    private void initViews() {
        listFilter = new ArrayList<>();
        dialog = new ProgressDialog(this);
        filter_choice = findViewById(R.id.filter_choice);
        restaurant_name = findViewById(R.id.restaurant_name);
        cart_linear = findViewById(R.id.cart_linear);
        restaurant_image = findViewById(R.id.restaurant_image);
        bottom_badge_count = findViewById(R.id.bottom_badge_textview);
        badge_count = findViewById(R.id.badge_textview);
        recycler_product = findViewById(R.id.recycler_product);
        recycler_product.setLayoutManager(new LinearLayoutManager(this));

        recycler_category = findViewById(R.id.recyclerview_category);
        recycler_category.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_category.setLayoutManager(manager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recycler_product.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recycler_product.getLayoutManager();
                    int visibleItem = layoutManager.findLastVisibleItemPosition();
                    changeCategoryBackground(visibleItem);
                }
            });
        }
        Paper.init(this);
        String count = Paper.book().read("badge");
        if (count != null){
            int countNext = Integer.parseInt(count);
            if(countNext>0){
                badge_count.setVisibility(View.VISIBLE);
                bottom_badge_count.setVisibility(View.VISIBLE);
                badge_count.setText(countNext+"");
                bottom_badge_count.setText(getString(R.string.cart)+"("+countNext+")");
                cart_linear.setVisibility(View.VISIBLE);
            }
        }
        filter_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });
    }

    private void showFilter() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_filter);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.select_food_item);
        for (int i=0;i<listFilter.size();i++){
            arrayAdapter.add(listFilter.get(i));
        }
        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String strName = arrayAdapter.getItem(which);
//                AlertDialog.Builder builderInner = new AlertDialog.Builder(RestaurantDetailsActivity.this);
//                builderInner.setMessage(strName);
//                builderInner.setTitle("Your Selected Item is");
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderInner.show();
                RestaurantDetailsActivity.recycler_category.getLayoutManager().scrollToPosition(which);
                FoodTypeCategoryAdapter categoryAdapter = new FoodTypeCategoryAdapter(
                        RestaurantDetailsActivity.this, foodTypeList,which);
                RestaurantDetailsActivity.recycler_category.setAdapter(categoryAdapter);
                RestaurantDetailsActivity.recycler_product.getLayoutManager().scrollToPosition(which);
            }
        });
        builderSingle.show();
    }

    public void changeCategoryBackground(int visibleItem) {
        recycler_category.getLayoutManager().scrollToPosition(visibleItem);
        FoodTypeCategoryAdapter categoryAdapter = new FoodTypeCategoryAdapter(
                RestaurantDetailsActivity.this, foodTypeList,visibleItem);
        recycler_category.setAdapter(categoryAdapter);
    }

    private void getRestaurantDeatils(String res_id) {
        dialog.setMessage(getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.RESTAURANTS_DETAILS_URL+res_id+"/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                jsonParser(jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getRestaurantDeatils(res_id);
            }
        }){
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

    private void jsonParser(JSONObject jsonObject) {
        foodTypeList = new ArrayList<>();
        JSONObject data = null;
        try {
            data = jsonObject.getJSONObject("data");
            JSONObject restaurantJson = data.getJSONObject("restaurant");
            if (LANG.equals("uz")){
                name = restaurantJson.getString("name_uz");
            }else{
                name = restaurantJson.getString("name_ru");
            }
            restaurant_name.setText(name+"");
            logo = restaurantJson.getString("logo");
            image = restaurantJson.getString("image");
            min_order_amunt = restaurantJson.getInt("min_order_amount");
            working_hours_from = restaurantJson.getString("working_hours_from");
            working_hours_to = restaurantJson.getString("working_hours_to");
            delivery_time_min = restaurantJson.getInt("delivery_time_min");
            delivery_time_max = restaurantJson.getInt("delivery_time_max");
            delivery_price = restaurantJson.getInt("delivery_price");
            latitude = restaurantJson.getDouble("latitude");
            longitude = restaurantJson.getDouble("longitude");
            is_top = restaurantJson.getInt("is_top");
            status = restaurantJson.getInt("status");
            String imageRes = restaurantJson.getString("image");
            Glide.with(this)
                    .load(SplashActivity.BASE_URL +"/storage/restaurant_images/"+ imageRes).into(restaurant_image);


            JSONArray foodtypes = data.getJSONArray("foodtypes");
            for (int i = 0 ; i<foodtypes.length();i++){
                JSONObject typeJson = foodtypes.getJSONObject(i);
                int type_id = typeJson.getInt("id");
                int type_restaurant_id = typeJson.getInt("restaurant_id");
                String type_name = (LANG.equals("uz"))?typeJson.getString("name_uz"):typeJson.getString("name_ru");
                listFilter.add(type_name);
                String type_icon = "icon"; // still type_icon don not come so I equals string to 'icon'
                int status = typeJson.getInt("status");
                List<Product> productList = new ArrayList<>();
                JSONArray products = typeJson.getJSONArray("products");
                for(int j=0;j<products.length();j++){
                    JSONObject productdetails = products.getJSONObject(j);
                    int id = productdetails.getInt("id");
                    int foodtype_id = productdetails.getInt("foodtype_id");
                    String name;
                    String description;
                    if (LANG.equals("uz")){
                        name = productdetails.getString("name_uz");
                        description = productdetails.getString("description_uz");
                    }else{
                        name = productdetails.getString("name_ru");
                        description = productdetails.getString("description_ru");
                    }
                    String image = productdetails.getString("image");
                    int price = productdetails.getInt("price");
                    String unity = productdetails.getString("unity");
                    int status_product = productdetails.getInt("status");
                    List<Product> children = new ArrayList<>();
                    JSONArray childrenProduct = productdetails.getJSONArray("children");
                    if (childrenProduct.length()>0){
                        for (int k=0;k<childrenProduct.length();k++){
                            JSONObject childJson = childrenProduct.getJSONObject(k);
                            int id1 = childJson.getInt("id");
                            int foodtype_id1 = childJson.getInt("foodtype_id");
                            String name1;
                            String description1;
                            if (LANG.equals("uz")){
                                name1 = childJson.getString("name_uz");
                                description1 = childJson.getString("description_uz");
                            }else{
                                name1 = childJson.getString("name_ru");
                                description1 = childJson.getString("description_ru");
                            }
                            String image1 = childJson.getString("image");
                            int price1 = childJson.getInt("price");
                            String unity1 = childJson.getString("unity");
                            int status_product1 = childJson.getInt("status");
                            Product product1 = new Product(id1,foodtype_id1,name1,description1,image1,price1
                                    ,unity1,status_product1,null);
                            if (status_product1 == 1){
                                children.add(product1);
                            }
                        }
                    }
                    Product product = new Product(id,foodtype_id,name,description,image,price
                    ,unity,status_product,children);
                    if (status_product == 1){
                        productList.add(product);
                    }
                }
                FoodType foodType = new FoodType(type_id,type_restaurant_id,
                        type_name,type_icon,status,productList);
                if (productList.size()>0){
                    foodTypeList.add(foodType);
                }
            }
            FoodTypeAdapter productAdapter = new FoodTypeAdapter(this,this,foodTypeList,recycler_category);
            recycler_product.setAdapter(productAdapter);
            FoodTypeCategoryAdapter categoryAdapter = new FoodTypeCategoryAdapter(this,foodTypeList);
            recycler_category.setAdapter(categoryAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void passCartActivity(View view) {
        startActivity(new Intent(this,CartActivity.class));
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