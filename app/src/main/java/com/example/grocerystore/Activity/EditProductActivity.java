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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProductActivity extends AppCompatActivity {
    private CircleImageView img_EditProduct ;
    private ImageView back_EditMainSeller;
    private EditText edt_EditNameProduct , edt_EditDescriptionProducts , edt_EditQuantityProduct,
            edt_EditDiscountPrice , edt_EditDiscountNote , edt_EditPriceProduct;
    private TextView txt_EditCategoryProduct;
    private Button btn_UpdateProduct;
    private SwitchCompat discount_EditSwitch;

    Uri img_Uri;
    public static final int IMAGE_PICKER = 199;
    FirebaseAuth auth  ;
    String id ;
    String img_Url;
    ProgressDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);


        auth = FirebaseAuth.getInstance();

        id = getIntent().getStringExtra("currentProduct");

        //init UI Views
        img_EditProduct             = (CircleImageView) findViewById(R.id.img_EditProduct);
        back_EditMainSeller         = (ImageView) findViewById(R.id.back_EditMainSeller);
        edt_EditNameProduct         = (EditText) findViewById(R.id.edt_EditNameProduct);
        edt_EditDescriptionProducts = (EditText) findViewById(R.id.edt_EditDescriptionProducts);
        edt_EditQuantityProduct     = (EditText) findViewById(R.id.edt_EditQuantityProduct);
        edt_EditDiscountPrice       = (EditText) findViewById(R.id.edt_EditDiscountPrice);
        txt_EditCategoryProduct     = (TextView) findViewById(R.id.txt_EditCategoryProduct);
        edt_EditDiscountNote        = (EditText) findViewById(R.id.edt_EditDiscountNote);
        edt_EditPriceProduct        = (EditText) findViewById(R.id.edt_EditPriceProduct);
        btn_UpdateProduct           = (Button) findViewById(R.id.btn_UpdateProduct);
        discount_EditSwitch         = (SwitchCompat) findViewById(R.id.discount_EditSwitch);

        loadProduct(id);

        back_EditMainSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProductActivity.this , MainSellerActivity.class));
                finish();
            }
        });

        img_EditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE_PICKER);
            }
        });

        txt_EditCategoryProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
                builder.setTitle("Loại sản phẩm")
                        .setItems(CategoryString.productCategory, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String category = CategoryString.productCategory[i];
                                txt_EditCategoryProduct.setText(category);
                            }
                        }).show();
            }
        });

        btn_UpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                updateData();
            }
        });

        discount_EditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    edt_EditDiscountPrice.setVisibility(View.VISIBLE);
                    edt_EditDiscountNote.setVisibility(View.VISIBLE);
                }else {
                    edt_EditDiscountPrice.setVisibility(View.GONE);
                    edt_EditDiscountNote.setVisibility(View.GONE);
                }
            }
        });


    }

    private String name , description , category , quantity ,realPrice , discount , discountNote  , discountAvailable;
    private void updateData() {
        name            = edt_EditNameProduct.getText().toString().trim();
        description     = edt_EditDescriptionProducts.getText().toString().trim();
        category        = txt_EditCategoryProduct.getText().toString().trim();
        quantity        = edt_EditQuantityProduct.getText().toString().trim();
        realPrice       = edt_EditPriceProduct.getText().toString().trim();
        discount        = edt_EditDiscountPrice.getText().toString().trim();
        discountNote    = edt_EditDiscountNote.getText().toString().trim();

        if(discount_EditSwitch.isChecked()){
            discountAvailable = "true";
        }else {
            discountAvailable = "false";
        }

        if(discount_EditSwitch.isChecked()){
            discountAvailable = "true";
        }else {
            discountAvailable = "false";
        }
        if(TextUtils.isEmpty(name)){
            edt_EditNameProduct.setError("Nhập tên sản phẩm!");
        }
        if(TextUtils.isEmpty(description)){
            edt_EditDescriptionProducts.setError("Nhập mô tả!");
        }
        if(TextUtils.isEmpty(category)){
            txt_EditCategoryProduct.setError("Chọn loại sản phẩm!");
        }
        if(TextUtils.isEmpty(quantity)){
            edt_EditQuantityProduct.setError("Nhập đơn vị tính!");
        }
        if(TextUtils.isEmpty(realPrice)) {
            edt_EditPriceProduct.setError("Nhập giá sản phẩm");
        }
        if(discountAvailable.equals("true")){

            discount        = edt_EditDiscountPrice.getText().toString().trim();
            discountNote    = edt_EditDiscountNote.getText().toString().trim() ;
            if(TextUtils.isEmpty(discount)){
                edt_EditDiscountPrice.setError("Nhập giá sau giảm!");
            }
        }else {
            discount = "0";
            discountNote = "";
        }
        if(img_Uri== null){
            Toast.makeText(EditProductActivity.this , "Them anh moi" , Toast.LENGTH_SHORT).show();
        }else {
            inputData();
        }


    }

    private void inputData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploadImage").child(id);

        storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            img_Url = uri.toString();

                            HashMap<String , Object> map = new HashMap<>();

                            map.put("name" , name);
                            map.put("description" , description);
                            map.put("category" , category);
                            map.put("quantity" ,quantity);
                            map.put("realPrice" , realPrice);
                            map.put("img_Url" , img_Url);
                            map.put("discountNote" , discountNote);
                            map.put("discount" ,discount);
                            map.put("discountAvailable" , discountAvailable);

                            reference.child(auth.getUid()).child("Products").child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    clearData();
                                    Toast.makeText(EditProductActivity.this , "Thêm thành công" , Toast.LENGTH_LONG).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(EditProductActivity.this , ""+e.getMessage() , Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

//Clear data in edittext when add success product
    private void clearData() {

        img_EditProduct.setImageResource(R.drawable.ic_cart);
        edt_EditNameProduct.setText("");
        edt_EditDescriptionProducts.setText("");
        edt_EditPriceProduct.setText("");
        edt_EditDiscountPrice.setText("");
        edt_EditDiscountNote.setText("");
        txt_EditCategoryProduct.setText("");
        edt_EditQuantityProduct.setText("");
    }

    private void loadProduct(String id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).child("Products").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    edt_EditNameProduct.setText(""+ snapshot.child("name").getValue());
                    edt_EditDescriptionProducts.setText(""+snapshot.child("description").getValue());
                    edt_EditPriceProduct.setText(""+snapshot.child("realPrice").getValue());
                    edt_EditQuantityProduct.setText(""+snapshot.child("quantity").getValue());
                    txt_EditCategoryProduct.setText(""+snapshot.child("category").getValue());

                    String img_Url = ""+ snapshot.child("img_Url").getValue();
                    Picasso.get().load(img_Url).into(img_EditProduct);
                    String discountAvailable = ""+snapshot.child("discountAvailable").getValue();

                    if(discountAvailable.equals("true")){

                        discount_EditSwitch.setChecked(true);
                        edt_EditDiscountPrice.setVisibility(View.VISIBLE);
                        edt_EditDiscountNote.setVisibility(View.VISIBLE);

                        edt_EditDiscountPrice.setText(""+snapshot.child("discount").getValue().toString());
                        edt_EditDiscountNote.setText(""+snapshot.child("discountNote").getValue().toString());
                    }else {
                        discount_EditSwitch.setChecked(false);
                        edt_EditDiscountPrice.setVisibility(View.GONE);
                        edt_EditDiscountNote.setVisibility(View.GONE);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this , ""+error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER){
            img_Uri = data.getData();
            img_EditProduct.setImageURI(img_Uri);
        }
    }
}