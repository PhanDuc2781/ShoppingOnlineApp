package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.grocerystore.Adapter.OrderUserAdapter;
import com.example.grocerystore.Adapter.ShopAdapter;
import com.example.grocerystore.Model.OrderItemUser;
import com.example.grocerystore.Model.ShopModel;
import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainUserActivity extends AppCompatActivity {
    private TextView txt_NameUser , txt_Email , txt_Phone , tab_ShopUser , tab_OrdersUser;
    private ImageView img_EditProfile , img_LogOutUser , img_Cart;
    private CircleImageView img_Profile;
    FirebaseAuth auth ;

    RecyclerView rec_ShopUser  ,rec_Order ;
    ArrayList<ShopModel> shopModels ;
    ArrayList<OrderItemUser> orderItemUsers ;
    OrderUserAdapter orderUserAdapter ;
    ShopAdapter adapter ;

    private RelativeLayout rel_Shop , rel_Order ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        auth = FirebaseAuth.getInstance();
        //init UI views
        txt_NameUser    = (TextView) findViewById(R.id.txt_NameUser);
        txt_Email       = (TextView)findViewById(R.id.email_User);
        txt_Phone       = (TextView) findViewById(R.id.phone_User);
        tab_ShopUser    = (TextView) findViewById(R.id.tab_ShopUser);
        tab_OrdersUser  = (TextView) findViewById(R.id.tab_OrdersUser);
        img_Profile     = (CircleImageView) findViewById(R.id.image_User);
        img_Cart        = (ImageView) findViewById(R.id.CartUser);
        img_EditProfile = (ImageView) findViewById(R.id.edit_ProfileUser);
        img_LogOutUser  = (ImageView) findViewById(R.id.logOutUser);
        rel_Shop        = (RelativeLayout) findViewById(R.id.rel_Shop);
        rel_Order       = (RelativeLayout) findViewById(R.id.rel_OrdersUser);
        rec_ShopUser    = (RecyclerView) findViewById(R.id.rec_ShopUser);
        rec_Order       = (RecyclerView) findViewById(R.id.rec_OrderUser);
        profile();
        loadShop();


        img_EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this , EditprofileUserActivity.class));
                finish();
            }
        });

        //
        img_LogOutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] options = {"Có" , "Không"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                builder.setTitle("Bạn có muốn đăng xuất không!")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0:
                                        HashMap<String  , Object> map = new HashMap<>();
                                        map.put("online" , "false");

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.child(auth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                auth.signOut();
                                                startActivity(new Intent(MainUserActivity.this , LoginActivity.class));
                                                finish();
                                            }
                                        });
                                        break;
                                    case 1:
                                        builder.setCancelable(true);
                                }
                            }
                        });
                builder.create().show();


            }
        });

        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //
        tab_ShopUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopUI();
            }
        });
        //
        tab_OrdersUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderUI();
                loadOrderUser();
            }
        });


    }

    private void loadOrderUser() {
        orderItemUsers = new ArrayList<>();
        rec_Order.setLayoutManager(new LinearLayoutManager(MainUserActivity.this  , RecyclerView.VERTICAL,false));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItemUsers.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                        String uId = ""+ds.getRef().getKey();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uId).child("Orders");
                        reference.orderByChild("orderBy").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot!=null){
                                        for (DataSnapshot ds:snapshot.getChildren()){
                                            OrderItemUser user = ds.getValue(OrderItemUser.class);
                                            orderItemUsers.add(user);
                                        }
                                        orderUserAdapter = new OrderUserAdapter(MainUserActivity.this , orderItemUsers);
                                        rec_Order.setAdapter(orderUserAdapter);
                                        orderUserAdapter.notifyDataSetChanged();
                                    }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showShopUI() {
        rel_Shop.setVisibility(View.VISIBLE);
        rel_Order.setVisibility(View.GONE);

        tab_ShopUser.setTextColor(getResources().getColor(R.color.black));
        tab_ShopUser.setBackgroundResource(R.drawable.bg_tab2);

        tab_OrdersUser.setTextColor(getResources().getColor(R.color.white));
        tab_OrdersUser.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrderUI() {
        rel_Shop.setVisibility(View.GONE);
        rel_Order.setVisibility(View.VISIBLE);

        tab_ShopUser.setTextColor(getResources().getColor(R.color.white));
        tab_ShopUser.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tab_OrdersUser.setTextColor(getResources().getColor(R.color.black));
        tab_OrdersUser.setBackgroundResource(R.drawable.bg_tab2);
    }



    private void profile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uId").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String img_Url = ""+ds.child("img_Profile").getValue();

                            txt_NameUser.setText(name);
                            txt_Email.setText(email);
                            txt_Phone.setText(phone);
                            Picasso.get().load(img_Url).into(img_Profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShop(){

        shopModels = new ArrayList<>();
        adapter = new ShopAdapter(MainUserActivity.this , shopModels);
        rec_ShopUser.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType")
                .equalTo("Seller").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    ShopModel model = ds.getValue(ShopModel.class);
                    Collections.shuffle(shopModels);
                    shopModels.add(model);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}