package com.seungrodotlee.mama.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.seungrodotlee.mama.MamaActivity;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;

import static android.content.ContentValues.TAG;

public class RegistryActivity extends MamaActivity {
    private TextView nameLabel, emailLabel, pwLabel, pwValidLabel;
    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry, R.id.registry_layout);

        LinearLayout inputForm = (LinearLayout) findViewById(R.id.registry_input_form);
        nameLabel = (TextView) findViewById(R.id.registry_name_label);
        emailLabel = (TextView) findViewById(R.id.registry_email_label);
        pwLabel = (TextView) findViewById(R.id.registry_pw_label);
        pwValidLabel =(TextView) findViewById(R.id.registry_pw_valid_label);
        final EditText nameInput = (EditText) findViewById(R.id.registry_name_input);
        final EditText emailInput = (EditText) findViewById(R.id.registry_email_input);
        final EditText pwInput = (EditText) findViewById(R.id.registry_pw_input);
        final EditText pwValidInput = (EditText) findViewById(R.id.registry_pw_valid_input);
        Button registBtn = (Button) findViewById(R.id.registry_regist_btn);
        Button googleLoginBtn = (Button) findViewById(R.id.registry_google_login_btn);
        Button startWithoutLoginBtn = (Button) findViewById(R.id.registry_start_without_login_btn);

        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String pw = pwInput.getText().toString();
                String pwValid = pwValidInput.getText().toString();

                nameLabel.setTextColor(getResources().getColor(R.color.white));
                emailLabel.setTextColor(getResources().getColor(R.color.white));
                pwLabel.setTextColor(getResources().getColor(R.color.white));
                pwValidLabel.setTextColor(getResources().getColor(R.color.white));

                if(userName.equals("")) {
                    nameLabel.setTextColor(getResources().getColor(R.color.exlusive));
                } else if(email.equals("")) {
                    emailLabel.setTextColor(getResources().getColor(R.color.exlusive));
                } else if(pw.equals("")) {
                    pwLabel.setTextColor(getResources().getColor(R.color.exlusive));
                } else if(!pw.equals(pwValid)) {
                    pwValidLabel.setTextColor(getResources().getColor(R.color.exlusive));
                } else if(pw.equals(pwValid)) {
                    global.getAuth().createUserWithEmailAndPassword(emailInput.getText().toString(), pwInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                finish();
                                String uid = global.getUser().getUid();

                                global.setUserName(userName);

                                SharedPreferences.Editor e = global.getPreferencesEditor();

                                e.putString(MamaGlobal.PreferencesKey.USER_STATUS, global.USER_SIGNED_IN);
                                e.commit();

                                global.getUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userName).build());

                                DatabaseReference ref = global.getDatabase().getReference("users");
                                ref.child(uid).child("name").setValue(userName);
                            } else {
                                Toast.makeText(getApplicationContext(), "앗! 오류에요. 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = global.getGoogleSignInClient().getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        startWithoutLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = nameInput.getText().toString();
                nameLabel.setTextColor(getResources().getColor(R.color.white));

                if(userName.equals("")) {
                    Log.d(TAG, "no userName");
                    nameLabel.setTextColor(getResources().getColor(R.color.exlusive));
                } else {
                    global.setUserName(userName);

                    SharedPreferences.Editor e = global.getPreferencesEditor();

                    e.putString(MamaGlobal.PreferencesKey.USER_STATUS, global.USER_ANONYMOUS);
                    e.commit();

                    finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                finish();
            } catch (ApiException e) {

            }
        }
    }
}
