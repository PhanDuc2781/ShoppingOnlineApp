package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grocerystore.Adapter.OrderedUserItemAdater;
import com.example.grocerystore.Model.OrderedItemUser;
import com.example.grocerystore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailOrderUserActivity extends AppCompatActivity {
    String orderId , orderTo ;
    private TextView item_OrderedId , item_DateOrdered , item_StatusOrdered , item_OrderedNameShop , item_TotalBill ,item_OrderedAddress;
    private ImageView back ;
    ArrayList<OrderedItemUser> list ;
    OrderedUserItemAdater adater ;
    private RecyclerView rec_ItemOrdered ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_user);

        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderId");
        //With timeStamp is Id
        orderTo = intent.getStringExtra("orderTo");
        //With shopId is orderTo

        //Init UI views
        item_OrderedId          = (TextView) findViewById(R.id.item_OrderedId);
        item_DateOrdered        = (TextView) findViewById(R.id.item_DateOrdered);
        item_StatusOrdered      = (TextView) findViewById(R.id.item_StatusOrdered);
        item_OrderedNameShop    = (TextView) findViewById(R.id.item_OrderedNameShop);
        item_TotalBill          = (TextView) findViewById(R.id.item_TotalBill);
        item_OrderedAddress          = (TextView) findViewById(R.id.item_OrderedAddress);
        rec_ItemOrdered = (RecyclerView) findViewById(R.id.rec_ItemOrderd);

        back = (ImageView) findViewById(R.id.back_OrderedRec);
        //
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailOrderUserActivity.this , MainUserActivity.class));
                finish();
            }
        });

        loadInfomationOrder();
        loadOrderDetail();
        loadItemOrdered();

    }

    //Load item order
    private void loadItemOrdered() {
        list = new ArrayList<>();
        adater = new OrderedUserItemAdater(DetailOrderUserActivity.this , list);
        rec_ItemOrdered.setLayoutManager(new LinearLayoutManager(DetailOrderUserActivity.this , RecyclerView.VERTICAL , false));
        rec_ItemOrdered.setAdapter(adater);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds :snapshot.getChildren()){
                    OrderedItemUser user = ds.getValue(OrderedItemUser.class);
                    list.add(user);
                }
                adater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Load detail Order
    private void loadOrderDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = ""+snapshot.child("status").getValue();
                    String totalBill = ""+snapshot.child("totalBill").getValue();
                    String date = ""+snapshot.child("orderTime").getValue();
                    String oderId = ""+snapshot.child("orderId").getValue();
                    String orderBy = ""+snapshot.child("orderBy").getValue();

                item_DateOrdered.setText(""+date);
                item_OrderedId.setText(""+oderId);
                item_TotalBill.setText(""+totalBill + "$");

                if(status.equals("Chờ xác nhận.")){
                    item_StatusOrdered.setText(""+status);
                    item_StatusOrdered.setTextColor(getResources().getColor(R.color.red));
                }else if(status.equals("Đang giao.")){
                    item_StatusOrdered.setText(""+status);
                    item_StatusOrdered.setTextColor(getResources().getColor(R.color.blue));
                }else if(status.equals("Đã giao.")){
                    item_StatusOrdered.setText(""+status);
                    item_StatusOrdered.setTextColor(getResources().getColor(R.color.blue));
                }else if(status.equals("Đã hủy.")){
                    item_StatusOrdered.setText(""+status);
                    item_StatusOrdered.setTextColor(getResources().getColor(R.color.red));
                }


            reference.child(orderTo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String address = ""+snapshot.child("address_shop").getValue();
                            item_OrderedAddress.setText(""+ address);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Load information shop user ordered
    private void loadInfomationOrder() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameStore = ""+snapshot.child("shop_name").getValue();
                item_OrderedNameShop.setText(""+ nameStore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}