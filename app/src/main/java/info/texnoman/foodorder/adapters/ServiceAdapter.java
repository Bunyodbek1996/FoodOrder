package info.texnoman.foodorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.texnoman.foodorder.CartActivity;
import info.texnoman.foodorder.HomeActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.ServiceFiltrActivity;
import info.texnoman.foodorder.SplashActivity;
import info.texnoman.foodorder.models.CartProduct;
import info.texnoman.foodorder.models.Service;
import io.paperdb.Paper;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    Context context;
    List<Service> serviceList;
    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Service service = serviceList.get(position);

        Glide.with(context)
                .load(SplashActivity.BASE_URL +"/storage/service_images/"+ service
                        .getImage()).into(holder.image_service);

        holder.text_service.setText(service.getName());
        holder.cardview_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("service_id",service.getId());
                ServiceFiltrActivity.service_id = service.getId();
                context.startActivity(intent);

            }
        });
        holder.text_service.bringToFront();

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image_service;
        TextView text_service;
        CardView cardview_service;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardview_service = itemView.findViewById(R.id.cardview_service);
            image_service = itemView.findViewById(R.id.image_service);
            text_service = itemView.findViewById(R.id.text_service);

        }
    }

}
