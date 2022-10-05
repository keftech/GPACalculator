package kef.technology.jumianet;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;

import java.util.Objects;

import kef.technology.jumianet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public DrawerLayout drawerLayout;
    private WebView webView;
    protected SharedPreferences prefs;
    private LinearProgressIndicator progressIndicator;
    private FloatingActionButton fab_action, fab_back, fab_forward, fab_refresh;
    private float dY_action;
    private boolean isFABOpen = false;
    private View fab_background;
    private SwipeRefreshLayout swipeRefreshLayout;
    protected WebViewModel webViewModel;
    private LinearLayout errorPage;
    private TextView errorMessageBox;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    protected static final String TAG = "Log message: ", OPENED_NUM_KEY = "Opened_Num_Key", ORDER_NUM_KEY = "Order_Num_Key";

    protected static final String kolID="c1d714e9-5800-4823-b3e5-a07395c33fa6";
    protected static final String NIGERIA = "Nigeria", KENYA="Kenya", MOROCCO="Morocco", EGYPT="Egypt", GHANA="Ghana", TUNISIA="Tunisia", SENEGAL="Senegal", UGANDA="Uganda", SOUTH_AFRICA="South Africa", ZANDO="Zando";
    protected static String placeID="";
    static String homeUrl = "";
    protected static String tags="";
    String official="";
    String anniversary="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(HomeActivity.PREFS_KEY, Context.MODE_PRIVATE);

        setSupportActionBar(binding.appBarMain.toolbar);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        EditText urlBar = binding.appBarMain.urlBar;
        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        progressIndicator = binding.appBarMain.progress;
        swipeRefreshLayout = binding.appBarMain.swipeRefresh;
        fab_action = binding.appBarMain.fabAction;
        fab_back = binding.appBarMain.fabBack;
        fab_forward = binding.appBarMain.fabForward;
        fab_refresh = binding.appBarMain.fabRefresh;
        errorPage = binding.appBarMain.errorPageLay;
        Button retryButton = binding.appBarMain.retryButton;
        errorMessageBox = binding.appBarMain.errorMessage;
        fab_background = binding.appBarMain.fabBackground;
        fab_refresh.setOnClickListener(onFabClickListener);
        fab_forward.setOnClickListener(onFabClickListener);
        fab_back.setOnClickListener(onFabClickListener);
        fab_action.setOnClickListener(onFabClickListener);
        retryButton.setOnClickListener(onFabClickListener);
        fab_action.setOnLongClickListener(onFabLongClickListener);
        binding.appBarMain.getRoot().setOnDragListener(onDragListener);
        navigationView.setNavigationItemSelectedListener(navItemSelectedListener);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        webView = binding.appBarMain.homeWebview;
        init(); onNewIntent(getIntent());
        urlBar.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_GO){
                loadUrl(v.getText().toString());
                v.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getData() != null){
            String incomingData = intent.getDataString();
            if(incomingData.equals(getString(R.string.homepage)))
                incomingData = getKolLink(homeUrl);
            else if(incomingData.equals(getString(R.string.official)))
                incomingData = getKolLink(homeUrl+official);
            else if(incomingData.equals(getString(R.string.black_friday)))
                incomingData = getKolLink(homeUrl+anniversary);
            else if(incomingData.equals(getString(R.string.menu_voucher))){
                incomingData = getKolLink(homeUrl);
                showVouchers();
            }
            else if(incomingData.equals(getString(R.string.account)))
                incomingData = homeUrl+"customer/account/index/";
            else if(incomingData.equals(getString(R.string.orders)))
                incomingData = homeUrl+"customer/order/index/";
            loadUrl(incomingData);
        }
        else
            loadUrl(getKolLink(homeUrl));
    }

    private void loadUrl(String url){
        url = getHttpsLink(url);
        if(isValidUrl(url))
            webView.loadUrl(url);
        else{
            showMessage("Invalid Product's URL, try again");
            if(!isValidUrl(webView.getUrl())){
                webView.loadUrl(homeUrl);
                Toast.makeText(this,"Redirecting to homepage...",Toast.LENGTH_LONG).show();
            }
        }
    }

    protected static boolean isValidUrl(String url){
        return URLUtil.isValidUrl(url) && (url.contains(homeUrl) || url.contains(kolID));
    }

    protected static String getKolLink(String url){
        if(url.contains("?")){
            url = url.substring(0, url.indexOf("?"));
        }
        url = getHttpsLink(url);
        String _kolLink = "https://kol.jumia.com/api/click/custom/" + kolID + "/";
        return _kolLink + placeID + "?r=" + url.replaceAll(":", "%3A").replaceAll("/", "%2F") + tags;
    }

    protected static String getHttpsLink(String url){
        if(url.startsWith("http:"))
            return url.replaceFirst("http:", "https:");
        return url;
    }

    private void init(){
        webView.setWebViewClient(new MyWebClient(this));
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressIndicator.setProgress(newProgress);
                swipeRefreshLayout.setRefreshing(newProgress != 100);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.setOnTouchListener((view, event) -> {
                if(isFABOpen)
                    closeFABMenu();
                return false;
        });
        if(Build.VERSION.SDK_INT >= 19)
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        else
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        fab_action.post(() -> {
            float dX_action = fab_action.getX()+((fab_action.getWidth()/2)-(fab_back.getWidth()/2));
            dY_action = fab_action.getY()+((fab_action.getHeight()/2)-(fab_back.getHeight()/2));
            fab_back.setX(dX_action); fab_back.setY(dY_action);
            fab_forward.setX(dX_action); fab_forward.setY(dY_action);
            fab_refresh.setX(dX_action); fab_refresh.setY(dY_action);
        });
        fab_background.setAlpha(0.0f);

        swipeRefreshLayout.setColorSchemeResources(R.color.accent_dark, R.color.primary_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        final Observer<Boolean> showErrorObserver = showError -> {
            if(showError){
                errorPage.setVisibility(View.VISIBLE);
                errorPage.setAlpha(0.85f);
                errorPage.animate().alpha(1.0f);
            }
            else
                errorPage.setVisibility(View.GONE);
        };
        final Observer<String> errorMessageObserver = errorMessage -> {
            String errorMsg = getString(R.string.no_connect_message) +" or "+errorMessage;
            errorMessageBox.setText(errorMsg);
        };
        webViewModel.getErrorMesaage().observe(this, errorMessageObserver);
        webViewModel.getShowError().observe(this, showErrorObserver);
        String place = prefs.getString(SplashActivity.PLACE_KEY,"");
        tags = "&s1=App&s2=July+08&s3="+place;
        switch(place){
            case NIGERIA:
                placeID = "306ad549-764c-349e-a497-cdd2d98c349a";
                homeUrl = "https://www.jumia.com.ng/";
                official="mlp-official-stores/";
                anniversary="mlp-anniversary/";
                break;
            case KENYA:
                placeID = "0e1c47ed-cc97-3a21-846e-3217fd1ea92a";
                homeUrl = "https://www.jumia.co.ke/";
                official="mlp-jumia-official-stores/";
                anniversary="mlp-anniversary/";
                break;
            case EGYPT:
                placeID = "d0c262cf-4904-37ef-843b-21b0446ba764";
                homeUrl = "https://www.jumia.com.eg/";
                anniversary="mlp-anniversary/";
                break;
            case MOROCCO:
                placeID = "a64be1f8-8e6c-3b5e-b681-e9802b3e1270";
                homeUrl = "https://www.jumia.ma/";
                anniversary="mlp-anniversaire/";
                official="mlp-toutes-les-boutiques-officielles/";
                break;
            case GHANA:
                placeID = "b183f971-68ba-3832-8736-1e2c617cfb22";
                homeUrl = "https://www.jumia.com.gh/";
                official="mlp-official-stores/";
                anniversary="mlp-anniversary/";
                break;
            case TUNISIA:
                placeID = "3ad80905-dee4-3632-aec0-f49c313e1e96";
                homeUrl = "https://www.jumia.com.tn/";
                anniversary="mlp-anniversaire/";
                official="mlp-boutiques-officielles/";
                break;
            case SENEGAL:
                placeID = "db927cf1-7729-364c-b7a3-167ddcf54fed";
                homeUrl = "https://www.jumia.sn/";
                anniversary="mlp-anniversaire/";
                official="mlp-boutiques-officielles/";
                break;
            case UGANDA:
                placeID = "672c75a8-fe17-3f19-80c8-597a829cf84a";
                homeUrl = "https://www.jumia.ug/";
                anniversary="mlp-jumia-anniversary-2022/";
                break;
            case SOUTH_AFRICA:
                placeID = "234050c3-ff79-3e8f-8c24-ccce5d776b41";
                homeUrl = "https://www.jumia.co.za/";
                break;
            case ZANDO:
                placeID = "06d877f7-1aba-3bb8-8dea-bb174fc3c729";
                homeUrl = "https://www.zando.co.za/";
        }
    }

    private final View.OnClickListener onFabClickListener = view -> {
        int itemID = view.getId();
        if(itemID == R.id.fab_back){
            if(webView.canGoBack())
                webView.goBack();
        }
        else if(itemID == R.id.fab_forward){
            if(webView.canGoForward())
                webView.goForward();
        }
        else if(itemID == R.id.fab_refresh || itemID == R.id.retry_button){
            webView.reload();
            swipeRefreshLayout.setRefreshing(true);
        }
        else if(itemID == R.id.fab_action) {
            if(isFABOpen)
                closeFABMenu();
            else
                showFABMenu();
        }
    };

    private void showFABMenu(){
        isFABOpen=true;
        fab_background.animate().alpha(0.4f);
        fab_back.animate().y(dY_action - getResources().getDimension(R.dimen.standard_155));
        fab_forward.animate().y(dY_action - getResources().getDimension(R.dimen.standard_105));
        fab_refresh.animate().y(dY_action - getResources().getDimension(R.dimen.standard_55));
        fab_action.setImageResource(R.drawable.ic_baseline_close_24);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab_back.animate().y(dY_action);
        fab_forward.animate().y(dY_action);
        fab_refresh.animate().y(dY_action);
        fab_background.animate().alpha(0.0f);
        fab_action.bringToFront();
        fab_action.setImageResource(R.drawable.ic_baseline_control_action_24);
    }

    private final View.OnLongClickListener onFabLongClickListener = view -> {
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab_action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            view.startDragAndDrop(null, myShadow, null, View.DRAG_FLAG_GLOBAL);
        else
            view.startDrag(null, myShadow, null, 0);
        return true;
    };

    private final View.OnDragListener onDragListener = (view, event) -> {
        if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            if(isFABOpen)
                closeFABMenu();
            float dX = event.getX(), dY = event.getY();
            dY_action = dY-fab_back.getHeight()/2;
            fab_action.setX(dX-fab_action.getWidth()/2); fab_action.setY(dY-fab_action.getHeight()/2);
            fab_back.setX(dX-fab_back.getWidth()/2); fab_back.setY(dY_action);
            fab_forward.setX(dX-fab_forward.getWidth()/2); fab_forward.setY(dY_action);
            fab_refresh.setX(dX-fab_refresh.getWidth()/2); fab_refresh.setY(dY_action);
        }
        return true;
    };

    private final NavigationView.OnNavigationItemSelectedListener navItemSelectedListener = item -> {
        closeDrawer();
        int itemID = item.getItemId();
        if(itemID == R.id.nav_home)
            finish();
        else if(itemID == R.id.nav_voucher)
            showVouchers();
        else if(itemID == R.id.nav_friday)
            loadUrl(homeUrl+anniversary);
        else if(itemID == R.id.nav_share)
            share(getString(R.string.download_our_app)+"\n"+"http://onelink.to/wjnb7k");
        else if(itemID == R.id.nav_more_apps)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6543429764882580007")));
        else if(itemID == R.id.nav_version)
            showReview(3);
        return true;
    };

    private void showVouchers(){
        new BottomSheet(this,"https://www.kefblog.com.ng/2022/05/jumia-discount-voucher-codes.html").show();
    }

    private void showMessage(String msg){
        Snackbar.make(binding.appBarMain.getRoot(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void share(String textToShare){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(intent,getString(R.string.share_using)));
    }

    private void closeDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        else if(item.getItemId() == R.id.share_btn){
            share(getString(R.string.check_message)+"\n"+webView.getUrl().replaceFirst("https:","http:"));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "onActivityResult: app download failed");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack(); return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        webView.clearHistory();
        webView.clearCache(true);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int openedNum = prefs.getInt(OPENED_NUM_KEY, 0)+1;
        startUpdate(openedNum);
        prefs.edit().putInt(OPENED_NUM_KEY, openedNum).apply();
    }

    private void startUpdate(int openedNum){
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (openedNum%10 == 0 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE )){
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, e.toString());
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    protected void showReview(int orderNum){
        if(!canShowReview(orderNum))
            return;
        ReviewManager reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                if (reviewInfo != null) {
                    Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                    flow.addOnCompleteListener(task1 -> Log.d(TAG, "In App Rating complete"));
                }
                else {
                    Log.e(TAG, "In App Rating failed");
                }
            } else {
                Log.e(TAG, "In App ReviewFlow failed to start");
            }
        });
    }
    private boolean canShowReview(int orderNum){
        return orderNum < 81 && (orderNum == 3 || orderNum%20 == 0);
    }

    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (mAppUpdateManager != null)
                    mAppUpdateManager.unregisterListener(installStateUpdatedListener);
            } else {
                Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        }

    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), getString(R.string.download_success_msg), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.install), view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        }).show();
    }

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
        super.onStop();
    }
}