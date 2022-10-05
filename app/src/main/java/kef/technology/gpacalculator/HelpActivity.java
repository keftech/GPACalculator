package kef.technology.gpacalculator;

import android.os.Bundle;
import android.widget.TextView;
import android.os.Build;
import android.text.Html;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity
{
    private TextView gpHowBox;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gpHowBox = findViewById(R.id.gpHowBox);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gpHowBox.setText(Html.fromHtml(getString(R.string.gpHowText), Html.FROM_HTML_MODE_LEGACY));
        } else {
            gpHowBox.setText(Html.fromHtml(getString(R.string.gpHowText)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
