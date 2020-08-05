package com.example.socialgood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialgood.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{



    List<String> categories;
    OnClickListener clickListener;

    public CategoriesAdapter(List<String> categories, OnClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use layout inflator to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        // wrap it inside a View Holder and return it
        return new ViewHolder(todoView);
    }

    // responsible for binding data to a particular View Holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Grab item at the position
        String item = categories.get(position);
        // Bind the item into the specified view holder
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    //Container to provide easy access to views that represent each row in list
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        // Update the view inside of the view holder with this data
        public void bind(String item) {
            tvItem.setText(item);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // notifies the listener which position was long pressed
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            YoYo.with(Techniques.Wobble).repeat(1000).playOn(btnDelete);
        }
    }
}