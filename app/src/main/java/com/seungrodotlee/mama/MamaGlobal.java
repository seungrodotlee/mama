package com.seungrodotlee.mama;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seungrodotlee.mama.data.MakerData;
import com.seungrodotlee.mama.data.ProjectData;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MamaGlobal {
    private ArrayList<ProjectData> projectDataList = new ArrayList<ProjectData>();

    public ArrayList<ProjectData> getProjectDataList() {
        return projectDataList;
    }

    public void setProjectDataList(ArrayList<ProjectData> projectDataList) {
        this.projectDataList = projectDataList;
    }

    public void addProjectDataItem(ProjectData item) {
        projectDataList.add(item);
    }

    public void updateProjectDataItem(int position, ProjectData item) {
        projectDataList.set(position, item);
    }

    public void removeProjectDataItem(int position) {
        projectDataList.remove(position);
    }

    public void resetProjectDataList() {
        projectDataList = new ArrayList<ProjectData>();
    }

    public void mergeProjectDataList(ArrayList<ProjectData> list) {
        projectDataList.addAll(list);
    }

    private ArrayList<MakerData> scheduleDataList = new ArrayList<MakerData>();

    public ArrayList<MakerData> getScheduleDataList() {
        return scheduleDataList;
    }

    public void setScheduleDataList(ArrayList<MakerData> scheduleDataList) {
        this.scheduleDataList = scheduleDataList;
    }

    public void addScheduleDataItem(MakerData item) {
        scheduleDataList.add(item);
    }

    public void updateScheduleDataItem(int position, MakerData item) {
        scheduleDataList.set(position, item);
    }

    public void removeScheduleDataItem(int position) {
        scheduleDataList.remove(position);
    }

    public void resetScheduleDataList() {
        scheduleDataList = new ArrayList<MakerData>();
    }

    public void mergeScheduleDataList(ArrayList<MakerData> list) {
        scheduleDataList.addAll(list);
    }

    private ArrayList<MakerData> goalDataList = new ArrayList<MakerData>();

    public ArrayList<MakerData> getGoalDataList() {
        return goalDataList;
    }

    public void setGoalDataList(ArrayList<MakerData> goalDataList) {
        this.goalDataList = goalDataList;
    }

    public void addGoalDataItem(MakerData item) {
        goalDataList.add(item);
    }

    public void updateGoalDataItem(int position, MakerData item) {
        goalDataList.set(position, item);
    }

    public void removeGoalDataItem(int position) {
        goalDataList.remove(position);
    }

    public void resetGoalDataList() {
        goalDataList = new ArrayList<MakerData>();
    }

    public void mergeGoalDataList(ArrayList<MakerData> list) {
        goalDataList.addAll(list);
    }

    public static class PreferencesKey {
        public static final String USER_STATUS = "userStatus";
        public static final String USER_NAME = "userName";
    }

    public static final String USER_SIGNED_IN = "signed up";
    public static final String USER_ANONYMOUS = "anonymous";
    public static final String USER_LOGGED_OUT = "logged out";
    public static final String USER_NEW = "new";

    private static MamaGlobal instance = null;
    private Context context;

    private MamaGlobal(Context context) {
        this.context = context;
    }

    public static synchronized MamaGlobal getInstance(Context context) {

        if (null == instance) {
            instance = new MamaGlobal(context);
        }

        return instance;
    }

    public static synchronized MamaGlobal getInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    public Context getContext() {
        return context;
    }

    public String pkgName;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    private FirebaseAuth auth;

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getUserDatabaseReference() {
        if(getUser() == null) {
            return null;
        }

        return getDatabase().getReference("users").child(getUser().getUid());
    }

    private FirebaseDatabase database;

    public boolean isUserLogin() {
        if(getAuth().getCurrentUser() != null) {
            return true;
        }

        return false;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    private FirebaseUser user;

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getPreferencesEditor() {
        return preferences.edit();
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    private SharedPreferences preferences;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;

        Log.d("TAG", "username = " + name);

        SharedPreferences.Editor e = this.getPreferencesEditor();

        e.putString(MamaGlobal.PreferencesKey.USER_NAME, name);
        e.commit();

        if (getAuth().getCurrentUser() != null) {
            setUser(getAuth().getCurrentUser());

            DatabaseReference ref = getDatabase().getReference("users");
            ref.child(getUser().getUid()).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("TAG", "유저이름 저장 '성공' " + userName);
                }
            });
        }
    }

    public void updateUserName(String name) {
        this.userName = name;

        Log.d("TAG", "username = " + name);

        SharedPreferences.Editor e = this.getPreferencesEditor();

        e.putString(MamaGlobal.PreferencesKey.USER_NAME, name);
        e.commit();

        if (getAuth().getCurrentUser() != null) {
            Log.d("TAG", "user = " + getAuth().getCurrentUser().toString());
            DatabaseReference ref = getDatabase().getReference("users");
            ref.child(getUser().getUid()).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("TAG", "유저이름 저장 '성공' " + userName);
                }
            });
        }
    }

    String userName;

    public int dpToPx(float dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, signInOptions);
    }

    public int getScreenHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public String removeSeperator(String text) {
        return text.replaceAll("(\r\n|\r|\n|\n\r|\t)", " ").trim();
    }

    public void init() {
        this.setPkgName(context.getPackageName());
        this.setAuth(FirebaseAuth.getInstance());
        this.setDatabase(FirebaseDatabase.getInstance());
        this.setPreferences(context.getSharedPreferences("mama_preference", MODE_PRIVATE));
    }
}
