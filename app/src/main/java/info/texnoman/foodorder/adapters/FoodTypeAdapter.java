package info.texnoman.foodorder.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import info.texnoman.foodorder.R;
import info.texnoman.foodorder.models.FoodType;
import info.texnoman.foodorder.models.Product;

import java.util.List;
import java.util.Objects;

public class FoodTypeAdapter extends RecyclerView.Adapter<FoodTypeAdapter.ViewHolder> {
    Context context;
    List<FoodType> foodTypeList;
    RecyclerView recyclerview_category;
    Activity activity;
    public FoodTypeAdapter(Activity activity,Context context, List<FoodType> foodTypeList,RecyclerView recyclerView) {
        this.context = context;
        this.foodTypeList = foodTypeList;
        recyclerview_category = recyclerView;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        FoodType foodType = foodTypeList.get(position);
        RecyclerView recyclerView = holder.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Product> productList = foodType.getProductList();
        ProductAdapter productAdapter = new ProductAdapter(activity,context,productList);
        recyclerView.setAdapter(productAdapter);

        holder.foodtype_name.setText(foodType.getName());
        Objects.requireNonNull(recyclerview_category.getLayoutManager()).scrollToPosition(position);

    }



    @Override
    public int getItemCount() {
        return foodTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;
        TextView foodtype_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            foodtype_name = itemView.findViewById(R.id.foodtype_name);
        }
    }



}
