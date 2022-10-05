package kef.technology.jumianet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import kef.technology.jumianet.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    static final String PLACE_KEY = "PlaceKey";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences.Editor prefsEditor = getSharedPreferences(HomeActivity.PREFS_KEY, Context.MODE_PRIVATE).edit();
        List<ListItem> countryList = new ArrayList<>();
        countryList.add(new ListItem(MainActivity.EGYPT, R.drawable.ic_egypt));
        countryList.add(new ListItem(MainActivity.GHANA, R.drawable.ic_ghana));
        countryList.add(new ListItem(MainActivity.KENYA, R.drawable.ic_kenya));
        countryList.add(new ListItem(MainActivity.MOROCCO, R.drawable.ic_morocco));
        countryList.add(new ListItem(MainActivity.NIGERIA, R.drawable.ic_nigeria));
        countryList.add(new ListItem(MainActivity.SENEGAL, R.drawable.ic_senegal));
        countryList.add(new ListItem(MainActivity.SOUTH_AFRICA, R.drawable.ic_southafrica));
        countryList.add(new ListItem(MainActivity.TUNISIA, R.drawable.ic_tunisia));
        countryList.add(new ListItem(MainActivity.UGANDA, R.drawable.ic_uganda));
        countryList.add(new ListItem(MainActivity.ZANDO, R.drawable.ic_southafrica));
        CountryAdapter adapter = new CountryAdapter(this, R.layout.activity_splash, countryList);
        ListView listView = binding.countryList;
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedPos(position);
            adapter.notifyDataSetChanged();
        });
        binding.okButton.setOnClickListener(v -> {
            if(adapter.selectedPos > -1){
                ListItem country = adapter.getItem(adapter.selectedPos);
                prefsEditor.putString(PLACE_KEY, country.getTitle());
                prefsEditor.apply();
                Uri uri = getIntent().getData();
                Intent pageIntent = new Intent(getBaseContext(), NavigateActivity.class);
                if(uri != null)
                    pageIntent.setData(uri);
                startActivity(pageIntent);
                finish();
            }
        });
    }

}
