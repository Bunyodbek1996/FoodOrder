package info.texnoman.foodorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import info.texnoman.foodorder.adapters.FavouriteRestaurantAdapter;
import info.texnoman.foodorder.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import info.texnoman.foodorder.HomeActivity;
import io.paperdb.Paper;

public class FavoriteFragment extends Fragment {
    public static List<Restaurant> favouriteRestaurantList;
    RecyclerView recycler_favourite;
    public static LinearLayout wishlist_empty_linear;
    private SwipeRefreshLayout swipe_refresh;
    View root = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (root == null){
            root = inflater.inflate(R.layout.fragment_favourite, container, false);
            favouriteRestaurantList = new ArrayList<>();
            initView(root);
        }
        return root;
    }

    private void initView(View root) {
        swipe_refresh = root.findViewById(R.id.swipe_refresh);
        recycler_favourite = root.findViewById(R.id.recycler_favourite);
        recycler_favourite.setLayoutManager(new LinearLayoutManager(getContext()));
        wishlist_empty_linear = root.findViewById(R.id.wishlist_empty_linear);
        HomeActivity.progressBar.setVisibility(View.VISIBLE);
        if (SplashActivity.city_id != null){
            int city_id = Integer.parseInt(SplashActivity.city_id);
            getRestaurant(city_id);
        }
        initSwipeRefresh(swipe_refresh);
    }

    private void getRestaurant(int city_id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_FAVORITE_URL+city_id+"/get_favourites",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipe_refresh.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                favouriteRestaurantList.clear();
                                HomeActivity.progressBar.setVisibility(View.GONE);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG.equals("uz")){
                                        name = restaurantJson.getString("name_uz");
                                    }else{
                                        name = restaurantJson.getString("name_ru");
                                    }
                                    String logo = restaurantJson.getString("logo");
                                    String image = restaurantJson.getString("image");
                                    int min_order_amount = restaurantJson.getInt("min_order_amount");
                                    String working_hours_from = restaurantJson.getString("working_hours_from");
                                    String working_hours_to = restaurantJson.getString("working_hours_to");
                                    int delivery_time_min = restaurantJson.getInt("delivery_time_min");
                                    int delivery_time_max = restaurantJson.getInt("delivery_time_max");
                                    int delivery_price = restaurantJson.getInt("delivery_price");
                                    double latitude = restaurantJson.getDouble("latitude");
                                    double longtitude = restaurantJson.getDouble("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = true;
                                    Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);

                                    String currentTime = SplashActivity.CURRENT_TIME;
                                    String timeFrom = working_hours_from;
                                    String timeTo = working_hours_to;

                                    String[] unitsFrom = timeFrom.split(":");
                                    int hourFrom = Integer.parseInt(unitsFrom[0]);
                                    int minuteFrom = Integer.parseInt(unitsFrom[1]);
                                    int from = 60 * hourFrom + minuteFrom;

                                    String[] unitsTo = timeTo.split(":");
                                    int hourTo = Integer.parseInt(unitsTo[0]);
                                    int minuteTo = Integer.parseInt(unitsTo[1]);
                                    int to = 60 * hourTo + minuteTo;

                                    String[] unitsCurrent = currentTime.split(":");
                                    int hourCurrent = Integer.parseInt(unitsCurrent[0]);
                                    int minuteCurrent = Integer.parseInt(unitsCurrent[1]);
                                    int current = 60 * hourCurrent + minuteCurrent;

                                    if (current >= from && current <= to){
                                        favouriteRestaurantList.add(restaurant);
                                    }
                                }
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG.equals("uz")){
                                        name = restaurantJson.getString("name_uz");
                                    }else{
                                        name = restaurantJson.getString("name_ru");
                                    }
                                    String logo = restaurantJson.getString("logo");
                                    String image = restaurantJson.getString("image");
                                    int min_order_amount = restaurantJson.getInt("min_order_amount");
                                    String working_hours_from = restaurantJson.getString("working_hours_from");
                                    String working_hours_to = restaurantJson.getString("working_hours_to");
                                    int delivery_time_min = restaurantJson.getInt("delivery_time_min");
                                    int delivery_time_max = restaurantJson.getInt("delivery_time_max");
                                    int delivery_price = restaurantJson.getInt("delivery_price");
                                    double latitude = restaurantJson.getDouble("latitude");
                                    double longtitude = restaurantJson.getDouble("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = true;
                                    Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);

                                    String currentTime = SplashActivity.CURRENT_TIME;
                                    String timeFrom = working_hours_from;
                                    String timeTo = working_hours_to;

                                    String[] unitsFrom = timeFrom.split(":");
                                    int hourFrom = Integer.parseInt(unitsFrom[0]);
                                    int minuteFrom = Integer.parseInt(unitsFrom[1]);
                                    int from = 60 * hourFrom + minuteFrom;

                                    String[] unitsTo = timeTo.split(":");
                                    int hourTo = Integer.parseInt(unitsTo[0]);
                                    int minuteTo = Integer.parseInt(unitsTo[1]);
                                    int to = 60 * hourTo + minuteTo;

                                    String[] unitsCurrent = currentTime.split(":");
                                    int hourCurrent = Integer.parseInt(unitsCurrent[0]);
                                    int minuteCurrent = Integer.parseInt(unitsCurrent[1]);
                                    int current = 60 * hourCurrent + minuteCurrent;

                                    if (current < from || current > to){
                                        favouriteRestaurantList.add(restaurant);
                                    }
                                }
                                if (favouriteRestaurantList.size()<1){
                                    wishlist_empty_linear.setVisibility(View.VISIBLE);
                                }else{
                                    wishlist_empty_linear.setVisibility(View.GONE);
                                }
                                FavouriteRestaurantAdapter adapter = new FavouriteRestaurantAdapter(getContext(),favouriteRestaurantList);
                                recycler_favourite.setAdapter(adapter);
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
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
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
    private void initSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getRestaurant(HomeFragment.city_id);
                    }
                }
        );
    }
}