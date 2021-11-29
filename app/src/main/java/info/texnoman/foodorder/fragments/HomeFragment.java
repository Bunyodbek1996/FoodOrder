package info.texnoman.foodorder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
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
import info.texnoman.foodorder.ServiceFiltrActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.adapters.CategoryAdapter;
import info.texnoman.foodorder.adapters.OfferAdapter;
import info.texnoman.foodorder.adapters.RestaurantAdapter;
import info.texnoman.foodorder.models.Category;
import info.texnoman.foodorder.models.Offer;
import info.texnoman.foodorder.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.texnoman.foodorder.HomeActivity;
import io.paperdb.Paper;

public class HomeFragment extends Fragment {
    private static final int REQUEST_LOCATION = 1;
    public static List<Restaurant> restaurantList;
    String latitude = "40.374443", longitude = "71.783475";
    public static TextView breadcrumb;
    RecyclerView recyclerViewOffer, recyclerViewCategory;
    public static RecyclerView recyclerViewRestaurant;
    public static int city_id = 1;
    double distance = 999999;
    private int index = 0;
    public static List<Category> categoryList;
    public static List<Offer> offerList;
    private SwipeRefreshLayout swipe_refresh;
    View root = null;
    RequestQueue queue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_home, container, false);
            queue = Volley.newRequestQueue(getContext());
            Paper.init(getContext());
            initVars();
            initViews(root);
            initCategory();
            getCities();
        }
        return root;
    }

    private void initVars() {
        restaurantList = new ArrayList<>();
        categoryList = new ArrayList<>();
        offerList = new ArrayList<>();

    }


    private void initViews(View root) {
        swipe_refresh = root.findViewById(R.id.swipe_refresh);
        recyclerViewOffer = root.findViewById(R.id.recyclerview_offer);
        recyclerViewCategory = root.findViewById(R.id.recyclerview_category);
        recyclerViewRestaurant = root.findViewById(R.id.recyclerview_restaurant);
        breadcrumb = root.findViewById(R.id.breadcrumb);
        HomeActivity.progressBar.setVisibility(View.VISIBLE);

        latitude = SplashActivity.LATITUDE;
        longitude = SplashActivity.LONGITUDE;


        initRecyclerViewRestaurant(recyclerViewRestaurant);
        initSwipeRefresh(swipe_refresh);

        initRecylersTillLoad();
    }

    private void initRecylersTillLoad() {
        Category category = new Category(-1, "icon", "");
        categoryList.add(category);
        categoryList.add(category);
        categoryList.add(category);
        categoryList.add(category);
        categoryList.add(category);

        recyclerViewCategory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewCategory.setLayoutManager(layoutManager);
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList);
        recyclerViewCategory.setAdapter(adapter);

        Offer offer = new Offer(-1, "", "", "", "", "", "-1");
        offerList.add(offer);
        offerList.add(offer);
        recyclerViewOffer.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewOffer.setLayoutManager(layoutManager1);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewOffer);
        final OfferAdapter adapter1 = new OfferAdapter(getContext(), offerList);
        recyclerViewOffer.setAdapter(adapter1);


        Restaurant restaurant = new Restaurant(-1, "", "", "", 1, "", "", 1, 1, 1, 1, 1, 1, 1, true);
        restaurantList.add(restaurant);
        restaurantList.add(restaurant);
        restaurantList.add(restaurant);
        RestaurantAdapter adapter2 = new RestaurantAdapter(getContext(), restaurantList);
        recyclerViewRestaurant.setAdapter(adapter2);

    }

    private void initSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        categoryList.clear();
                        restaurantList.clear();
                        initCategory();
                        getCities();
                    }
                }
        );
    }

    private void initRecyclerViewRestaurant(RecyclerView recyclerViewRestaurant) {
        recyclerViewRestaurant.setHasFixedSize(true);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void getCities() {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.CITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject cityJson = jsonArray.getJSONObject(i);
                                    String lat = cityJson.getString("latitude");
                                    String lon = cityJson.getString("longitude");
                                    double distance2 = distance(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (distance > distance2) {
                                        distance = distance2;
                                        city_id = cityJson.getInt("id");
                                    }
                                }
                                Paper.book().write("city_id", city_id + "");
                                getRestaurant(city_id);
                                initOffers();
                            } else {
                                int error_code = jsonObject.getInt("error_code");
                                if (error_code == 1) {
                                    Toast.makeText(getContext(), R.string.not_enough_parametrs, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 2) {
                                    Toast.makeText(getContext(), R.string.phone_number_invalid, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 3) {
                                    Toast.makeText(getContext(), R.string.too_many_tries, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 4) {
                                    Toast.makeText(getContext(), R.string.unknow_query_error, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 5) {
                                    Toast.makeText(getContext(), R.string.phone_number_not_found, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 6) {
                                    Toast.makeText(getContext(), R.string.code_is_expired, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 7) {
                                    Toast.makeText(getContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 10) {
                                    Toast.makeText(getContext(), R.string.invalid_order_amount, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(getContext(), NoInternetActivity.class));
                getActivity().finish();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void getRestaurant(final int city_id) {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.RESTAURANTS_URL + city_id + "/" + ServiceFiltrActivity.service_id + "/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipe_refresh.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                restaurantList.clear();
                                HomeActivity.progressBar.setVisibility(View.GONE);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG != null) {
                                        if (SplashActivity.LANG.equals("uz")) {
                                            name = restaurantJson.getString("name_uz");
                                        } else {
                                            name = restaurantJson.getString("name_ru");
                                        }
                                    } else {
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
                                    Restaurant restaurant = new Restaurant(id, name, logo, image, min_order_amount, working_hours_from, working_hours_to, delivery_time_min, delivery_time_max, delivery_price, latitude, longtitude, is_top, status, heart);


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

                                    if (current >= from && current <= to) {
                                        restaurantList.add(restaurant);
                                    }
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject restaurantJson = jsonArray.getJSONObject(i);
                                    int id = restaurantJson.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG != null) {
                                        if (SplashActivity.LANG.equals("uz")) {
                                            name = restaurantJson.getString("name_uz");
                                        } else {
                                            name = restaurantJson.getString("name_ru");
                                        }
                                    } else {
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
                                    Restaurant restaurant = new Restaurant(id, name, logo, image, min_order_amount, working_hours_from, working_hours_to, delivery_time_min, delivery_time_max, delivery_price, latitude, longtitude, is_top, status, heart);

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

                                    if (current < from || current > to) {
                                        restaurantList.add(restaurant);
                                    }
                                }
                                RestaurantAdapter adapter = new RestaurantAdapter(getContext(), restaurantList);
                                recyclerViewRestaurant.setAdapter(adapter);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            HomeActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipe_refresh.setRefreshing(false);
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    private void initCategory() {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.CATEGORY_URL + ServiceFiltrActivity.service_id + "/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                categoryList.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    categoryList.add(new Category(0, "", getString(R.string.all)));
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject categoryJSON = jsonArray.getJSONObject(i);
                                    int id = categoryJSON.getInt("id");
                                    String name = "";
                                    if (SplashActivity.LANG != null) {
                                        if (SplashActivity.LANG.equals("uz")) {
                                            name = categoryJSON.getString("name_uz");
                                        } else {
                                            name = categoryJSON.getString("name_ru");
                                        }
                                    } else {
                                        name = categoryJSON.getString("name_ru");
                                    }
                                    String icon = categoryJSON.getString("icon");
                                    int status = categoryJSON.getInt("status");
                                    Category category = new Category(id, icon, name);
                                    if (status == 1) {
                                        categoryList.add(category);
                                    }
                                }
                                recyclerViewCategory.setHasFixedSize(true);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                                recyclerViewCategory.setLayoutManager(layoutManager);
                                CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList);
                                recyclerViewCategory.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void initOffers() {
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.OFFER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                offerList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject offerJSON = jsonArray.getJSONObject(i);
                                    int id = offerJSON.getInt("id");
                                    String name, text;
                                    if (SplashActivity.LANG != null) {
                                        if (SplashActivity.LANG.equals("uz")) {
                                            name = offerJSON.getString("name_uz");
                                            text = offerJSON.getString("text_uz");
                                        } else {
                                            name = offerJSON.getString("name_ru");
                                            text = offerJSON.getString("text_ru");
                                        }
                                    } else {
                                        name = offerJSON.getString("name_ru");
                                        text = offerJSON.getString("text_ru");
                                    }
                                    String image = offerJSON.getString("image");
                                    String link = offerJSON.getString("link");
                                    String type = offerJSON.getString("type");
                                    int status = offerJSON.getInt("status");
                                    String restaurant_id = offerJSON.getString("restaurant_id");
                                    Offer offer = new Offer(id, name, text, image, type, link, restaurant_id);
                                    if (status == 1) {
                                        offerList.add(offer);
                                    }
                                }
                                recyclerViewOffer.setHasFixedSize(true);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                layoutManager.setOrientation(RecyclerView.HORIZONTAL);
                                recyclerViewOffer.setLayoutManager(layoutManager);

                                final OfferAdapter adapter = new OfferAdapter(getContext(), offerList);
                                recyclerViewOffer.setAdapter(adapter);
                                final Handler handler = new Handler();
                                final Runnable r = new Runnable() {
                                    public void run() {
                                        index++;
                                        if (index == adapter.getItemCount()) index = 0;
                                        recyclerViewOffer.smoothScrollToPosition(index);
                                        handler.postDelayed(this, 4000);
                                    }
                                };
                                handler.postDelayed(r, 4000);
                                if (offerList.size() < 1) {
                                    recyclerViewOffer.setVisibility(View.GONE);
                                }
                            } else {
                                int error_code = jsonObject.getInt("error_code");
                                if (error_code == 8 || error_code == 9) {
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", city_id + "");
                return new JSONObject(map).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
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