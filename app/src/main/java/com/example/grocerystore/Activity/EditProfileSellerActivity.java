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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileSellerActivity extends AppCompatActivity implements LocationListener {
    private CircleImageView img_Profile ;
    private EditText edt_Email , edt_ShopName , edt_Phone , edt_Address , edt_DeliveryFee ;
    private ImageView back , gps ;
    private Button btn_Update ;

    private LocationManager locationManager;
    private double latitue, longtitue;

    public static final int LOCATION_REQUEST = 100;

    public static final int IMAGE_STORAGE_PICKER = 114;
    private String[] location;

    private Uri img_Uri;
    ProgressDialog dialog ;
    String img_Url ;


    String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseUser fuser ;
    DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_seller);

        auth        = FirebaseAuth.getInstance();
        fuser       = auth.getCurrentUser();
        database    = FirebaseDatabase.getInstance();
        storage     = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //init permission
        location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        //Init UI views
        img_Profile         = (CircleImageView) findViewById(R.id.add_ImageSellerEdit);
        edt_ShopName        = (EditText) findViewById(R.id.name_ShopEdit);
        edt_Email           = (EditText) findViewById(R.id.email_EditSeller);
        edt_Phone           = (EditText) findViewById(R.id.phone_EditSeller);
        edt_Address         = (EditText) findViewById(R.id.address_ShopEdit);
        edt_DeliveryFee     = (EditText) findViewById(R.id.fee_DeliverEdit);
        back                = (ImageView) findViewById(R.id.back_EditSellerProfile);
        gps                 = (ImageView) findViewById(R.id.editSeller_gps);
        btn_Update          = (Button) findViewById(R.id.update_EditSeller);


        loadProfileSeller();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileSellerActivity.this , MainSellerActivity.class));
                finish();
            }
        });

        img_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE_STORAGE_PICKER);
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
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

        btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                setData();
            }
        });
    }
    private void loadProfileSeller(){
        DatabaseReference reference = database.getReference("Users");
        reference.orderByChild("uId").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dialog.dismiss();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            edt_Email.setText(""+ds.child("email").getValue());
                            edt_Address.setText(""+ds.child("address_shop").getValue());
                            edt_Phone.setText(""+ds.child("phone").getValue());
                            edt_ShopName.setText(""+ds.child("shop_name").getValue());
                            edt_DeliveryFee.setText(""+ds.child("delivery").getValue());
                            String img_profile = ""+ds.child("img_Profile").getValue();

                            Picasso.get().load(img_profile).into(img_Profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String shopName , email , phone , address , fee_Delivery;
    private void setData() {
        String timeStamp = ""+System.currentTimeMillis();
        email = edt_Email.getText().toString().trim();
        phone = edt_Phone.getText().toString().trim();
        address = edt_Address.getText().toString().trim();
        shopName = edt_ShopName.getText().toString().trim();
        fee_Delivery = edt_DeliveryFee.getText().toString().trim();

        DatabaseReference reference = database.getReference("Users");
        StorageReference storageReference = storage.getReference("imageProfile").child(timeStamp);

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
                                map.put("email" , email);
                                map.put("phone" , phone);
                                map.put("address_shop" , address);
                                map.put("img_Profile" , img_Url);
                                map.put("shop_name" , shopName);
                                map.put("delivery" , fee_Delivery);

                                reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialog.dismiss();
                                            startActivity(new Intent(EditProfileSellerActivity.this , MainSellerActivity.class));
                                            finish();
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
            map.put("shop_name" , shopName);
            map.put("email" , email);
            map.put("phone" , phone);
            map.put("address" , address);
            map.put("img_Profile" , img_Url);

            reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        startActivity(new Intent(EditProfileSellerActivity.this , MainSellerActivity.class));
                        finish();
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
                img_Profile.setImageURI(img_Uri);
            }
        }
    }
}