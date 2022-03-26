package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerystore.Model.CategoryString;
import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductsActivity extends AppCompatActivity {
    private CircleImageView img_Product;
    private EditText edt_Name , edt_Description  , edt_Quantity , edt_Price , edt_DiscountPrice , edt_DiscountNote ;
    private SwitchCompat switchCompat ;
    private TextView  txt_Category;
    private Button btn_AddProduct;
    private ImageView img_Back ;
    Uri img_Uri ;
    
    public static final int IMAGE_PICKER = 100 ;
    FirebaseAuth auth ;
    FirebaseFirestore firestore ;
    ProgressDialog dialog ;
    String img_Url ;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        auth        = FirebaseAuth.getInstance();
        firestore   = FirebaseFirestore.getInstance();
        dialog      = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        
        //init UI views 
        img_Product         = (CircleImageView) findViewById(R.id.img_AddProduct);
        img_Back            = (ImageView) findViewById(R.id.back_MainSeller);
        switchCompat        = (SwitchCompat) findViewById(R.id.discount_Switch);
        edt_Name            = (EditText) findViewById(R.id.edt_NameProduct);
        edt_Description     = (EditText) findViewById(R.id.edt_DescriptionProducts);
        edt_Quantity        = (EditText) findViewById(R.id.edt_QuantityProduct);
        edt_Price           = (EditText) findViewById(R.id.edt_PriceProduct);
        edt_DiscountNote    = (EditText) findViewById(R.id.edt_DiscountNote);
        edt_DiscountPrice   = (EditText) findViewById(R.id.edt_DiscountPrice);
        txt_Category        = (TextView) findViewById(R.id.txt_CategoryProduct);
        btn_AddProduct      = (Button) findViewById(R.id.btn_AddNewProduct);

        //If switchCompat not is Checked

        edt_DiscountPrice.setVisibility(View.GONE);
        edt_DiscountNote.setVisibility(View.GONE);

        //Button add product
        btn_AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                inputData();
            }
        });
        //Button back previos Activity
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProductsActivity.this , MainSellerActivity.class));
                finish();
            }
        });
        //
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    edt_DiscountPrice.setVisibility(View.VISIBLE);
                    edt_DiscountNote.setVisibility(View.VISIBLE);
                }else {
                    edt_DiscountPrice.setVisibility(View.GONE);
                    edt_DiscountNote.setVisibility(View.GONE);
                }
            }
        });
        
        //
        img_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE_PICKER);
            }
        });

        txt_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });
        
        
    }
    private String name , description , category , quantity ,realPrice , discount , discountNote  , discountAvailable;
    //Input data
    private void inputData() {
        name            = edt_Name.getText().toString().trim();
        description     = edt_Description.getText().toString().trim();
        category        = txt_Category.getText().toString().trim();
        quantity        = edt_Quantity.getText().toString().trim();
        realPrice       = edt_Price.getText().toString().trim();
        discount        = edt_DiscountPrice.getText().toString().trim();
        discountNote    = edt_DiscountNote.getText().toString().trim();
        if(switchCompat.isChecked()){
            discountAvailable = "true";
        }else {
            discountAvailable = "false";
        }

        if(img_Uri==null){
            Toast.makeText(AddProductsActivity.this , "Thêm ảnh sản phẩm" , Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(name)){
            edt_Name.setError("Nhập tên sản phẩm!");
        }
        if(TextUtils.isEmpty(description)){
            edt_Description.setError("Nhập mô tả!");
        }
        if(TextUtils.isEmpty(category)){
            txt_Category.setError("Chọn loại sản phẩm!");
        }
        if(TextUtils.isEmpty(quantity)){
            edt_Quantity.setError("Nhập đơn vị tính!");
        }
        if(TextUtils.isEmpty(realPrice)) {
            edt_Price.setError("Nhập giá sản phẩm");
        }
        if(discountAvailable == "true"){

            discount        = edt_DiscountPrice.getText().toString().trim();
            discountNote    = edt_DiscountNote.getText().toString().trim() ;
            if(TextUtils.isEmpty(discount)){
                edt_DiscountPrice.setError("Nhập giá sau giảm!");
            }
        }else {
            discount = "0";
            discountNote = "";
        }
        addProducts();
    }

    //After check all condition add product to Firebase
    private void addProducts() {
        String timeStamp = ""+System.currentTimeMillis();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("imageProducts").child(timeStamp);

        storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(task.isSuccessful()){
                                img_Url = uri.toString();

                                HashMap<String , Object> map = new HashMap<>();
                                map.put("productId" , timeStamp);
                                map.put("name" , name);
                                map.put("description" , description);
                                map.put("category" , category);
                                map.put("quantity" , quantity);
                                map.put("realPrice" , realPrice);
                                map.put("discount" , discount);
                                map.put("discountNote" , discountNote);
                                map.put("img_Url" , img_Url);
                                map.put("discountAvailable" ,discountAvailable);
                                map.put("timeStamp" , timeStamp);
                                map.put("uId" , auth.getUid());

                                reference.child(auth.getUid())
                                        .child("Products")
                                        .child(timeStamp)
                                        .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        clearData();
                                        Toast.makeText(AddProductsActivity.this , "Thêm thành công" , Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(AddProductsActivity.this , ""+e.getMessage() , Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    //When add product success will clear data in edittext
    private void clearData() {
        img_Product.setImageResource(R.drawable.ic_cart);
        edt_Name.setText("");
        edt_Description.setText("");
        edt_Price.setText("");
        edt_DiscountPrice.setText("");
        edt_DiscountNote.setText("");
        txt_Category.setText("");
        edt_Quantity.setText("");
    }

    //Chose category product in Dialog
    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Loại sản phẩm .")
                .setItems(CategoryString.productCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String category = CategoryString.productCategory[i];
                        txt_Category.setText(category);
                    }
                }).show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER){
            if(data != null){
                img_Uri = data.getData();
                img_Product.setImageURI(img_Uri);
            }
        }
    }
}