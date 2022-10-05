package kef.technology.jumianet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import kef.technology.jumianet.databinding.ActivityNavigateBinding;

public class NavigateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNavigateBinding binding = ActivityNavigateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<ListItem> gridItems = new ArrayList<>();
        gridItems.add(new ListItem(getString(R.string.homepage), R.drawable.homepage));
        gridItems.add(new ListItem(getString(R.string.official), R.drawable.official));
        gridItems.add(new ListItem(getString(R.string.black_friday), R.drawable.anniversary));
        gridItems.add(new ListItem(getString(R.string.menu_voucher), R.drawable.voucher));
        gridItems.add(new ListItem(getString(R.string.account), R.drawable.account));
        gridItems.add(new ListItem(getString(R.string.orders), R.drawable.order));
        GridItemAdapter adapter = new GridItemAdapter(this, R.layout.grid_item, gridItems);
        GridView gridView = binding.gridList;
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> openPage(Uri.parse(adapter.getItem(position).getTitle())));
        openPage(getIntent().getData());
    }

    private void openPage(Uri uri){
        if(uri != null){
            Intent pageIntent = new Intent(getBaseContext(), MainActivity.class);
            pageIntent.setData(uri);
            startActivity(pageIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.privacy_btn)
            new BottomSheet(this, "https://www.kefblog.com.ng/p/jumia-app-privacy-policy.html").show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigate, menu);
        return true;
    }
}
