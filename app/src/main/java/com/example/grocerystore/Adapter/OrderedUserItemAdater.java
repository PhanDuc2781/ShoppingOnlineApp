package com.example.grocerystore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Model.OrderedItemUser;
import com.example.grocerystore.R;

import java.util.ArrayList;

public class OrderedUserItemAdater extends RecyclerView.Adapter<OrderedUserItemAdater.ViewHolder> {
    private Context context ;
    ArrayList<OrderedItemUser> list ;

    public OrderedUserItemAdater(Context context, ArrayList<OrderedItemUser> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderedUserItemAdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordered_user , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedUserItemAdater.ViewHolder holder, int position) {
                OrderedItemUser item = list.get(position);
                String name = item.getName();
                String quantity = item.getQuantyti();
                String priceEach = item.getPrice();
                String totalPrice = item.getTotalPrice();

                holder.item_NameOrdered_User.setText(""+name);
                holder.priceEachItem_OrderedUser.setText(""+priceEach + "$");
                holder.totalPriceItem_OrderedUser.setText(""+totalPrice + "$");
                holder.quantityItemOrderedUser.setText("[ "+ quantity + " ]");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_NameOrdered_User , totalPriceItem_OrderedUser , quantityItemOrderedUser , priceEachItem_OrderedUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_NameOrdered_User = itemView.findViewById(R.id.item_NameOrdered_User);
            totalPriceItem_OrderedUser = itemView.findViewById(R.id.totalPriceItem_OrderedUser);
            quantityItemOrderedUser = itemView.findViewById(R.id.quantityItemOrderedUser);
            priceEachItem_OrderedUser = itemView.findViewById(R.id.priceEachItem_OrderedUser);

        }
    }
}
