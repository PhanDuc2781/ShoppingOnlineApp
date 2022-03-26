package com.example.grocerystore.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Activity.DetailShopActivity;
import com.example.grocerystore.Model.CartItem;
import com.example.grocerystore.R;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private Context context;
    ArrayList<CartItem> list ;

    public CartItemAdapter(Context context, ArrayList<CartItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item , parent , false);

        return new ViewHolder(view);
    }
    public   String timeStamp ;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            CartItem item= list.get(position);
            final String id = item.getId();
            String name = item.getName();
            String total = item.getTotalPrice();
            String price = item.getPrice();
            String number = item.getNumber();

            holder.name_ProCartItem.setText(""+name);
            holder.price_ProItem.setText(""+price);
            holder.number_ProItem.setText("["+number+"]");
            holder.total_PriceProItem.setText(""+total);



            holder.remove_ProItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Delete data in Sqlite
                    EasyDB easyDB = EasyDB.init(context , "ECommerce_DB")
                            .setTableName("CART_ITEM")
                            .addColumn(new Column("Id_Item" , new String[]{"text" , "unique"}))
                            .addColumn(new Column("ProductId" , new String[]{"text" , "not null"}))
                            .addColumn(new Column("Name" , new String[]{"text" , "not null"}))
                            .addColumn(new Column("Price_Each" , new String[]{"text" , "not null"}))
                            .addColumn(new Column("Number" , new String[]{"text" , "not null"}))
                            .addColumn(new Column("Total_Price" , new String[]{"text" , "not null"}))
                            .doneTableColumn();

                    easyDB.deleteRow(1 , id);
                    Toast.makeText(context , "Xóa thành công " , Toast.LENGTH_SHORT).show();

                    list.remove(position);
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                    double tx = Double.parseDouble(((DetailShopActivity)context).total_Bill.getText().toString().trim());
                    double totalPrice = tx - Double.parseDouble(total);
                    double deliveryFee = Double.parseDouble(((DetailShopActivity)context).delivery_fee);
                    double sTotalPrice = Double.parseDouble(String.format("%.2f" , totalPrice)) -deliveryFee;
                    ((DetailShopActivity)context).total_Price = 0.0 ;
                    ((DetailShopActivity)context).total_Monney.setText(String.format("%.2f" , sTotalPrice));
                    ((DetailShopActivity)context).total_Bill.setText( "".valueOf(totalPrice));

                    ((DetailShopActivity) context).cartCount();
                    if(list.size()==0){
                        ((DetailShopActivity)context).delivery.setVisibility(View.GONE);
                    }

                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_ProCartItem , total_PriceProItem , price_ProItem , number_ProItem , remove_ProItem ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_ProCartItem = itemView.findViewById(R.id.name_ProCartItem);
            total_PriceProItem = itemView.findViewById(R.id.total_PriceProItem);
            price_ProItem = itemView.findViewById(R.id.price_ProItem);
            number_ProItem = itemView.findViewById(R.id.number_ProItem);
            remove_ProItem = itemView.findViewById(R.id.remove_ProItem);
        }
    }
}
