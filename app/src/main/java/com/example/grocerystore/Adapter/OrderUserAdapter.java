package com.example.grocerystore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Activity.DetailOrderUserActivity;
import com.example.grocerystore.Model.OrderItemUser;
import com.example.grocerystore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.ViewHolder> {
    private Context context ;
    ArrayList<OrderItemUser> list;

    public OrderUserAdapter(Context context, ArrayList<OrderItemUser> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_user , parent ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItemUser orderItemUser = list.get(position);
        holder.code_OrderUser.setText("CODE : " +orderItemUser.getOrderId());

        holder.status_OrderUser.setText(""+orderItemUser.getStatus());
        holder.total_BillOrderUser.setText("Tổng tiền : "+orderItemUser.getTotalBill() + "$");
        holder.date_OrderUser.setText(""+orderItemUser.getOrderTime());
        String status = ""+orderItemUser.getStatus();
        String orderTo = orderItemUser.getOrderTo();
        String orderId = orderItemUser.getOrderId();

        if(status.equals("Chờ xác nhận")){
            holder.status_OrderUser.setTextColor(context.getResources().getColor(R.color.teal_700));
        }else if (status.equals("Đang giao")){
            holder.status_OrderUser.setTextColor(context.getResources().getColor(R.color.teal_200));
        }else if(status.equals("Đã giao")){
            holder.status_OrderUser.setTextColor(context.getResources().getColor(R.color.teal_200));
        }else if(status.equals("Đã hủy")){
            holder.status_OrderUser.setTextColor(context.getResources().getColor(R.color.teal_700));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , DetailOrderUserActivity.class);
                intent.putExtra("orderTo" , orderTo);
                intent.putExtra("orderId" , orderId);
                context.startActivity(intent);

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderItemUser.getOrderTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("shop_name").getValue();
                holder.name_StoreOrderUser.setText(""+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView code_OrderUser , name_StoreOrderUser , total_BillOrderUser , date_OrderUser , status_OrderUser ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code_OrderUser = itemView.findViewById(R.id.code_OrderUser);
            name_StoreOrderUser = itemView.findViewById(R.id.name_StoreOrderUser);
            total_BillOrderUser = itemView.findViewById(R.id.total_BillOrderUser);
            date_OrderUser = itemView.findViewById(R.id.date_OrderUser);
            status_OrderUser = itemView.findViewById(R.id.status_OrderUser);
        }
    }
}
