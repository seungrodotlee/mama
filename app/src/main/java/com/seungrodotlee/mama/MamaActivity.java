package com.seungrodotlee.mama;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.seungrodotlee.mama.util.MamaFragmentFactory;

public class MamaActivity extends AppCompatActivity {
    public static final int ACTIVITY_IN = 1;
    public static final int ACTIVITY_OUT = 0;
    public static final int FINISH_WITH_NEW_ACTIVITY = 1;
    public static final int FINISH_WITHOUT_NEW_ACTIVTY = 0;

    protected MamaGlobal global;
    protected MamaFragmentFactory fragmentFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        global = MamaGlobal.getInstance(getApplicationContext());
        fragmentFactory = MamaFragmentFactory.getInstance(getApplicationContext());
        fragmentFactory.setFragmentManager(getSupportFragmentManager());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        view.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void startActivity(Intent intent) {
        super.startActivity(intent);

        overridePendingTransition(R.anim.activity_transition_in, R.anim.activity_transition_out);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_transition_back_in, R.anim.activity_transition_back_out);
    }

    public void finish(int Mode) {
        super.finish();

        if(Mode == FINISH_WITH_NEW_ACTIVITY) {

        }

        if(Mode == FINISH_WITHOUT_NEW_ACTIVTY) {
            overridePendingTransition(R.anim.activity_transition_back_in, R.anim.activity_transition_back_out);
        }
    }

    public void setContentView(int layoutResID, int viewID) {
        super.setContentView(layoutResID);

        View view = findViewById(viewID);
        view.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    public void setLayoutPadding(View view) {
        view.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    public int getStatusBarHeight() {
        int statusHeight = 0;

        int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            statusHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }

        return statusHeight;
    }

    public Point getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        return size;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        global.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Authentication Successed.", Toast.LENGTH_SHORT).show();

                            String userName = global.getUser().getDisplayName();
                            String uid = global.getUser().getUid();

                            SharedPreferences.Editor e = global.getPreferencesEditor();

                            if(userName.equals("")) {
                                userName = "무명 사용자";
                            }

                            global.setUserName(userName);

                            e.putString(MamaGlobal.PreferencesKey.USER_STATUS, global.USER_SIGNED_IN);
                            e.commit();

                            DatabaseReference ref = global.getDatabase().getReference("users");
                            ref.child(uid).child("name").setValue(userName);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
