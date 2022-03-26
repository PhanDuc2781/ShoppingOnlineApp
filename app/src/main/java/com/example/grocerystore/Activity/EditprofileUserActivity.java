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
import android.widget.TextView;
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

public class EditprofileUserActivity extends AppCompatActivity implements LocationListener {
    private CircleImageView img_Profile ;
    private TextView txt_Name , txt_Email , txt_Phone , txt_Address ;
    private ImageView img_Back , img_GPS;
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
        setContentView(R.layout.activity_editprofile_user);

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

        //init UI views
        img_Profile     = (CircleImageView) findViewById(R.id.add_ImageUserEdit);
        img_Back        = (ImageView) findViewById(R.id.back_EditUserProfile);
        img_GPS         = (ImageView) findViewById(R.id.editUser_gps);
        txt_Name        = (EditText) findViewById(R.id.name_EditUser);
        txt_Email       = (EditText) findViewById(R.id.email_EditUser);
        txt_Phone       = (EditText) findViewById(R.id.phone_EditUser);
        txt_Address     = (EditText) findViewById(R.id.address_EditUser);
        btn_Update      = (Button) findViewById(R.id.update_EditUser);

        loadProfileUser();

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditprofileUserActivity.this , MainUserActivity.class));
                finish();
            }
        });

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

        img_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE_STORAGE_PICKER);
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

    private void loadProfileUser(){
        DatabaseReference reference = database.getReference("Users");
        reference.orderByChild("uId").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dialog.dismiss();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            txt_Email.setText(""+ds.child("email").getValue());
                            txt_Name.setText(""+ds.child("name").getValue());
                            txt_Address.setText(""+ds.child("address").getValue());
                            txt_Phone.setText(""+ds.child("phone").getValue());
                            Picasso.get().load(ds.child("img_Profile").getValue().toString()).into(img_Profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String name , email , phone , address ;
    private void setData() {
        name = txt_Name.getText().toString().trim();
        email = txt_Email.getText().toString().trim();
        phone = txt_Phone.getText().toString().trim();
        address = txt_Address.getText().toString().trim();

        DatabaseReference reference = database.getReference("Users");
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
                                map.put("name" , name);
                                map.put("email" , email);
                                map.put("phone" , phone);
                                map.put("address" , address);
                                map.put("img_Profile" , img_Url);

                                reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialog.dismiss();
                                            startActivity(new Intent(EditprofileUserActivity.this , MainUserActivity.class));
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
            map.put("name" , name);
            map.put("email" , email);
            map.put("phone" , phone);
            map.put("address" , address);
            map.put("img_Profile" , img_Url);

            reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        startActivity(new Intent(EditprofileUserActivity.this , MainUserActivity.class));
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
            String city = list.get(0).getLocality();
            String state = list.get(0).getAdminArea();
            String country = list.get(0).getCountryName();

            txt_Address.setText(address);
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