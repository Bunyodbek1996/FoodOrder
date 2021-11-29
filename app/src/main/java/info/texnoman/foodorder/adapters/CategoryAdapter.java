package info.texnoman.foodorder.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import info.texnoman.foodorder.HomeActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.ServiceFiltrActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.fragments.HomeFragment;
import info.texnoman.foodorder.models.Category;
import info.texnoman.foodorder.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    List<Category> categoryList;
    ProgressDialog dialog;

    public CategoryAdapter(Context context, List<Category> categoriesList) {
        this.context = context;
        this.categoryList = categoriesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (categoryList.get(position).getName().length()>12){
            holder.text_category.setText(categoryList.get(position).getName().substring(0,9)+"...");
        }else{
            holder.text_category.setText(categoryList.get(position).getName());
        }
        holder.text_category.bringToFront();
        Glide.with(context)
                .load(SplashActivity.BASE_URL +"/storage/category_icons/"+ categoryList.get(position)
                        .getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.image_category);
        if(categoryList.get(position).getId() == 0){
            Glide.with(context).load(context.getResources().getIdentifier("all_food", "drawable", context.getPackageName())).into(holder.image_category);
        }
        if(categoryList.get(position).getId() == -1){
            Glide.with(context).load(context.getResources().getIdentifier("default_image", "drawable", context.getPackageName())).into(holder.image_category);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryList.get(position).getId() != 0){
                    HomeFragment.breadcrumb.setText(categoryList.get(position).getName());
                    getRestaurant(categoryList.get(position).getId());
                }else{
                    HomeFragment.breadcrumb.setText(categoryList.get(position).getName());
                    getRestaurantAll(HomeFragment.city_id);
                }
            }
        });
    }

    private void getRestaurant(int cat_id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.RESTAURANTS_URL+HomeFragment.city_id+"/get/"+cat_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                HomeFragment.restaurantList.clear();
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
                                    double latitude = restaurantJson.getInt("latitude");
                                    double longtitude = restaurantJson.getInt("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = restaurantJson.getBoolean("heart");

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
                                        Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);
                                        HomeFragment.restaurantList.add(restaurant);
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
                                    double latitude = restaurantJson.getInt("latitude");
                                    double longtitude = restaurantJson.getInt("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = restaurantJson.getBoolean("heart");

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
                                        Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);
                                        HomeFragment.restaurantList.add(restaurant);
                                    }
                                }
                                RestaurantAdapter adapter = new RestaurantAdapter(context,HomeFragment.restaurantList);
                                HomeFragment.recyclerViewRestaurant.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_category;
        ImageView image_category;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_category = itemView.findViewById(R.id.text_category);
            image_category = itemView.findViewById(R.id.image_category);
            cardView = itemView.findViewById(R.id.cardview_category);
        }
    }


    private void getRestaurantAll(final int city_id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.RESTAURANTS_URL + city_id + "/" + ServiceFiltrActivity.service_id + "/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                HomeFragment.restaurantList.clear();
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
                                    double latitude = restaurantJson.getInt("latitude");
                                    double longtitude = restaurantJson.getInt("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = restaurantJson.getBoolean("heart");

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
                                        Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);
                                        HomeFragment.restaurantList.add(restaurant);
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
                                    double latitude = restaurantJson.getInt("latitude");
                                    double longtitude = restaurantJson.getInt("longitude");
                                    int is_top = restaurantJson.getInt("is_top");
                                    int status = restaurantJson.getInt("status");
                                    boolean heart = restaurantJson.getBoolean("heart");

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
                                        Restaurant restaurant = new Restaurant(id,name,logo,image,min_order_amount,working_hours_from,working_hours_to,delivery_time_min,delivery_time_max,delivery_price,latitude,longtitude,is_top,status,heart);
                                        HomeFragment.restaurantList.add(restaurant);
                                    }
                                }
                                RestaurantAdapter adapter = new RestaurantAdapter(context,HomeFragment.restaurantList);
                                HomeFragment.recyclerViewRestaurant.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
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
}
