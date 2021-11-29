package info.texnoman.foodorder.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import info.texnoman.foodorder.CartActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.CartProduct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static info.texnoman.foodorder.HomeActivity.progressBar;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    List<CartProduct> cartList;

    public CartAdapter(Context context, List<CartProduct> cartList) {
        this.context = context;
        this.cartList = cartList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final CartProduct product = cartList.get(position);
        Glide.with(context)
                .load(SplashActivity.BASE_URL + "/storage/product_images/" + product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.product_image);

        holder.product_name.setText(product.getName());
        holder.product_price.setText(product.getPrice() + "so'm");
        holder.product_desc.setText(product.getDescription() + "");
        holder.quantity.setText(product.getQuantity() + "");
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                minusFunction(product.getId(), holder.quantity, position, holder.progressBar);
            }
        });
        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                plusFunction(product.getId(), holder.quantity, position, holder.progressBar);
            }
        });

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                removeFunction(product.getId(), position, holder.progressBar);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image, btn_remove;
        TextView product_name, btn_plus, btn_minus, quantity, product_price, product_desc;
        RelativeLayout layout;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            btn_plus = itemView.findViewById(R.id.btn_plus);
            btn_minus = itemView.findViewById(R.id.btn_minus);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            quantity = itemView.findViewById(R.id.quantity);
            product_price = itemView.findViewById(R.id.product_price);
            product_desc = itemView.findViewById(R.id.product_description);
            layout = itemView.findViewById(R.id.linearlayout);
        }
    }

    private void minusFunction(int id, final TextView quantityText, final int position, final ProgressBar progressBar) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.DECREMENT_URL + id
                + "/cart_item_decrement",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                int quantity = data.getInt("quantity");
                                quantityText.setText(quantity + "");
                                cartList.get(position).setQuantity(quantity);
                                CartActivity.cartList.get(position).setQuantity(quantity);
                                int minusPrice = cartList.get(position).getPrice();
                                CartActivity.products_price -= minusPrice;
                                CartActivity.products_price_textview.setText(CartActivity.products_price + "");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "e: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context,  context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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

    private void plusFunction(int id, final TextView quantityText, final int position, final ProgressBar progressBar) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.ADD_TO_CART_URL + id + "/add_to_cart",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                int quantity = data.getInt("quantity");
                                quantityText.setText("" + quantity);
                                cartList.get(position).setQuantity(quantity);
                                CartActivity.cartList.get(position).setQuantity(quantity);
                                int plusPrice = cartList.get(position).getPrice();
                                CartActivity.products_price += plusPrice;
                                CartActivity.products_price_textview.setText(CartActivity.products_price + "");
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
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

    private void removeFunction(int id, final int position, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.REMOVE_URL + id + "/cart_item_delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                int removePrice = cartList.get(position).getQuantity() * cartList.get(position).getPrice();
                                CartActivity.products_price -= removePrice;
                                CartActivity.products_price_textview.setText(CartActivity.products_price + "");
                                removeItem(position);
                                Paper.init(context);
                                String count = "" + Paper.book().read("badge");
                                int nextCount = Integer.parseInt(count) - 1;
                                Paper.book().write("badge", "" + nextCount);
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
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

    private void removeItem(int position) {
        cartList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartList.size());
        if (cartList.size() < 1) {
            CartActivity.empty_cart.setVisibility(View.VISIBLE);
            RestaurantDetailsActivity.cart_linear.setVisibility(View.GONE);
            RestaurantDetailsActivity.badge_count.setVisibility(View.GONE);
        }
    }

}
