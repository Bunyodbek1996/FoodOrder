package info.texnoman.foodorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import info.texnoman.foodorder.NoInternetActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.adapters.OrderAdapter;
import info.texnoman.foodorder.models.Order;
import info.texnoman.foodorder.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.texnoman.foodorder.HomeActivity;

public class OrderFragment extends Fragment {
    RecyclerView recyclerview_order;
    List<Order> orderList;
    public static LinearLayout wishlist_empty_linear;
    private SwipeRefreshLayout swipe_refresh;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        swipe_refresh = root.findViewById(R.id.swipe_refresh);
        wishlist_empty_linear = root.findViewById(R.id.wishlist_empty_linear);
       recyclerview_order = root.findViewById(R.id.recyclerview_order);
       recyclerview_order.setHasFixedSize(true);
       recyclerview_order.setLayoutManager(new LinearLayoutManager(getContext()));

       orderList = new ArrayList<>();
       getOrders();
       initSwipeRefresh(swipe_refresh);
    }
    private void initSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getOrders();
                    }
                }
        );
    }

    private void getOrders() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.ORDER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipe_refresh.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                orderList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    JSONObject orderJson = data.getJSONObject(i);
                                    int id = orderJson.getInt("id");
                                    int customer_id = orderJson.getInt("customer_id");
                                    String  name = orderJson.getString("name");
                                    int restaurant_id = orderJson.getInt("restaurant_id");
                                    int delivery_price = orderJson.getInt("delivery_price");
                                    int total_price = orderJson.getInt("total_price");
                                    String address = orderJson.getString("address");
                                    int phone = orderJson.getInt("phone");
                                    String comment = orderJson.getString("comment");
                                    int status = orderJson.getInt("status");
                                    double latitude = orderJson.getDouble("latitude");
                                    double longitude = orderJson.getDouble("longitude");
                                    String created_at = orderJson.getString("created_at");
                                    String restaurant_name;
                                    if (SplashActivity.LANG.equals("uz")){
                                        restaurant_name = orderJson.getJSONObject("restaurant").getString("name_uz");
                                    }else{
                                        restaurant_name = orderJson.getJSONObject("restaurant").getString("name_ru");
                                    }
                                    JSONObject restaurantJson = orderJson.getJSONObject("restaurant");
                                    int id1 = restaurantJson.getInt("id");
                                    String name1;
                                    if (SplashActivity.LANG != null){
                                        if ( SplashActivity.LANG.equals("uz")){
                                            name1 = restaurantJson.getString("name_uz");
                                        }else{
                                            name1 = restaurantJson.getString("name_ru");
                                        }
                                    }else{
                                        name1 = restaurantJson.getString("name_ru");
                                    }
                                    String logo1 = restaurantJson.getString("logo");
                                    String image1 = restaurantJson.getString("image");
                                    int min_order_amount1 = restaurantJson.getInt("min_order_amount");
                                    String working_hours_from1 = restaurantJson.getString("working_hours_from");
                                    String working_hours_to1 = restaurantJson.getString("working_hours_to");
                                    int delivery_time_min1 = restaurantJson.getInt("delivery_time_min");
                                    int delivery_time_max1 = restaurantJson.getInt("delivery_time_max");
                                    int delivery_price1 = restaurantJson.getInt("delivery_price");
                                    double latitude1 = restaurantJson.getDouble("latitude");
                                    double longtitude1 = restaurantJson.getDouble("longitude");
                                    int is_top1 = restaurantJson.getInt("is_top");
                                    int status1 = restaurantJson.getInt("status");
                                    Restaurant restaurant = new Restaurant(id1,name1,logo1,image1,min_order_amount1,working_hours_from1,working_hours_to1,delivery_time_min1,delivery_time_max1,delivery_price1,latitude1,longtitude1,is_top1,status1,false);
                                    Order order = new Order(id,customer_id,name,restaurant_id,delivery_price,total_price,
                                            address,phone,comment,status,latitude,longitude,created_at,restaurant_name,restaurant);
                                    orderList.add(order);
                                }
                                if (orderList.size()<1){
                                    wishlist_empty_linear.setVisibility(View.VISIBLE);
                                }
                                OrderAdapter adapter = new OrderAdapter(getContext(),orderList);
                                recyclerview_order.setAdapter(adapter);
                            }else{
                                int error_code = jsonObject.getInt("error_code");
                                if (error_code == 1){
                                    Toast.makeText(getContext(), R.string.not_enough_parametrs, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 2){
                                    Toast.makeText(getContext(), R.string.phone_number_invalid, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 3){
                                    Toast.makeText(getContext(), R.string.too_many_tries, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 4){
                                    Toast.makeText(getContext(), R.string.unknow_query_error, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 5){
                                    Toast.makeText(getContext(), R.string.phone_number_not_found, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 6){
                                    Toast.makeText(getContext(), R.string.code_is_expired, Toast.LENGTH_SHORT).show();
                                }else if (error_code == 7){
                                    Toast.makeText(getContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                                }else if(error_code == 8){
                                    Toast.makeText(getContext(), R.string.token_not_found, Toast.LENGTH_SHORT).show();
                                }else if(error_code == 9){
                                    Toast.makeText(getContext(), R.string.customer_not_found, Toast.LENGTH_SHORT).show();
                                }else if(error_code == 10){
                                    Toast.makeText(getContext(), R.string.invalid_order_amount, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipe_refresh.setRefreshing(false);
                startActivity(new Intent(getContext(), NoInternetActivity.class));
                getActivity().finish();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}