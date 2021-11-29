package info.texnoman.foodorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.FoodType;
import info.texnoman.foodorder.models.Offer;
import info.texnoman.foodorder.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.texnoman.foodorder.SplashActivity.LANG;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    Context context;
    List<Offer> offerList;

    public OfferAdapter(Context context, List<Offer> offerList) {
        this.context = context;
        this.offerList = offerList;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offer_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(context)
                .load(SplashActivity.BASE_URL +"/storage/offer_images/"+ offerList.get(position)
                        .getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.image_offer);
        if (offerList.get(position).getId() == -1){
            Glide.with(context).load(context.getResources().getIdentifier("default_image", "drawable", context.getPackageName())).into(holder.image_offer);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offerList.get(position).getType().equals("restaurant")){
                    getRestaurantDeatils(offerList.get(position).getRestaurant_id()+"");
                }else if (offerList.get(position).getType().equals("link")){
                    String link = offerList.get(position).getLink();
                    if (!link.equals("") && link != null){
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(link));
                        context.startActivity(i);
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image_offer;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_offer = itemView.findViewById(R.id.image_offer);
            linearLayout = itemView.findViewById(R.id.linearlayout);
        }
    }


    private void getRestaurantDeatils(String res_id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.RESTAURANTS_DETAILS_URL+res_id+"/get",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                jsonParser(jsonObject,res_id);
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
    private void jsonParser(JSONObject jsonObject,String res_id) {
        JSONObject data = null;
        try {
            data = jsonObject.getJSONObject("data");
            JSONObject restaurantJson = data.getJSONObject("restaurant");
            int status = restaurantJson.getInt("status");
            if (status == 1){
                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                intent.putExtra("restaurantd_id",res_id);
                context.startActivity(intent);
            }else{
                Toast.makeText(context, ""+context.getString(R.string.res_is_close), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
