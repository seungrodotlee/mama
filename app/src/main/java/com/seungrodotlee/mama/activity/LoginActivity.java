package com.seungrodotlee.mama.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.seungrodotlee.mama.MamaActivity;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.databinding.ActivityLoginBinding;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoginActivity extends MamaActivity {
    private ActivityLoginBinding binding;
    private String email, pw;

    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setActivity(this);
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

    public void login(View v) {
        email = binding.loginEmailInput.getText().toString();
        pw = binding.loginPwInput.getText().toString();

        binding.loginEmailLabel.setTextColor(getResources().getColor(R.color.white));
        binding.loginPwLabel.setTextColor(getResources().getColor(R.color.white));

        if(email.equals("")) {
            binding.loginEmailLabel.setTextColor(getResources().getColor(R.color.exlusive));
        } else if(pw.equals("")) {
            binding.loginPwLabel.setTextColor(getResources().getColor(R.color.exlusive));
        } else {
            global.getAuth().signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        SharedPreferences.Editor e = global.getPreferencesEditor();

                        e.putString(MamaGlobal.PreferencesKey.USER_STATUS, global.USER_SIGNED_IN);
                        e.commit();
                        finish();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        binding.loginErrorLabel.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void googleLogin(View v) {
        Intent intent = global.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public void anonymousLogin(View v) {
        global.setUserName(binding.loginNameInput.getText().toString());

        SharedPreferences.Editor e = global.getPreferencesEditor();

        e.putString(MamaGlobal.PreferencesKey.USER_STATUS, global.USER_SIGNED_IN);
        e.commit();
        finish();
    }

    public void openRegistry(View v) {
        Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
        finish(FINISH_WITH_NEW_ACTIVITY);
        startActivity(intent);
    }
}
