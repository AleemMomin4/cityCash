package com.assignment.citycash.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.assignment.citycash.Interface.ItemClickListener;
import com.assignment.citycash.activity.ItemDetails;
import com.assignment.citycash.constant.Constant;
import com.assignment.citycash.model.ItemModel;
import com.assignment.citycash.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {

    private List<ItemModel> itemModels;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName,categoryName,qty,price;
        public ImageView img;
        public ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);
            img =  view.findViewById(R.id.img);
            itemName =  view.findViewById(R.id.item_name);
            categoryName =  view.findViewById(R.id.category_name);
            qty =  view.findViewById(R.id.qty);
            price =  view.findViewById(R.id.price);

            context = view.getContext();
            view.setTag(view);
            view.setOnClickListener(this);

        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());

        }
    }

    public ItemListAdapter(Context ctx, List<ItemModel> itemModels) {
        this.itemModels = itemModels;
        this.context = ctx;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        final ItemModel item = itemModels.get(position);
        holder.itemName.setText(item.getName());
        holder.categoryName.setText(item.getCategory_name());
        holder.qty.setText("QTY: ".concat(item.getQty()));
        holder.price.setText("₹ ".concat(item.getPrice()));

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_gallery)
                .error(R.drawable.ic_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(item.getImage())
                .apply(options)
                .into(holder.img);

        holder.img.setScaleType(ImageView.ScaleType.FIT_XY);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, ItemDetails.class);
                intent.putExtra(Constant.ID,item.getId());
                context.startActivity(intent);
                ((Activity)  context).overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            }
        });
    }
    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public void setFilter(List listItem) {
        itemModels = new ArrayList<>();
        itemModels.addAll(listItem);
        notifyDataSetChanged();
    }
}

