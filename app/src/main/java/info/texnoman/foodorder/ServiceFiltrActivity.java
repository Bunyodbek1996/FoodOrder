package info.texnoman.foodorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.texnoman.foodorder.adapters.ServiceAdapter;
import info.texnoman.foodorder.models.Service;
import io.paperdb.Paper;

public class ServiceFiltrActivity extends AppCompatActivity {
    RecyclerView recycler_service;
    List<Service> serviceList;
    int PLACE_PICKER_REQUEST = 1001;
    TextView address_textview;
    public static int service_id;
    SwipeRefreshLayout swipe_refresh;
    double distance = 999999999;
    public static String min_distance;
    public static String min_distance_price;
    public static String one_km_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_filtr);

        setStatusBarColor();
        initVars();
        initViews();
        displayLocationSettingsRequest(this);

        if (SplashActivity.ADDRESS.length()>28){
            address_textview.setText(SplashActivity.ADDRESS.substring(0,27)+"...");
        }else{
            address_textview.setText(SplashActivity.ADDRESS);
        }
    }

    private void initVars() {
        serviceList = new ArrayList<>();
    }

    private void initViews() {
        swipe_refresh = findViewById(R.id.swipe_refresh);
        address_textview = findViewById(R.id.address_textview);
        recycler_service = findViewById(R.id.recycler_service);


        initService(recycler_service);
        initSwipeRefresh(swipe_refresh);
        getPrices();

    }

    private void initSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getServices();
                    }
                }
        );
    }

    private void initService(RecyclerView recycler_service) {
        recycler_service.setHasFixedSize(true);
        recycler_service.setLayoutManager(new LinearLayoutManager(this));
        getServices();
    }

    private void setStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 23){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.whiteText));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void getServices() {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_SERVICES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipe_refresh.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                serviceList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject services = jsonArray.getJSONObject(i);
                                    int id = services.getInt("id");
                                    String name;
                                    if (SplashActivity.LANG != null) {
                                        if (SplashActivity.LANG.equals("uz")) {
                                            name = services.getString("name_uz");
                                        } else {
                                            name = services.getString("name_ru");
                                        }
                                    } else {
                                        name = services.getString("name_ru");
                                    }
                                    String image = services.getString("image");
                                    int status = services.getInt("status");
                                    Service service = new Service(id,name,image,status);
                                    if (status == 1){
                                        serviceList.add(service);
                                    }
                                }
                                ServiceAdapter adapter = new ServiceAdapter(ServiceFiltrActivity.this,serviceList);
                                recycler_service.setAdapter(adapter);
                            }else{
                                int error_code = jsonObject.getInt("error_code");
                                if (error_code == 1) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.not_enough_parametrs, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 2) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.phone_number_invalid, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 3) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.too_many_tries, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 4) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.unknow_query_error, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 5) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.phone_number_not_found, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 6) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.code_is_expired, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 7) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.error_code, Toast.LENGTH_SHORT).show();
                                } else if (error_code == 8) {
                                    startActivity(new Intent(ServiceFiltrActivity.this,SignUpActivity.class));
                                    finish();
                                } else if (error_code == 9) {
                                    startActivity(new Intent(ServiceFiltrActivity.this,SignUpActivity.class));
                                    finish();
                                } else if (error_code == 10) {
                                    Toast.makeText(ServiceFiltrActivity.this, R.string.invalid_order_amount, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceFiltrActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipe_refresh.setRefreshing(false);
            }
        }) {
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

    public void getLocation(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                double latitude = data.getExtras().getDouble("lat");
                double longitude = data.getExtras().getDouble("lon");

                Geocoder geocoder = new Geocoder(ServiceFiltrActivity.this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(
                            latitude, longitude, 1);

                    String address = list.get(0).getAddressLine(0);
                    if (address.length()>40){
                        address_textview.setText(address.substring(0,37)+"...");
                    }else{
                        address_textview.setText(address);
                    }
                } catch (IOException e) {
                    String lat = (latitude+"").substring(0,9);
                    String lon = (longitude+"").substring(0,9);
                    address_textview.setText(lat+" "+lon);
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 111){
            if (resultCode == RESULT_OK){
            }else{
                this.finish();
            }
        }
    }

    public void passAccount(View view) {
        startActivity(new Intent(ServiceFiltrActivity.this, AccountActivity.class));
    }

    private void getPrices() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_PRICES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                min_distance = data.getString("min_distance");
                                min_distance_price = data.getString("min_price");
                                one_km_price = data.getString("one_km_price");
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ServiceFiltrActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ServiceFiltrActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }) {
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
                            status.startResolutionForResult(ServiceFiltrActivity.this, 111);
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

}