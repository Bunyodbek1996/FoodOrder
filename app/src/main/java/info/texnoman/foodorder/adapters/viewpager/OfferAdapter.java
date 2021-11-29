package info.texnoman.foodorder.adapters.viewpager;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import info.texnoman.foodorder.models.Offer;

public class OfferAdapter extends PagerAdapter {
    ArrayList<Offer> offers;
    Context context;

    public OfferAdapter(ArrayList<Offer> offers, Context context) {
        this.offers = offers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
