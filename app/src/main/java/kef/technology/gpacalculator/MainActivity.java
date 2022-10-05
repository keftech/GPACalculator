package kef.technology.gpacalculator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainLinearLayout,subLinearLayout;
    private ScrollView mainScrollView;
    private TableLayout txtBoxsContainer;
    private Button goBtn,calcBtn,clearBtn;
    private EditText courseNumBox,firstScoreBox,lastCourseBox,lastScoreBox,lastUnitBox;
    private TextView cgpTxtBox;
    private ProgressDialog pDialog;
    private boolean layoutAltered=false,firstShow=true;
    private int courseNum=0;
    private LinkedList<Integer> scoreBoxIds,unitBoxIds;
    private ForegroundColorSpan red;
    private SpannableStringBuilder ssb;
    private Space firstSpace;
    private boolean adLoaded=false;
    private AdLoader adLoader;
    private TemplateView adView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        MobileAds.initialize(this);
        initAds();

        adView2 = findViewById(R.id.bottomNativeTemplateView);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        mainScrollView = findViewById(R.id.mainScrollView);
        courseNumBox = findViewById(R.id.courseNumBox);
        firstSpace = findViewById(R.id.firstSpace);
        subLinearLayout = findViewById(R.id.subLinearLayout);
        txtBoxsContainer = findViewById(R.id.txtBoxContainer);
        goBtn = findViewById(R.id.goBtn);
        goBtn.setOnClickListener(performAction);
        calcBtn = findViewById(R.id.calcBtn);
        calcBtn.setOnClickListener(performAction);
        clearBtn = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(performAction);
        clearBtn.setTypeface(FontManager.getTypeface(this,FontManager.FONTAWESOME));
        cgpTxtBox = findViewById(R.id.cgpTxtBox);
        red = new ForegroundColorSpan(getResources().getColor(R.color.Accent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ssb = new SpannableStringBuilder("Made with "+Html.fromHtml("&#xf004;", Html.FROM_HTML_MODE_LEGACY)+" by Jamiu Akinyemi");
            clearBtn.setText(Html.fromHtml("&#xf0e2", Html.FROM_HTML_MODE_LEGACY));
        } else {
            ssb = new SpannableStringBuilder("Made with "+Html.fromHtml("&#xf004;")+" by Jamiu Akinyemi");
            clearBtn.setText(Html.fromHtml("&#xf0e2"));
        }
        ssb.setSpan(red,10,11,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pDialog = new ProgressDialog(this);
        scoreBoxIds = new LinkedList<>();
        unitBoxIds = new LinkedList<>();
    }
    private final View.OnClickListener performAction = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(!adLoaded && adLoader != null)
                adLoader.loadAd(new AdRequest.Builder().build());
            switch(v.getId()){
                case R.id.goBtn:
                    int sleep,delay=400;
                    boolean showPD = true;
                    if(courseNumBox.getText().toString().equals("")){
                        Toast.makeText(getBaseContext(),"Enter a number",Toast.LENGTH_LONG).show();
                        return;
                    }
                    int preCourseNum = courseNum;
                    int courseNum = Integer.parseInt(courseNumBox.getText().toString());
                    if(courseNum == 0){
                        Toast.makeText(getBaseContext(),"0 is not a valid input",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(courseNum > 200)
                        sleep = 3000;
                    else{
                        sleep = 100;
                        if(courseNum < 30 && preCourseNum < 200){
                            showPD = false;
                            if(courseNum > 7 && preCourseNum > 7){
                                calcBtn.setVisibility(View.GONE);
                                txtBoxsContainer.setVisibility(View.GONE);
                            }
                        }
                    }
                    if(!layoutAltered)
                        delay = 1000;
                    showTextBoxs(delay,sleep,showPD);
                    if(firstShow)
                        clearBtn.setVisibility(View.VISIBLE);
                    break;

                case R.id.calcBtn:
                    cgpTxtBox.setVisibility(View.GONE);
                    EditText scoreBox,unitBox;
                    long totalScoreUnit=0, totalUnit=0;
                    int score=0,unit;

                    for(int scoreBoxId: scoreBoxIds){
                        scoreBox = findViewById(scoreBoxId);
                        unitBox = findViewById(unitBoxIds.get(scoreBoxIds.indexOf(scoreBoxId)));
                        if(scoreBox.getText().toString().equals("")){score=0;}
                        else{score=Integer.parseInt(scoreBox.getText().toString());}
                        if(score > 100){break;}
                        else if(score >= 70){score = 5;}
                        else if(score >= 60){score = 4;}
                        else if(score >= 50){score = 3;}
                        else if(score >= 45){score = 2;}
                        else if(score >= 40){score = 1;}
                        else {score=0;}
                        if(unitBox.getText().toString().equals("")){unit=0;}
                        else{unit=Integer.parseInt(unitBox.getText().toString());}
                        totalUnit += unit;
                        totalScoreUnit += score*unit;
                    }
                    if(score > 100){
                        Toast.makeText(getBaseContext(),"A score cannot be greater than 100",Toast.LENGTH_LONG).show();}
                    else{
                        double cgpa; String remark="";
                        if (totalUnit == 0){cgpa=0;}
                        else{cgpa = (float)totalScoreUnit/totalUnit;}
                        if(cgpa >= 4.5){remark = " (First Class)";}
                        else if(cgpa >= 3.5){remark = " (Second Class Upper)";}
                        else if(cgpa >= 2.5){remark = " (Second Class Lower)";}
                        else if(cgpa >= 1.0){remark = " (Third Class)";}
                        ssb = new SpannableStringBuilder("Your CGPA is: "+String.format("%.2f",cgpa)+remark);
                        ssb.setSpan(red,14,18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TransitionManager.beginDelayedTransition(mainLinearLayout);
                        cgpTxtBox.setVisibility(View.VISIBLE);
                        cgpTxtBox.setText(ssb);
                        mainScrollView.post(()-> mainScrollView.fullScroll(View.FOCUS_DOWN));
                    }
                    break;
                case R.id.clearBtn:
                    mainScrollView.smoothScrollTo(0,0);
                    txtBoxsContainer.setVisibility(View.GONE);
                    for(int scoreBoxId: scoreBoxIds){
                        scoreBox = findViewById(scoreBoxId);
                        unitBox = findViewById(unitBoxIds.get(scoreBoxIds.indexOf(scoreBoxId)));
                        scoreBox.setText("");unitBox.setText("");
                    }
                    TransitionManager.beginDelayedTransition(mainLinearLayout);
                    cgpTxtBox.setVisibility(View.GONE);
                    txtBoxsContainer.setVisibility(View.VISIBLE);
                    firstScoreBox.requestFocus();
            }

        }
    };

    private void showTextBoxs(int delay,int sleep,boolean showPD){
        if(showPD){
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        doActionBackground doAB = new doActionBackground();
        doAB.execute(delay,sleep);
    }

    private class doActionBackground extends AsyncTask<Integer,String,String>
    {
        @Override
        protected String doInBackground(Integer[] p1)
        {
            final int delay = p1[0];
            final int sleep = p1[1];
            Thread t = new Thread(() -> {
                AutoTransition autoTransition = new AutoTransition();
                autoTransition.setDuration(delay);
                TransitionManager.beginDelayedTransition(mainLinearLayout,autoTransition);

                mainLinearLayout.post(() -> {
                    int courseNumB4,index=1;
                    if(delay == 1000)
                        alterLayout();
                    txtBoxsContainer.setVisibility(View.VISIBLE);
                    calcBtn.setVisibility(View.VISIBLE);
                    courseNumB4 = courseNum;
                    courseNum = Integer.parseInt(courseNumBox.getText().toString());

                    if(courseNumB4 > courseNum){
                        txtBoxsContainer.removeViews(courseNum+1,courseNumB4-courseNum);
                        removeViewsIDs(scoreBoxIds); removeViewsIDs(unitBoxIds);
                        lastScoreBox = findViewById(scoreBoxIds.get(courseNum-1));
                        lastUnitBox = findViewById(unitBoxIds.get(courseNum-1));
                    }
                    else if(courseNumB4 < courseNum){
                        if(!firstShow){lastCourseBox.setImeOptions(EditorInfo.IME_ACTION_GO); lastScoreBox.setImeOptions(EditorInfo.IME_ACTION_GO); lastUnitBox.setImeOptions(EditorInfo.IME_ACTION_GO);}

                        boolean firstRun = true;
                        if(courseNumB4 != 0)
                            index = courseNumB4+1;
                        for(;index<=courseNum;index++){
                            EditText newEdtTxt1,newEdtTxt2,newEdtTxt3;
                            if (Build.VERSION.SDK_INT >= 21){
                                newEdtTxt1 = new EditText(getApplicationContext(),null,0,R.style.EditText_bold_color);
                                newEdtTxt2 = new EditText(getApplicationContext(),null,0,R.style.EditText_2nd);
                                newEdtTxt3 = new EditText(getApplicationContext(),null,0,R.style.EditText);
                            }
                            else{
                                newEdtTxt1 = new EditText(new ContextThemeWrapper(getApplicationContext(),R.style.EditText_bold_color));
                                newEdtTxt2 = new EditText(new ContextThemeWrapper(getApplicationContext(),R.style.EditText_2nd));
                                newEdtTxt3 = new EditText(new ContextThemeWrapper(getApplicationContext(),R.style.EditText));
                            }
                            newEdtTxt1.setText("#"+index);
                            applyEdtTxtStyle(newEdtTxt1);
                            applyEdtTxtStyle(newEdtTxt2);
                            applyEdtTxtStyle(newEdtTxt3);

                            if(firstShow && firstRun){ firstScoreBox = newEdtTxt2; firstRun = false;}
                            if(index == courseNum){ lastCourseBox = newEdtTxt1; lastUnitBox = newEdtTxt2; lastScoreBox = newEdtTxt3;}
                            unitBoxIds.add(newEdtTxt2.getId());
                            scoreBoxIds.add(newEdtTxt3.getId());

                            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
                            TableRow newRow = new TableRow(getApplicationContext());
                            newRow.setLayoutParams(rowParams);
                            txtBoxsContainer.addView(newRow,index);
                            newRow.addView(newEdtTxt1);
                            newRow.addView(newEdtTxt2);
                            newRow.addView(newEdtTxt3);
                        }
                    }
                    firstScoreBox.requestFocus();
                    lastCourseBox.setImeOptions(EditorInfo.IME_ACTION_DONE); lastScoreBox.setImeOptions(EditorInfo.IME_ACTION_DONE); lastUnitBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    firstShow = false;
                });
            });
            t.start(); try{ t.sleep(sleep); t.join(); } catch(InterruptedException e){e.printStackTrace();}
            pDialog.dismiss();
            return null;
        }
    }

    private void alterLayout(){
        mainLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        subLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        subLinearLayout.setWeightSum(1f);

        LinearLayout.LayoutParams mainTxtVwParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        mainTxtVwParams.weight = 0.7f;
        courseNumBox.setLayoutParams(mainTxtVwParams);
        LinearLayout.LayoutParams mainBtnParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        mainBtnParams.weight = 0.3f;
        goBtn.setLayoutParams(mainBtnParams);
        firstSpace.setVisibility(View.GONE);
        adView2.setVisibility(View.VISIBLE);
        layoutAltered = true;
    }

    private void removeViewsIDs(LinkedList linkedList){
        Iterator listIT = linkedList.iterator();
        while(listIT.hasNext()){
            if(linkedList.indexOf(listIT.next()) == courseNum){
                listIT.remove();
            }
        }
    }

    private void applyEdtTxtStyle(EditText newEdtTxt){
        newEdtTxt.setId(newEdtTxt.generateViewId());
        newEdtTxt.setFocusableInTouchMode(true);
    }

    private void initAds(){
        adLoader = new AdLoader.Builder(this, "ca-app-pub-8327472816877927/7918453976")//"ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {
                    if (isDestroyed()) {
                        nativeAd.destroy();
                        return;
                    }
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(Color.WHITE)).build();
                    TemplateView adView1 = findViewById(R.id.topNativeTemplateView);
                    adView1.setStyles(styles);
                    adView1.setNativeAd(nativeAd);

                    adView2.setStyles(styles);
                    adView2.setNativeAd(nativeAd);

                    adLoaded = true;
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.help){
            startActivity(new Intent(this,HelpActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}