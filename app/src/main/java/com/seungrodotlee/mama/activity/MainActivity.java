package com.seungrodotlee.mama.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.seungrodotlee.mama.MamaActivity;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.data.MakerData;
import com.seungrodotlee.mama.data.Post;
import com.seungrodotlee.mama.data.ProjectData;
import com.seungrodotlee.mama.databinding.ActivityMainBinding;
import com.seungrodotlee.mama.dialog.MamaAlertDialog;
import com.seungrodotlee.mama.util.MamaFragmentFactory;
import com.seungrodotlee.mama.util.TypeWriter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends MamaActivity {
    private ActivityMainBinding binding;
    SharedPreferences preferences;
    Boolean isUserRegist = false;
    private boolean gotoHome = false;
    private boolean firstDraw = true;

    public static final Integer[] menuIdList = {R.id.nav_menu_home, R.id.nav_menu_calendar, R.id.nav_menu_marker, R.id.nav_menu_portfolio, R.id.nav_menu_setting};

    public BottomNavigationView getNavigation() {
        return navigation;
    }

    private BottomNavigationView navigation;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        setLayoutPadding(binding.getRoot());

        appInit();

        global.getAuth().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gotoHome) {
            fragmentFactory.changeFragment(MamaFragmentFactory.FRAGMENT_HOME);
            gotoHome = false;
        }
    }

    private void appInit() {
        global.init();

        preferences = global.getPreferences();
        String userStatusTemp = preferences.getString(MamaGlobal.PreferencesKey.USER_STATUS, "");

        if (userStatusTemp.equals("")) {
            isUserRegist = false;
        } else {
            isUserRegist = true;

            global.setUserName(preferences.getString(MamaGlobal.PreferencesKey.USER_NAME, ""));
        }
    }

    private void fragmentInit() {
        Log.d("TAG", "fragment init call");

        for(int i = 0; i < MamaFragmentFactory.fragmentTitles.length; i++) {
            fragmentFactory.addFragment(i);
        }

        for (int i = 1; i < MamaFragmentFactory.fragmentTitles.length; i++) {
            fragmentFactory.registryFragment(i);
        }

        fragmentFactory.addFragment(0);
        fragmentFactory.registryFragment(0);

        navigation = (BottomNavigationView) findViewById(R.id.main_navigation_bar);
        navigation.setElevation(10);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int index = Arrays.asList(menuIdList).indexOf(item.getItemId());
                fragmentFactory.changeFragment(index);

                return false;
            }
        });
    }

    private void splashAnimation() {
        final TextView logo = (TextView) findViewById(R.id.main_logo);

        final TypeWriter typeWriter = new TypeWriter(logo, "mama");
        typeWriter.start(500);

        Animation logoAnim = new TranslateAnimation(0, 0, (float) (getScreenSize().y * 0.44), (global.dpToPx(8)));
        logoAnim.setDuration(1500);
        logoAnim.setStartOffset(1700);
        logoAnim.setFillAfter(true);
        logoAnim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), R.anim.logo_interpolator));
        logo.setAnimation(logoAnim);

        ValueAnimator logoColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.highTheme), getResources().getColor(R.color.white));
        logoColorAnim.setDuration(1000);
        logoColorAnim.setStartDelay(1800);
        logoColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                logo.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        logoColorAnim.start();

        LinearLayout splashBg = (LinearLayout) findViewById(R.id.splash_bg);

        int splashParts = splashBg.getChildCount();

        Animation[] splashAnimArray = new Animation[splashParts];

        for (int i = 0; i < splashParts; i++) {
            final LinearLayout current = (LinearLayout) splashBg.getChildAt(i);
            long startOffset = 2000 + (i * 50);

            splashAnimArray[i] = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_bg_anim);
            splashAnimArray[i].setFillAfter(true);
            splashAnimArray[i].setStartOffset(startOffset);
            current.startAnimation(splashAnimArray[i]);

            ValueAnimator splashBackColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(R.color.white), getResources().getColor(R.color.highTheme));
            splashBackColorAnim.setDuration(800);
            splashBackColorAnim.setStartDelay(startOffset);
            splashBackColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    current.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });

            splashBackColorAnim.start();

            if (i == splashParts - 1) {
                splashAnimArray[i].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.d("MA", "애니메이션 끝");
                        handler = new Handler();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (!isUserRegist) {
                                    Intent intent = new Intent(getApplicationContext(), FirstTimeActivity.class);
                                    startActivity(intent);
                                } else {

                                }
                            }
                        };

                        handler.postDelayed(runnable, 1000);
                    }
                });
            }
        }
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (global.getAuth().getCurrentUser() != null) {
                global.setUser(global.getAuth().getCurrentUser());
                global.resetProjectDataList();
                global.resetScheduleDataList();
                global.resetGoalDataList();
                DatabaseReference ref = global.getUserDatabaseReference();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        if(post.getProjects() != null) {
                            ArrayList<ProjectData> projects = post.getProjects();

                            global.setProjectDataList(projects);
                        }

                        if(firstDraw) {
                            fragmentInit();
                            splashAnimation();
                            firstDraw = false;
                        }

                        if(post.getSchedules() != null) {
                            Log.d("TAG", "schedule size = " + post.getSchedules().size());
                            ArrayList<MakerData> schedules = post.getSchedules();

                            global.setScheduleDataList(schedules);
                        }

                        if(post.getGoals() != null) {
                            ArrayList<MakerData> goals = post.getGoals();

                            global.setGoalDataList(goals);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                global.setUser(global.getAuth().getCurrentUser());
                Log.d("TAG", "username = " + global.getUserName());
                binding.mainUserStatusDot.setImageDrawable(getDrawable(R.drawable.dot_pos));

                global.getUserDatabaseReference().child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!firstDraw) {
                            fragmentFactory.changeFragment(MamaFragmentFactory.FRAGMENT_HOME);
                        }

                        final String name = dataSnapshot.getValue().toString();

                        if(!dataSnapshot.getValue().equals(global.getUserName())) {
                            final MamaAlertDialog dialog = MamaAlertDialog.newInstance("기기에 저장된 이름과 로그인 한 계정의 이름이 다릅니다.\n로그인 한 계정의 정보로 앱을 사용할게요!");
                            dialog.setOnPositiveClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    global.setUserName(name);
                                    dialog.dismiss();
                                }
                            });
                            dialog.show(getSupportFragmentManager(), MamaAlertDialog.TAG);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                if(!firstDraw) {
                    fragmentFactory.changeFragment(MamaFragmentFactory.FRAGMENT_HOME);
                    binding.mainUserStatusDot.setImageDrawable(getDrawable(R.drawable.dot_neg));
                } else {
                    fragmentInit();
                    splashAnimation();
                    firstDraw = false;
                }
            }
        }
    };
}
