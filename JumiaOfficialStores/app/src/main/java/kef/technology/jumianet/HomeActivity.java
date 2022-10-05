package kef.technology.jumianet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    static final String PREFS_KEY = "PreferenceKey";
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        if(prefs.contains(SplashActivity.PLACE_KEY))
            showPage(new Intent(this, NavigateActivity.class));
        else
            showPage(new Intent(this, SplashActivity.class));
    }

    private void showPage(Intent pageIntent){
        Intent intent = getIntent();
        if(intent.getData() != null){
            pageIntent.setData(intent.getData());
        }
        startActivity(pageIntent);
        finish();
    }
}
