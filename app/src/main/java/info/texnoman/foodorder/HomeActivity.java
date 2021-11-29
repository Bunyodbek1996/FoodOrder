package info.texnoman.foodorder;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.texnoman.foodorder.fragments.FavoriteFragment;
import info.texnoman.foodorder.fragments.HomeFragment;
import info.texnoman.foodorder.fragments.OrderFragment;
import info.texnoman.foodorder.fragments.SearchFragment;


public class HomeActivity extends AppCompatActivity {
    public static TextView address_textview;
    public static ProgressBar progressBar;
    int PLACE_PICKER_REQUEST = 1001;
    public List<Address> list_address = new ArrayList<>();

    public static final String CHANNEL_ID = "ketaylik_chanel_id";
    private static final String CHANNEL_NAME = "Ketaylik chanel name";
    private static final String CHANNEL_DESC = "Ketaylik Notification";

    double distance = 999999;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    FragmentManager fm = getSupportFragmentManager();
    SearchFragment searchFragment = new SearchFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    OrderFragment orderFragment = new OrderFragment();
    HomeFragment homeFragment = new HomeFragment();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        initVars();
        initViews();
        initLocation();
        setStatusBarColor();
        initBottomNavMenu();
        displayLocationSettingsRequest(this);

        initFCM();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                    try {
                        List<Address> list = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        SplashActivity.ADDRESS = list.get(0).getAddressLine(0);
                        SplashActivity.LATITUDE = "" + location.getLatitude();
                        SplashActivity.LONGITUDE = "" + location.getLongitude();
                        if (SplashActivity.ADDRESS.length() > 28) {
                            address_textview.setText(SplashActivity.ADDRESS.substring(0, 27) + "...");
                        } else {
                            address_textview.setText(SplashActivity.ADDRESS);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initVars() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Geocoder geocoder = new Geocoder(HomeActivity.this, new Locale("ru"));
                    try {
                        List<Address> list = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        SplashActivity.ADDRESS = list.get(0).getAddressLine(0);
                        SplashActivity.LATITUDE = "" + location.getLatitude();
                        SplashActivity.LONGITUDE = "" + location.getLongitude();

                        if (SplashActivity.ADDRESS.length() > 28) {
                            address_textview.setText(SplashActivity.ADDRESS.substring(0, 27) + "...");
                        } else {
                            address_textview.setText(SplashActivity.ADDRESS);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void initFCM() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            sendTokenToServer(token);
                        } else {
                        }
                    }
                });
    }

    private void sendTokenToServer(String token) {
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.SEND_FCM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("fcm_token", token);
                return new JSONObject(map).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(request);
    }

    private void initBottomNavMenu() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        fm.beginTransaction().add(R.id.nav_host_fragment, active, "1").commit();

    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();
        address_textview = findViewById(R.id.address_textview);
        if (SplashActivity.ADDRESS.length() > 28) {
            address_textview.setText(SplashActivity.ADDRESS.substring(0, 27) + "...");
        } else {
            address_textview.setText(SplashActivity.ADDRESS);
        }
    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                double latitude = data.getExtras().getDouble("lat");
                double longitude = data.getExtras().getDouble("lon");

                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(
                            latitude, longitude, 1);

                    String address = list.get(0).getAddressLine(0);
                    if (address.length() > 40) {
                        address_textview.setText(address.substring(0, 37) + "...");
                    } else {
                        address_textview.setText(address);
                    }
                } catch (IOException e) {
                    String lat = (latitude + "").substring(0, 9);
                    String lon = (longitude + "").substring(0, 9);
                    address_textview.setText(lat + " " + lon);
                    e.printStackTrace();
                }
            }
        }
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(HomeActivity.this, 111);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private boolean search_stack = false, favourite_stack = false, order_stack = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (active != homeFragment) {
                        fm.beginTransaction().hide(active).show(homeFragment).commit();
                        active = homeFragment;
                        return true;
                    } else {
                        return false;
                    }
                case R.id.navigation_search:
                    if (search_stack) {
                        fm.beginTransaction().hide(active).show(searchFragment).commit();
                    } else {
                        fm.beginTransaction().hide(active).add(R.id.nav_host_fragment, searchFragment, "2").show(searchFragment).commit();
                        search_stack = true;
                    }
                    active = searchFragment;
                    return true;

                case R.id.navigation_favorite:
                    if (favourite_stack) {
                        fm.beginTransaction().hide(active).show(favoriteFragment).commit();
                    } else {
                        fm.beginTransaction().hide(active).add(R.id.nav_host_fragment, favoriteFragment, "3").show(favoriteFragment).commit();
                        favourite_stack = true;
                    }
                    active = favoriteFragment;
                    return true;
                case R.id.navigation_order:
                    if (order_stack) {
                        fm.beginTransaction().hide(active).show(orderFragment).commit();
                    } else {
                        fm.beginTransaction().add(R.id.nav_host_fragment, orderFragment, "4").show(orderFragment).commit();
                        order_stack = true;
                    }
                    active = orderFragment;
                    return true;
            }
            return false;
        }
    };

    public void passAccount(View view) {
        startActivity(new Intent(HomeActivity.this, AccountActivity.class));
    }
}