package com.example.grocerystore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_Email , edt_Pass ;
    private TextView txt_ForgotPass , txt_NoAccount ;
    private Button btn_Login ;

    FirebaseAuth auth ;
    ProgressDialog dialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        //init UI Views
        edt_Email       = (EditText) findViewById(R.id.email_Login);
        edt_Pass        = (EditText) findViewById(R.id.pass_Login);
        txt_ForgotPass  = (TextView) findViewById(R.id.txt_ForgotPass);
        txt_NoAccount   = (TextView) findViewById(R.id.txt_NoAccount);
        btn_Login       = (Button) findViewById(R.id.btn_Login);


        //NoAccount click to create new account
        txt_NoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this , RegisterActivity.class));
                finish();
            }
        });

        //Forgot PassWord
        txt_ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
                finish();
            }
        });

        //Click button to SignIn
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                login();
            }
        });
    }
    String  email , pass ;
    private void login() {
        email = edt_Email.getText().toString().trim();
        pass = edt_Pass.getText().toString().trim();

        auth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserType();
                }else {
                    Toast.makeText(LoginActivity.this , "Đăng nhập thất bại" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkUserType() {
         String type ;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uId").equalTo(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String accountType = ""+ds.child("accountType").getValue();
                    if(accountType.equals("Seller")){
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this , MainSellerActivity.class));
                        finish();
                        makeMeOnline();
                    }
                    if(accountType .equals("User")){
                        dialog.dismiss();
                        startActivity(new Intent(LoginActivity.this , MainUserActivity.class));
                        finish();
                        makeMeOnline();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot s:snapshot.getChildren()){
//                            type = ""+s.child("accountType").getValue();
//                            if(accountType.equals("Seller")){
//                                dialog.dismiss();
//                                startActivity(new Intent(LoginActivity.this , MainSellerActivity.class));
//                                finish();
//                                makeMeOnline();
//                            }
//                            if(accountType .equals("User")){
//                                dialog.dismiss();
//                                startActivity(new Intent(LoginActivity.this , MainUserActivity.class));
//                                finish();
//                                makeMeOnline();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
    }


    private void makeMeOnline() {
        HashMap<String  , Object> map = new HashMap<>();
        map.put("online" , "true");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(auth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }else {

                }
            }
        });

    }

}