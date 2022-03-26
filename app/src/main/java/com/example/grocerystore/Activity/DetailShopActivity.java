package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerystore.Adapter.CartItemAdapter;
import com.example.grocerystore.Adapter.ShopDetailProductAdapter;
import com.example.grocerystore.Model.CartItem;
import com.example.grocerystore.Model.CategoryString;
import com.example.grocerystore.Model.ProductSeller;
import com.example.grocerystore.Model.ShopModel;
import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class DetailShopActivity extends AppCompatActivity {
    public String shopId ;
    
    private TextView name_DetailShop ,phone_DetailShop , email_DetailShop , address_DetailShop ,open_DetailShop , deliveryFee_DetailShop , cart_Count;
    private ImageView back , call , check_Location  , filterDetail  , cart_UserShop;
    private EditText search_DetailShop ;
    private RecyclerView rec_DetailProductShop ;
    private ArrayList<ProductSeller> list ;
    private ShopDetailProductAdapter adapter ;
    String phone , myLatitude , myLongitude , myPhone;
    public String latitude , longitude , delivery_fee  ;

    ProgressDialog dialog ;
    FirebaseAuth auth ;

    private ArrayList<CartItem> cartItems ;
    private CartItemAdapter cartItemAdapter;
    private EasyDB easyDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_shop);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        auth = FirebaseAuth.getInstance();

        shopId = getIntent().getStringExtra("shopId");
        
        name_DetailShop         = (TextView) findViewById(R.id.name_DetailShop);
        phone_DetailShop        = (TextView) findViewById(R.id.phone_DetailShop);
        email_DetailShop        = (TextView) findViewById(R.id.email_DetailShop);
        address_DetailShop      = (TextView) findViewById(R.id.address_DetailShop);
        open_DetailShop         = (TextView) findViewById(R.id.open_DetailShop);
        deliveryFee_DetailShop  = (TextView) findViewById(R.id.deliveryFee_DetailShop);
        cart_Count              = (TextView) findViewById(R.id.cart_Count);
        search_DetailShop       = (EditText) findViewById(R.id.search_DetailShop);
        filterDetail            = (ImageView)  findViewById(R.id.catrgory_DetailShop) ;
        back                    = (ImageView) findViewById(R.id.back_DetailMainUser);
        call                    = (ImageView) findViewById(R.id.phoneCallDetail) ;
        check_Location          = (ImageView) findViewById(R.id.check_Location);
        cart_UserShop           = (ImageView) findViewById(R.id.cart_UserShop);
        rec_DetailProductShop   = (RecyclerView) findViewById(R.id.rec_DetailProductShop);

        //Load cart in Database
        easyDB = EasyDB.init(DetailShopActivity.this , "ECommerce_DB")
                .setTableName("CART_ITEM")
                .addColumn(new Column("Id_Item" , new String[]{"text" , "unique"}))
                .addColumn(new Column("ProductId" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Price_Each" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Number" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Total_Price" , new String[]{"text" , "not null"}))
                .doneTableColumn();


        loadInformationShop();
        loadProduct();
        cartCount();

        //each shop have order if add to cart between shop different
        //delete cart when dialog cartItem dismiss
        deleteCart();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailShopActivity.this , MainUserActivity.class));
                finish();
            }
        });
        //Search product
        search_DetailShop.addTextChangedListener(new TextWatcher() {
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
        //Chose category product
        filterDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailShopActivity.this);
                builder.setTitle("Chọn loại sản phẩm.")
                        .setItems(CategoryString.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String category = CategoryString.productCategory1[i];
                                if(category.equals("Tất Cả")){
                                    loadProduct();
                                }else {
                                    adapter.getFilter().filter(category);
                                }
                            }
                        });
                builder.create().show();
            }
        });

        //
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL , Uri.parse("tel:" + Uri.encode(phone))));

            }
        });
    //
        check_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = "http://maps.google.com/maps?saddr=" + myLatitude + "," +myLongitude +"&daddr"+latitude+ "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(address));
                startActivity(intent);
            }
        });

        //
        cart_UserShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showCartDialog();
            }
        });

    }

    private void deleteCart() {


        easyDB.deleteAllDataFromTable();
    }

    //Count number item in cart
    public void cartCount(){
        int count = easyDB.getAllData().getCount();
        if(count<=0){
            cart_Count.setVisibility(View.GONE);
        }else {
            cart_Count.setVisibility(View.VISIBLE);
            cart_Count.setText(""+count);
        }
    }


    public double total_Price = 0.0;
    public TextView total_Monney;
    public TextView delivery , total_Bill;

    private void showCartDialog() {

        cartItems = new ArrayList<>();


        View view = LayoutInflater.from(DetailShopActivity.this).inflate(R.layout.dialog_cart , null);

        total_Monney = view.findViewById(R.id.total_Monney);
        delivery = view.findViewById(R.id.delivery_Fee);
        total_Bill = view.findViewById(R.id.total_Bill);
        RecyclerView rec_Cart = view.findViewById(R.id.rec_CartItem);
        Button btn_CheckOut = view.findViewById(R.id.btn_CheckOut);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        //get all data from Sqlite Database
        easyDB = EasyDB.init(this , "ECommerce_DB")
                .setTableName("CART_ITEM")
                .addColumn(new Column("Id_Item" , new String[]{"text" , "unique"}))
                .addColumn(new Column("ProductId" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Price_Each" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Number" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Total_Price" , new String[]{"text" , "not null"}))
                .doneTableColumn();

        //get all records fromDB
        Cursor  res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String proId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String number = res.getString(5);
            String totalPrice = res.getString(6);

            total_Price = total_Price + Double.parseDouble(totalPrice);

            CartItem item = new CartItem(""+id  , ""+proId , ""+name , ""+price , ""+number , ""+totalPrice);

            cartItems.add(item);

        }
        rec_Cart.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        cartItemAdapter = new CartItemAdapter(this , cartItems);
        rec_Cart.setAdapter(cartItemAdapter);

        delivery.setText(delivery_fee + "$");

        total_Monney.setText("".valueOf(total_Price));
        total_Bill.setText(""+ (total_Price + Double.parseDouble(delivery_fee.replaceAll("$" , ""))));
        if(cartItems.size()==0){
            btn_CheckOut.setVisibility(View.GONE);
            total_Monney.setText("0$");
            delivery.setText("0$");
            total_Bill.setText("0$");
            delivery.setVisibility(View.GONE);
        }else {
            btn_CheckOut.setVisibility(View.VISIBLE);
        }
        //Check out
        btn_CheckOut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                submitOrder();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //Delete cart when dialog demiss because defferent order store
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                EasyDB easyDB = EasyDB.init(DetailShopActivity.this , "ECommerce_DB")
                        .setTableName("CART_ITEM")
                        .addColumn(new Column("Id_Item" , new String[]{"text" , "unique"}))
                        .addColumn(new Column("ProductId" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("Name" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("Price_Each" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("Number" , new String[]{"text" , "not null"}))
                        .addColumn(new Column("Total_Price" , new String[]{"text" , "not null"}))
                        .doneTableColumn();

                easyDB.deleteAllDataFromTable();
                adapter.notifyDataSetChanged();
            }
        });

    }

    //Check out
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitOrder() {
        dialog.show();
        String bill = total_Bill.getText().toString();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        String timeStamp = ""+System.currentTimeMillis();
        //put data to Firebase Database
        HashMap<String , Object> map =new HashMap<>();
        map.put("orderId" , timeStamp);
        map.put("orderTime" , currentTime + "/" + currentDate);
        map.put("totalBill" , bill);
        map.put("status" , "Chờ xác nhận.");
        map.put("orderBy" , auth.getUid());
        map.put("orderTo" , shopId);

        DatabaseReference reference = FirebaseDatabase.
                getInstance().getReference("Users").child(shopId).child("Orders");
        reference.child(timeStamp).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //put Item product to Firebase Database
                for (int i = 0; i < cartItems.size(); i++) {
                    String pId = cartItems.get(i).getProId();
                    String id = cartItems.get(i).getId();
                    String name = cartItems.get(i).getName();
                    String price = cartItems.get(i).getPrice();
                    String quantyti = cartItems.get(i).getNumber();
                    String totalPrice = cartItems.get(i).getTotalPrice();

                    HashMap<String , Object> map1 = new HashMap<>();
                    map1.put("pId" , pId);
                    map1.put("name" , name);
                    map1.put("price" , price);
                    map1.put("quantyti",quantyti);
                    map1.put("totalPrice" , totalPrice);

                    reference.child(timeStamp).child("Items").child(pId).setValue(map1);

                    Intent intent = new Intent(DetailShopActivity.this , DetailOrderUserActivity.class);
                    startActivity(intent);

                }
                dialog.dismiss();
            }
        });

    }

    //Load products
    private void loadProduct() {

        list = new ArrayList<>();
        adapter = new ShopDetailProductAdapter(DetailShopActivity.this , list);
        rec_DetailProductShop.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopId).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    ProductSeller productSeller = ds.getValue(ProductSeller.class);
                    list.add(productSeller);
                    Collections.shuffle(list);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadInformationShop() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name_DetailShop.setText(""+snapshot.child("shop_name").getValue());
                phone = ""+snapshot.child("phone").getValue();
                phone_DetailShop.setText(phone);
                email_DetailShop.setText(" "+snapshot.child("email").getValue());
                address_DetailShop.setText(" "+snapshot.child("address_shop").getValue());
                deliveryFee_DetailShop.setText(""+ snapshot.child("delivery").getValue());
                latitude = ""+snapshot.child("latitue");
                longitude = ""+snapshot.child("longtitue");

               delivery_fee = deliveryFee_DetailShop.getText().toString();
                String status = ""+snapshot.child("open").getValue();
                if(status.equals("true")){
                    open_DetailShop.setText("Mở cửa");
                }else {
                    open_DetailShop.setText("Đóng cửa");
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}