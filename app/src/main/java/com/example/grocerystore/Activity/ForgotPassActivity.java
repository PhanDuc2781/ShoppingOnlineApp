package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private EditText edt_EmailReceive;
    private Button btn_ReceivePass ;
    private ImageView img_Back ;

    ProgressDialog dialog ;
    FirebaseAuth auth ;
    String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_pass);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        auth = FirebaseAuth.getInstance();
        

        //init UI views
        edt_EmailReceive = (EditText) findViewById(R.id.email_Receive);
        btn_ReceivePass = (Button) findViewById(R.id.btn_ReceivePass);
        img_Back = (ImageView) findViewById(R.id.back_Login);

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassActivity.this , LoginActivity.class));
                finish();
            }
        });
        
        btn_ReceivePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                receivePass();
            }
        });
    }
    String email ;
    private void receivePass() {
        email = edt_EmailReceive.getText().toString().trim();
        if(!email.matches(emailPattern)){
            edt_EmailReceive.setError("Nhập đúng email!");
        }else {
            auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    Toast.makeText(ForgotPassActivity.this  , "Đã gửi yêu cầu đến email của" , Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ForgotPassActivity.this  , ""+e.getMessage() , Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}