package info.texnoman.foodorder.adapters;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;


import info.texnoman.foodorder.ProductDetailActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.Product;
import info.texnoman.foodorder.util.CircleAnimationUtil;
import io.paperdb.Paper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    List<Product> productList;
    Activity activity;
    int id;
    public ProductAdapter(Activity activity, Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item_sub,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Product product = productList.get(position);
        Glide.with(context)
                .load(SplashActivity.BASE_URL +"/storage/product_images/"+ productList.get(position).getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.product_image.setVisibility(View.GONE);
                        holder.default_image.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.product_image.setVisibility(View.VISIBLE);
                        holder.default_image.setVisibility(View.GONE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.product_image);
        holder.product_name.setText(product.getName());
        holder.product_price.setText(product.getPrice()+" "+context.getString(R.string.sum));
        String desc = product.getDescription()+"";
        if (desc.length()>40){
            holder.product_description.setText(desc.substring(0,37)+"...");
        }else{
            holder.product_description.setText(product.getDescription()+"");
        }
        final Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("product",productList.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(intent);
            }
        });
        holder.add_to_cart_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = productList.get(position).getId();
                makeFlyAnimation(holder.product_image);
                Glide.with(context)
                        .load(SplashActivity.BASE_URL +"/storage/product_images/"+ productList.get(position).getImage())
                        .into(holder.product_image);
                Paper.init(context);
                String res_id = Paper.book().read("restaurant_id");
                if (res_id != null){
                    if(res_id.equals(RestaurantDetailsActivity.res_id)){
                        add_to_cart_function();
                    }else{
                        add_to_cart_other_res();
                    }
                }else{
                    add_to_cart_function();
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView add_to_cart_iv;
        ImageView product_image;
        TextView product_name;
        TextView product_price;
        TextView product_description;
        RelativeLayout layout;
        ImageView default_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            default_image = itemView.findViewById(R.id.default_image);
            add_to_cart_iv = itemView.findViewById(R.id.add_to_cart_iv);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_description = itemView.findViewById(R.id.product_description);
            layout = itemView.findViewById(R.id.linearlayout);
        }
    }
    private void makeFlyAnimation(final ImageView targetView) {


        LinearLayout destView = activity.findViewById(R.id.cart_image_linear);

        new CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(1000).setDestView(destView).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                targetView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }


    private void add_to_cart_other_res() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.notification)
                .setMessage(R.string.cart_delete_other_res)
                .setPositiveButton(context.getString(R.string.positive_button_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RestaurantDetailsActivity.dialog.show();
                        sendRequestForClearCart();
                    }
                }).setNegativeButton(context.getString(R.string.negative_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void sendRequestForClearCart() {
        RequestQueue queue = Volley.newRequestQueue(context);
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
                                changeBadge(0);
                            }else{
                                RestaurantDetailsActivity.dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            RestaurantDetailsActivity.dialog.dismiss();
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

    private void changeBadge(int i) {
        if (i != 0) {
            int countNext = i;
            RestaurantDetailsActivity.badge_count.setVisibility(View.VISIBLE);
            RestaurantDetailsActivity.badge_count.setText(countNext + "");
            RestaurantDetailsActivity.cart_linear.setVisibility(View.VISIBLE);
            RestaurantDetailsActivity.bottom_badge_count.setText(context.getString(R.string.cart) + "(" + countNext + ")");
            Paper.book().write("badge", "" + countNext);
        } else {
            RestaurantDetailsActivity.badge_count.setVisibility(View.GONE);
            RestaurantDetailsActivity.badge_count.setText("");
            RestaurantDetailsActivity.cart_linear.setVisibility(View.GONE);
            Paper.book().write("badge", "0");
        }
    }

    private void add_to_cart_function() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.ADD_TO_CART_URL +id+ "/add_to_cart",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        RestaurantDetailsActivity.dialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                int cart_counter = data.getInt("cart_counter");
                                Paper.init(context);
                                Paper.book().write("restaurant_id", "" + RestaurantDetailsActivity.res_id);
                                Paper.book().write("res_lat", "" + RestaurantDetailsActivity.latitude);
                                Paper.book().write("res_lon", "" + RestaurantDetailsActivity.longitude);
                                changeBadge(cart_counter);
                            }else{
                                RestaurantDetailsActivity.dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            RestaurantDetailsActivity.dialog.dismiss();
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
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> quantity = new HashMap<>();
                quantity.put("quantity", "1");
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

}
