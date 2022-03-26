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
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity implements LocationListener{
    private CircleImageView add_ImgProfile ;
    private EditText edt_Name , edt_Email , edt_Phone , edt_Address , edt_Pass , edt_RePass ;
    private Button btn_SignUp ;
    private TextView txt_AlreadyAccount , txt_SellerRegister;
    private ImageView img_GPS;


    private LocationManager locationManager;
    private double latitue, longtitue;

    public static final int LOCATION_REQUEST = 100;

    public static final int IMAGE_STORAGE_PICKER = 114;
    private String[] location;

    private Uri img_Uri;
    ProgressDialog dialog ;


    String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseUser fuser ;
    DatabaseReference reference ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth        = FirebaseAuth.getInstance();
        fuser       = auth.getCurrentUser();
        database    = FirebaseDatabase.getInstance();
        storage     = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        //init permission
        location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        //init UI views
        add_ImgProfile      = (CircleImageView) findViewById(R.id.add_ImageRegister);
        edt_Name            = (EditText) findViewById(R.id.name_Register);
        edt_Email           = (EditText) findViewById(R.id.email_Register);
        edt_Phone           = (EditText) findViewById(R.id.phone_Register);
        edt_Address         = (EditText) findViewById(R.id.address_Register);
        edt_Pass            = (EditText) findViewById(R.id.pass_Register);
        edt_RePass          = (EditText) findViewById(R.id.rePass_Register);
        btn_SignUp          = (Button) findViewById(R.id.btn_SignUp);
        txt_AlreadyAccount  = (TextView) findViewById(R.id.txt_AlreadyAccount);
        txt_SellerRegister  = (TextView) findViewById(R.id.txt_SellerRegister);
        img_GPS             = (ImageView) findViewById(R.id.register_gps);

        txt_SellerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, SellerRegisterActivity.class));
                finish();
            }
        });
        //
        txt_AlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        //
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
        img_GPS.setOnClickListener(new View.OnClickListener() {
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
        //
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData();
            }
        });
    }
    String img_Url , name , email , phone , address , pass , re_pass , timeStamp;

    private void setData() {
        timeStamp = ""+System.currentTimeMillis();
        name = edt_Name.getText().toString().trim();
        email = edt_Email.getText().toString().trim();
        phone = edt_Phone.getText().toString().trim();
        address = edt_Address.getText().toString().trim();
        pass = edt_Pass.getText().toString().trim();
        re_pass = edt_RePass.getText().toString().trim();

        //Check conditions
        if(TextUtils.isEmpty(name)){
            edt_Name.setError("Nhập tên!");
        }else if(TextUtils.isEmpty(email)){
            edt_Email.setError("Nhập email!");
        }else if(!email.matches(emailPattern)){
            edt_Email.setError("Nhập đúng email!");
        }else if(TextUtils.isEmpty(phone)){
            edt_Phone.setError("Nhập số điện thoại!");
        }else if (TextUtils.isEmpty(pass) || pass.length()<6){
            edt_Pass.setError("Nhâp mật khẩu > 6");
        }else if(!pass.matches(re_pass)){
            edt_RePass.setError("Mật khẩu không khớp!");
        }else {
            dialog.show();
            saveDataFireBase();
        }
    }

    private void saveDataFireBase() {
        auth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    saveData();
                }else {
                    Toast.makeText(RegisterActivity.this , "" +task.getException().getMessage() , Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void saveData() {
        reference = database.getReference().child("Users").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("uploadImage").child(auth.getUid());
        if(img_Uri!=null){
            storageReference.putFile(img_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                img_Url = uri.toString();

                                HashMap<String , Object> map = new HashMap<>();
                                map.put("uId" , auth.getUid());
                                map.put("name" , name);
                                map.put("img_Profile" , img_Url);
                                map.put("email" , email);
                                map.put("phone" , phone);
                                map.put("address" , address);
                                map.put("timeStamp" , timeStamp);
                                map.put("accountType" , "User");
                                map.put("online" , "true");
                                map.put("latitude" , latitue);
                                map.put("longtitue" , longtitue);

                                 reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             if(email.equals(database.getReference("Sellers").orderByChild("email"))){
                                                 dialog.dismiss();
                                                 edt_Email.setError("Email tồn tại !");
                                             }else if(task.isSuccessful()){
                                                 dialog.dismiss();
                                                 Toast.makeText(RegisterActivity.this , "Đăng ký thành công", Toast.LENGTH_LONG).show();
                                                 startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                                                 finish();
                                             }else {
                                                 dialog.dismiss();
                                                 Toast.makeText(RegisterActivity.this , "Lỗi đăng ký . Thử lai !", Toast.LENGTH_LONG).show();
                                             }
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
            map.put("name" , name);
            map.put("img_Profile" , img_Url);
            map.put("email" , email);
            map.put("phone" , phone);
            map.put("address" , address);
            map.put("timeStamp" , timeStamp);
            map.put("accountType" , "Users");
            map.put("online" , "true");
            map.put("latitude" , latitue);
            map.put("longtitue" , longtitue);

            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(email.equals(database.getReference("Users").orderByChild("email"))){
                            dialog.setCanceledOnTouchOutside(true);
                            edt_Email.setError("Email tồn tại !");
                        }else if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this , "Đăng ký thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
                            finish();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this , ""+task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
