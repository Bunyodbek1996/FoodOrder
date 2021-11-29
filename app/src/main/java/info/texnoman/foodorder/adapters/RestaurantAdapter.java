package info.texnoman.foodorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.model.LatLng;

import info.texnoman.foodorder.PlaceOrderActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.ServiceFiltrActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.Restaurant;
import io.paperdb.Paper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.texnoman.foodorder.SplashActivity.TOKEN;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    Context context;
    List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Glide.with(context)
                .load(SplashActivity.BASE_URL + "/storage/restaurant_images/" + restaurantList.get(position).getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.image_restaurant);
//        Glide.with(context)
//                .load(SplashActivity.BASE_URL + "/storage/restaurant_logos/" + restaurantList.get(position).getLogo())
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .into(holder.image_logo);

        holder.image_logo.bringToFront();
        holder.close_tv.bringToFront();
        holder.name.setText(restaurantList.get(position).getName());
        holder.delivery_time.setText(restaurantList.get(position).getDelivery_time_min() + " - "
                + restaurantList.get(position).getDelivery_time_max() + " " + context.getString(R.string.min));
        holder.min_order_price.setText(restaurantList.get(position).getMin_order_amount() + " " + context.getString(R.string.sum));
        holder.delivery_price.setText(restaurantList.get(position).getDelivery_price() + " " + context.getString(R.string.sum));
        holder.working_hours_tv.setText("" + restaurantList.get(position).getWorking_hours_from() + "-"
                + restaurantList.get(position).getWorking_hours_to() + "");
        holder.linear_time.bringToFront();
        if (restaurantList.get(position).isHeart()) {
            holder.image_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_accent_24dp));
        }
        holder.image_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavorite(restaurantList.get(position).getId(), holder.image_favorite);
            }
        });
        if (restaurantList.get(position).getId() == -1) {
            Glide.with(context).load(context.getResources().getIdentifier("default_image", "drawable", context.getPackageName())).into(holder.image_restaurant);
        } else {

            String currentTime = SplashActivity.CURRENT_TIME;
            String timeFrom = restaurantList.get(position).getWorking_hours_from(); //mm:ss
            String timeTo = restaurantList.get(position).getWorking_hours_to(); //mm:ss

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


            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (restaurantList.get(position).getStatus() == 1 && current >= from && current <= to) {
                        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                        intent.putExtra("restaurantd_id", restaurantList.get(position).getId() + "");
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, R.string.res_is_close, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (restaurantList.get(position).getStatus() == 0) {
                holder.close_tv.setVisibility(View.VISIBLE);
            }
            if (current < from || current > to) {
                holder.close_tv.setVisibility(View.VISIBLE);
            }

            double dis = CalculationByDistance(restaurantList.get(position).getLatitude() + "", restaurantList.get(position).getLongtitude() + "");
            getPrices(dis, holder.delivery_price);

        }
        if(restaurantList.get(position).getId() == -1){
            holder.working_hours_tv.setVisibility(View.INVISIBLE);
            holder.close_tv.setVisibility(View.INVISIBLE);
            holder.name.setVisibility(View.GONE);
            holder.delivery_time.setVisibility(View.INVISIBLE);
            holder.min_order_price.setVisibility(View.INVISIBLE);
            holder.delivery_price.setVisibility(View.INVISIBLE);
            holder.image_logo.setVisibility(View.INVISIBLE);
            holder.image_favorite.setVisibility(View.INVISIBLE);
            holder.linear_time.setVisibility(View.INVISIBLE);
        }
    }

    public double CalculationByDistance(String resLa, String resLo) {
        Paper.init(context);
        String res_lat = resLa;
        String res_lon = resLo;
        LatLng StartP = new LatLng(Double.parseDouble(res_lat), Double.parseDouble(res_lon));
        LatLng EndP = new LatLng(Double.parseDouble(SplashActivity.LATITUDE), Double.parseDouble(SplashActivity.LONGITUDE));
        int Radius = 6371;
        // radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + " KM " + kmInDec + " Meter " + meterInDec);
        return Radius * c;
    }

    private void getPrices(final double distance, final TextView delivery_price) {
        String min_distance = ServiceFiltrActivity.min_distance;
        String min_distance_price = ServiceFiltrActivity.min_distance_price;
        String one_km_price = ServiceFiltrActivity.one_km_price;
        double price = 0;
        if (min_distance != null && min_distance_price != null && one_km_price != null) {
            if (distance > (Double.parseDouble(min_distance) / 1000)) {
                price = Double.parseDouble(min_distance_price)
                        + (distance - (Double.parseDouble(min_distance) / 1000)) * Double.parseDouble(one_km_price);
            } else {
                price = Double.parseDouble(min_distance_price);
            }
            price = price / 100;
            int price_int = (int) price;
            price_int = price_int * 100;
            delivery_price.setText(context.getString(R.string.min) + ". " + price_int + " " + context.getString(R.string.sum));
        }
    }

    //    private void getPrices(final double distance, final TextView delivery_price) {
//        RequestQueue queue = Volley.newRequestQueue(context);
//        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_PRICES_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            boolean success = jsonObject.getBoolean("success");
//                            if (success) {
//                                JSONObject data = jsonObject.getJSONObject("data");
//                                String min_distance = data.getString("min_distance");
//                                String min_distance_price = data.getString("min_price");
//                                String one_km_price = data.getString("one_km_price");
//                                double price = 0;
//                                if (min_distance != null && min_distance_price != null && one_km_price != null) {
//                                    if (distance > (Double.parseDouble(min_distance) / 1000)) {
//                                        price = Double.parseDouble(min_distance_price)
//                                                + (distance - (Double.parseDouble(min_distance) / 1000)) * Double.parseDouble(one_km_price);
//                                    } else {
//                                        price = Double.parseDouble(min_distance_price);
//                                    }
//                                    price = price / 100;
//                                    int price_int = (int) price;
//                                    price_int = price_int * 100;
//                                    delivery_price.setText(context.getString(R.string.min) + ". " + price_int + " " + context.getString(R.string.sum));
//                                }
//                            } else {
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("Content-Type", "application/json");
//                map.put("auth_token", SplashActivity.TOKEN);
//                return map;
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(request);
//    }
    private void setFavorite(int id, final ImageView image_favorite) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.FAVORITE_URL + id + "/set_favourite",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String data = jsonObject.getString("data");
                                if (data.equals("Unset")) {
                                    image_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_accent_border_icon));
                                } else if (data.equals("Set")) {
                                    image_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_accent_24dp));
                                }
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", TOKEN);
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
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, delivery_time, min_order_price, delivery_price, close_tv, working_hours_tv;
        ImageView image_restaurant, image_favorite, image_logo;
        LinearLayout linearLayout, linear_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            working_hours_tv = itemView.findViewById(R.id.working_hours_tv);
            close_tv = itemView.findViewById(R.id.close_tv);
            name = itemView.findViewById(R.id.name);
            delivery_time = itemView.findViewById(R.id.delivery_time);
            min_order_price = itemView.findViewById(R.id.min_order_price);
            delivery_price = itemView.findViewById(R.id.delivery_price);
            image_logo = itemView.findViewById(R.id.image_logo);
            image_restaurant = itemView.findViewById(R.id.image_restaurant);
            image_favorite = itemView.findViewById(R.id.image_favorite);
            linearLayout = itemView.findViewById(R.id.linearlayout);
            linear_time = itemView.findViewById(R.id.linear_time);
        }
    }
}
