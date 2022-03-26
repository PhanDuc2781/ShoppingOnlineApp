package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerRegisterActivity extends AppCompatActivity implements LocationListener {
    private CircleImageView add_ImgProfile;
    private ImageView image_GPS;
    private EditText edt_Name, edit_ShopName, edt_Phone, edt_Email, edt_Address, edt_FeeDelivery, edt_Pass, edt_RePass;
    private Button btn_SellerSignUp;

    private LocationManager locationManager;
    private double latitue, longtitue;

    public static final int LOCATION_REQUEST = 100;

    public static final int IMAGE_STORAGE_PICKER = 114;
    private String[] location;
    ProgressDialog dialog ;

    private Uri img_Uri;

    String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseUser fuser ;
    DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_regiter);

        auth        = FirebaseAuth.getInstance();
        fuser       = auth.getCurrentUser();
        database    = FirebaseDatabase.getInstance();
        storage     = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);


        //init UI views
        add_ImgProfile      = (CircleImageView) findViewById(R.id.add_ImageProfileSeller);
        image_GPS           = (ImageView) findViewById(R.id.gps);
        edt_Name            = (EditText) findViewById(R.id.name_SellerRegister);
        edit_ShopName       = (EditText) findViewById(R.id.name_Shop);
        edt_Email           = (EditText) findViewById(R.id.email_SellerRegister);
        edt_Address         = (EditText) findViewById(R.id.address_Shop);
        edt_Phone           = (EditText) findViewById(R.id.phone_SellerRegister);
        edt_FeeDelivery     = (EditText) findViewById(R.id.fee_Delivery);
        edt_Pass            = (EditText) findViewById(R.id.pass_SellerRegister);
        edt_RePass          = (EditText) findViewById(R.id.rePass_SellerRegister);
        btn_SellerSignUp    = (Button) findViewById(R.id.btn_SellerSignUp);

        //init permission
        location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        //
        image_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocalPermission()) {
                    //allowed
                    detectLocation();
                } else {
                    //not allowed
                    requestLocationPermission();
                }
            }
        });

        add_ImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE_STORAGE_PICKER);
            }
        });

        //
        btn_SellerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                inputData();
            }
        });
    }
    String img_Url , name , email , phone , address_Shop , delivery , pass , re_pass  , shop_name;
    private void inputData() {
        name = edt_Name.getText().toString().trim();
        email = edt_Email.getText().toString().trim();
        phone = edt_Phone.getText().toString().trim();
        address_Shop = edt_Address.getText().toString().trim();
        delivery = edt_FeeDelivery.getText().toString().trim();
        pass = edt_Pass.getText().toString().trim();
        re_pass = edt_RePass.getText().toString();
        shop_name = edit_ShopName.getText().toString().trim();

        //Check conditions
        if(TextUtils.isEmpty(name)){
            edt_Name.setError("Điền tên!");
        }else if(TextUtils.isEmpty(email)){
            edt_Email.setError("Điền email!");
        }else if(!email.matches(emailPattern)){
            edt_Email.setError("Điền đúng email!");
        }else if(TextUtils.isEmpty(phone)){
            edt_Phone.setError("Nhập số điện thoại!");
        }else if(TextUtils.isEmpty(address_Shop)){
            edt_Address.setError("Chọn địa chỉ!");
        }else if(TextUtils.isEmpty(delivery)){
            edt_FeeDelivery.setError("Nhập phí giao hàng!");
        }else if(TextUtils.isEmpty(pass)){
            edt_Pass.setError("Nhập mật khẩu!");
        }else if(TextUtils.isEmpty(re_pass)){
            edt_RePass.setError("Nhập lại mật khẩu!");
        }else if(pass.length()<6){
            edt_Pass.setError("Mật khẩu > 6 ký tự!");
        }else if(!pass.matches(re_pass)){
            edt_RePass.setError("Mật khẩu không khớp");
        }else {
            setDataFireBaseDataBase();
        }


    }

    private void setDataFireBaseDataBase() {
        auth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    saveData();
                }else {
                    Toast.makeText(SellerRegisterActivity.this , "" + task.getException().getMessage() , Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void saveData() {
        String timeStamp  = ""+System.currentTimeMillis();
        reference = database.getReference().child("Users").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("uploadImage").child(auth.getUid());

        if(img_Uri!=null){

            storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                img_Url = uri.toString();

                                HashMap<String , Object> map = new HashMap<>();
                                map.put("uId" , auth.getUid());
                                map.put("img_Profile" , img_Url);
                                map.put("name" , name);
                                map.put("shopName" , shop_name);
                                map.put("email" , email);
                                map.put("phone" , phone);
                                map.put("address_shop" , address_Shop);
                                map.put("delivery" , delivery);
                                map.put("latitue" , latitue);
                                map.put("longtitue" , longtitue);
                                map.put("timeStamp" , timeStamp);
                                map.put("accountType" , "Seller");
                                map.put("online" , "true");
                                map.put("open" , "true");

                                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(email.equals(database.getReference("Users").orderByChild("email"))){
                                            dialog.dismiss();
                                            edt_Email.setError("Email tồn tại !");
                                        }else if(task.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(SellerRegisterActivity.this , "Đăng ký thành công", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SellerRegisterActivity.this, MainSellerActivity.class));
                                            finish();
                                        }else {
                                            dialog.dismiss();
                                            Toast.makeText(SellerRegisterActivity.this , "Lỗi đăng ký . Thử lai !", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else {
            img_Url = "https://firebasestorage.googleapis.com/v0/b/grocery-store-bdcd0.appspot.com/o/uploadImage%2Fprofile.png?alt=media&token=200fd6f6-b62e-438b-ad5c-7cf49953c03d";
            HashMap<String , Object> map = new HashMap<>();
            map.put("uId" , auth.getUid());
            map.put("img_Profile" , img_Url);
            map.put("name" , name);
            map.put("email" , email);
            map.put("phone" , phone);
            map.put("address_shop" , address_Shop);
            map.put("delivery" , delivery);
            map.put("latitue" , latitue);
            map.put("longtitue" , longtitue);
            map.put("timeStamp" , timeStamp);
            map.put("accountType" , "Người bán");
            map.put("online" , "true");
            map.put("open" , "true");
            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(email.equals(database.getReference("Users").orderByChild("email"))){
                            dialog.setCanceledOnTouchOutside(true);
                            edt_Email.setError("Email tồn tại !");
                        }else if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(SellerRegisterActivity.this , "Đăng ký thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SellerRegisterActivity.this, MainSellerActivity.class));
                            finish();
                        }
                    }
                }
            });
        }
    }

    private boolean checkLocalPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, location, LOCATION_REQUEST);
    }


    public void detectLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitue = location.getLatitude();
        longtitue = location.getLongitude();
        findAdress();
    }

    private void findAdress() {
        Geocoder geocoder;
        List<Address> list ;
        geocoder = new Geocoder(this , Locale.getDefault());

        try {
            list = geocoder.getFromLocation(latitue , longtitue , 1);
            String address = list.get(0).getAddressLine(0);
            String city = list.get(0).getLocality();
            String state = list.get(0).getAdminArea();
            String country = list.get(0).getCountryName();

            edt_Address.setText(address);
         }catch (Exception e){
            Toast.makeText(this , "" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:{
                if(grantResults.length>0){
                    boolean locationAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccept){
                        //allowed
                        detectLocation();
                    }else {
                        //denied

                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_STORAGE_PICKER){
            if(data!=null){
                img_Uri = data.getData();
                add_ImgProfile.setImageURI(img_Uri);
            }
        }
    }
}