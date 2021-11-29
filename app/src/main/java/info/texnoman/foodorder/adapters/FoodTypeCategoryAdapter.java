package info.texnoman.foodorder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import info.texnoman.foodorder.R;
import info.texnoman.foodorder.RestaurantDetailsActivity;
import info.texnoman.foodorder.models.FoodType;

import java.util.List;

public class FoodTypeCategoryAdapter extends RecyclerView.Adapter<FoodTypeCategoryAdapter.ViewHolder> {
    int active = 1;
    Context context;
    List<FoodType> foodTypeList;
    public FoodTypeCategoryAdapter(Context context, List<FoodType> foodTypeList) {
        this.context = context;
        this.foodTypeList = foodTypeList;
    }
    public FoodTypeCategoryAdapter(Context context, List<FoodType> foodTypeList,int active) {
        this.context = context;
        this.foodTypeList = foodTypeList;
        this.active = active;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.foodtype_list_item_horizont,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        FoodType foodType = foodTypeList.get(position);
        holder.category_name.setText(foodType.getName());
        if (position == active){
            holder.category_name.setTextColor(Color.WHITE);
            holder.cardView.setBackground(context.getResources().getDrawable(R.drawable.background_category_green));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantDetailsActivity.recycler_category.getLayoutManager().scrollToPosition(position);
                FoodTypeCategoryAdapter categoryAdapter = new FoodTypeCategoryAdapter(
                        context, foodTypeList,position);
                RestaurantDetailsActivity.recycler_category.setAdapter(categoryAdapter);
                RestaurantDetailsActivity.recycler_product.getLayoutManager().scrollToPosition(position);
            }
        });

    }



    @Override
    public int getItemCount() {
        return foodTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView category_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }



}
