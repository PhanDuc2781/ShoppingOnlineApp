package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.grocerystore.Adapter.ProductSellerAdapter;
import com.example.grocerystore.Model.CategoryString;
import com.example.grocerystore.Model.ProductSeller;
import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainSellerActivity extends AppCompatActivity {
    private TextView txt_NameShop , txt_Email , txt_Phone , tab_ProductsSeller , tab_OrdersSeller;
    private ImageView img_LogOutSeller , img_EditSeller  , add_Products , filterProduct , filterOrder;
    private CircleImageView img_Profile ;
     private EditText edt_SearchOrder , edt_SearchProduct ;
    FirebaseAuth auth ;

    RelativeLayout rel_Products , rel_Orders ;
    RecyclerView rec_ProductSeller  , rec_Order;


    ArrayList<ProductSeller> list ;
    ProductSellerAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        auth = FirebaseAuth.getInstance();

        txt_NameShop        = (TextView) findViewById(R.id.txt_NameSeller);
        txt_Phone           = (TextView) findViewById(R.id.phone_Seller) ;
        txt_Email           = (TextView) findViewById(R.id.email_Seller);
        tab_ProductsSeller  = (TextView) findViewById(R.id.tab_ProductsSeller);
        tab_OrdersSeller    = (TextView)  findViewById(R.id.tab_OrdersSeller);
        img_Profile         = (CircleImageView) findViewById(R.id.image_Seller);
        filterProduct       = (ImageView) findViewById(R.id.filer_CategoryProduct);
        filterOrder         = (ImageView) findViewById(R.id.filer_Order);
        edt_SearchOrder     = (EditText) findViewById(R.id.edt_SearchOrder);
        edt_SearchProduct   = (EditText) findViewById(R.id.edt_SearchProduct);
        img_EditSeller      = (ImageView) findViewById(R.id.edit_ProfileSeller);
        img_LogOutSeller    = (ImageView) findViewById(R.id.logOutSeller);
        add_Products        = (ImageView) findViewById(R.id.add_CartSeller);
        rel_Products        = (RelativeLayout) findViewById(R.id.rel_Products);
        rel_Orders          = (RelativeLayout) findViewById(R.id.rel_Orders);

        rec_ProductSeller  = (RecyclerView) findViewById(R.id.rec_ProductSeller);
        rec_Order = (RecyclerView) findViewById(R.id.rec_OrderSeller);

        loadAllProducts();
        profile();
        showProducts();


        tab_ProductsSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProducts();

            }
        });
        //
        tab_OrdersSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrders();

            }
        });
        //
        img_EditSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainSellerActivity.this , EditProfileSellerActivity.class));
                    finish();
            }
        });
//
        img_LogOutSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String  , Object> map = new HashMap<>();
                map.put("online" , "false");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            auth.signOut();
                            startActivity(new Intent(MainSellerActivity.this , LoginActivity.class));
                            finish();
                        }
                    }
                });

            }
        });
//
        add_Products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this , AddProductsActivity.class));
                finish();
            }
        });

        edt_SearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Chọn loại sản phẩm.")
                        .setItems(CategoryString.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    String category = CategoryString.productCategory1[i];
                                    if(category.equals("Tất Cả")){
                                        loadAllProducts();
                                    }else {
                                        loadFilter(category);
                                    }
                            }
                        }).show();
            }
        });



    }

    //Search product by Category
    private void loadFilter(String category) {
        list = new ArrayList<>();
        rec_ProductSeller.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this , RecyclerView.VERTICAL , false));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String categoryProduct = ds.child("category").getValue().toString();
                    if(category.equals(categoryProduct)){
                        ProductSeller model = ds.getValue(ProductSeller.class);
                        list.add(model);
                    }
                }
                adapter = new ProductSellerAdapter(MainSellerActivity.this , list);
                rec_ProductSeller.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//Load all products
    private void loadAllProducts() {
        list = new ArrayList<>();
        rec_ProductSeller.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this , RecyclerView.VERTICAL , false));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    ProductSeller model = ds.getValue(ProductSeller.class);
                    Collections.shuffle(list);
                    list.add(model);
                }
                adapter = new ProductSellerAdapter(MainSellerActivity.this , list);
                rec_ProductSeller.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Show tab order
    private void showOrders() {
        rel_Products.setVisibility(View.GONE);
        rel_Orders.setVisibility(View.VISIBLE);

        tab_ProductsSeller.setTextColor(getResources().getColor(R.color.white));
        tab_ProductsSeller.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tab_OrdersSeller.setTextColor(getResources().getColor(R.color.black));
        tab_OrdersSeller.setBackgroundResource(R.drawable.bg_tab2);
    }

    //Show tab Products
    private void showProducts() {
        rel_Products.setVisibility(View.VISIBLE);
        rel_Orders.setVisibility(View.GONE);

        tab_ProductsSeller.setTextColor(getResources().getColor(R.color.black));
        tab_ProductsSeller.setBackgroundResource(R.drawable.bg_tab2);

        tab_OrdersSeller.setTextColor(getResources().getColor(R.color.white));
        tab_OrdersSeller.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

//Show profile
    private void profile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uId").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String name = ""+ds.child("shop_name").getValue() ;
                            String phone = ""+ds.child("phone").getValue();
                            String email = ""+ds.child("email").getValue();
                            String img_Url = ""+ds.child("img_Profile").getValue();

                            Picasso.get().load(img_Url).into(img_Profile);
                            txt_Email.setText(email);
                            txt_NameShop.setText(name);
                            txt_Phone.setText(phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}