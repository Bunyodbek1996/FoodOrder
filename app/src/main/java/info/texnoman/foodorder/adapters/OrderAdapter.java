package info.texnoman.foodorder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import info.texnoman.foodorder.CartActivity;
import info.texnoman.foodorder.OrderDetailsActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.Order;
import io.paperdb.Paper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    List<Order> orderList;
    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Order order = orderList.get(position);

        Glide.with(context)
                .load(SplashActivity.BASE_URL +"/storage/restaurant_images/"+ order.getRestaurant().getImage()).into(holder.restaurant_image);

        holder.restaurant_name_textview.setText(order.getRestaurant_name());
        holder.order_id_textView.setText(context.getString(R.string.order_id)+order.getId());

        int total = order.getTotal_price()+order.getDelivery_price();
        holder.order_price_textview.setText(total+" "+context.getString(R.string.sum));

        int status = order.getStatus();
        if (status == -1){
            holder.order_status_textView.setText(R.string.order_pending);
        }
        if (status == 1){
            holder.order_status_textView.setText(R.string.order_send);
        }
        if (status == 2){
            holder.order_status_textView.setText(R.string.order_received);
        }
        if (status == 22){
            holder.order_status_textView.setText(R.string.order_received);
        }
        if (status == -2){
            holder.order_status_textView.setText(R.string.cancelled_by_restaurant);
            holder.cancel_order_tv.setText(context.getString(R.string.cancelled));
        }
        if (status == 3){
            holder.order_status_textView.setText(R.string.order_in_delivery_man);
        }
        if (status == 4){
            holder.order_status_textView.setText(R.string.order_at_way);
        }
        if (status == -4 || status == -5){
            holder.order_status_textView.setText(R.string.cancelled_by_customer);
            holder.cancel_order_tv.setText(context.getString(R.string.cancelled));
        }
        if (status == 5){
            holder.order_status_textView.setText(R.string.received_by_customer);
            holder.cancel_order_tv.setText(context.getString(R.string.received));
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("id",order.getId());
                context.startActivity(intent);
            }
        });
        holder.cancel_order_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.getStatus() != -2 && order.getStatus() != -4 && order.getStatus() != 5 && order.getStatus() != -5){
                    showNotifFirst(order.getId());
                }
            }
        });
    }

    private void showNotifFirst(final int id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(R.string.do_you_cancel)
            .setPositiveButton(context.getString(R.string.positive_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteOrder(id);
                }
            });
        alert.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        ImageView restaurant_image;
        TextView restaurant_name_textview,order_id_textView,order_status_textView,order_price_textview,cancel_order_tv;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativelayout);
            restaurant_image = itemView.findViewById(R.id.restaurant_image);
            restaurant_name_textview = itemView.findViewById(R.id.restaurant_name_textview);
            order_id_textView = itemView.findViewById(R.id.order_id_textview);
            order_status_textView = itemView.findViewById(R.id.order_status_textview);
            order_price_textview = itemView.findViewById(R.id.orders_price_textview);
            cancel_order_tv = itemView.findViewById(R.id.cancel_order_tv);
        }
    }

    private void deleteOrder(final int id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.DELETE_ORDER_URL+id+"/cancel",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){

                                Toast.makeText(context, R.string.cancelled, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deleteOrder(id);
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
