package info.texnoman.foodorder.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.texnoman.foodorder.HomeActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.ServiceFiltrActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.adapters.RestaurantAdapter;
import info.texnoman.foodorder.models.Restaurant;

import static info.texnoman.foodorder.fragments.HomeFragment.city_id;

public class SearchFragment extends Fragment {
    SearchView searchview;
    RequestQueue queue;
    public static List<Restaurant> restaurantList;
    public static RecyclerView recyclerViewRestaurant;
    LinearLayout linear_empty,root_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        initVars(root);

        return root;
    }

    private void initVars(View root) {
        root_layout = root.findViewById(R.id.root_layout);
        linear_empty = root.findViewById(R.id.linear_empty);
        recyclerViewRestaurant = root.findViewById(R.id.recyclerview_restaurant);
        recyclerViewRestaurant.setHasFixedSize(true);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        queue = Volley.newRequestQueue(getContext());
        restaurantList = new ArrayList<>();
        searchview  = root.findViewById(R.id.searchview);
        root_layout.requestFocus();
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length()>2){
                    getRestaurant(s);
                }
                return false;
            }
        });
        searchview.setIconifiedByDefault(true);
        searchview.setFocusable(true);
        searchview.setIconified(false);
        searchview.requestFocusFromTouch();
    }
    private void getRestaurant(String keyword) {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.SEARCH_RESTAURANT_URL+city_id+"/search_restaurants",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        restaurantList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG != null){
                                        if ( SplashActivity.LANG.equals("uz")){
                                            name = restaurantJson.getString("name_uz");
                                        }else{
                                            name = restaurantJson.getString("name_ru");
                                        }
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
                                    boolean heart = restaurantJson.getBoolean("heart");
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
                                        restaurantList.add(restaurant);
                                    }
                                }
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG != null){
                                        if ( SplashActivity.LANG.equals("uz")){
                                            name = restaurantJson.getString("name_uz");
                                        }else{
                                            name = restaurantJson.getString("name_ru");
                                        }
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
                                    boolean heart = restaurantJson.getBoolean("heart");
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
                                        restaurantList.add(restaurant);
                                    }
                                }
                                RestaurantAdapter adapter = new RestaurantAdapter(getContext(),restaurantList);
                                recyclerViewRestaurant.setAdapter(adapter);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            HomeActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (restaurantList.size()>0){
                            linear_empty.setVisibility(View.GONE);
                        }else{
                            linear_empty.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
                return map;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("keyword", keyword);
                return new JSONObject(param).toString().getBytes();
            }
        };
        queue.add(request);
    }
}