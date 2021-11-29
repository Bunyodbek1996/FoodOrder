package info.texnoman.foodorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.texnoman.foodorder.models.Offer;
import info.texnoman.foodorder.other.GPSTracker;
import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://admin.ketaylik.uz";
    public static final String SET_CUSTOMER_ADDRESS_URL = BASE_URL + "/api/customer/auth/set_customer_address";
    public static final String SEND_FCM = BASE_URL + "/api/customer/auth/set_customer_fcm";
    public static final String GET_SERVICES_URL = BASE_URL + "/api/customer/services/get";
    public static final String GET_PRICES_URL = BASE_URL + "/api/customer/auth/get_prices";
    public static final String OFFER_URL = BASE_URL + "/api/customer/offers/get";
    public static final String CATEGORY_URL = BASE_URL + "/api/customer/categories/";
    public static final String CITY_URL = BASE_URL + "/api/customer/cities/get";
    public static final String RESTAURANTS_URL = BASE_URL + "/api/customer/restaurants/";
    public static final String ADD_TO_CART_URL = BASE_URL + "/api/customer/product/";
    public static final String RESTAURANTS_DETAILS_URL = BASE_URL + "/api/customer/restaurant/";
    public static final String FAVORITE_URL = BASE_URL + "/api/customer/restaurant/";
    public static final String GET_FAVORITE_URL = BASE_URL + "/api/customer/restaurants/";
    public static final String GET_CART_LIST_URL = BASE_URL + "/api/customer/product/get_cart_list";
    public static final String DECREMENT_URL = BASE_URL + "/api/customer/product/";
    public static final String REMOVE_URL = BASE_URL + "/api/customer/product/";
    public static final String CLEAR_CART_URL = BASE_URL + "/api/customer/product/clear_cart";
    public static final String CREATE_ORDER = BASE_URL + "/api/customer/order/create";
    public static final String ORDER_URL = BASE_URL + "/api/customer/orders/get";
    public static final String GET_SINGLE_ORDER_URL = BASE_URL + "/api/customer/orders/";
    public static final String DELETE_ORDER_URL = BASE_URL + "/api/customer/orders/";
    public static final String GET_CURRENT_TIME_URL = BASE_URL + "/api/customer/restaurants/get_current_time";
    public static final String SEARCH_RESTAURANT_URL = BASE_URL + "/api/customer/restaurants/";

    public static String LATITUDE = "40.385788";
    public static String LONGITUDE = "71.785002";
    public static String ADDRESS = "";
    public static String TOKEN = "";
    public static String LANG = "";
    public static String CURRENT_TIME = "12:00";
    private ImageView splashLogo;
    int PLACE_PICKER_REQUEST = 1001;
    public static String city_id = "1";
    double distance = 999999999;
    ProgressDialog loading;
    Context context;
    LocationManager locationManager;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Paper.init(this);
        context = this;
        loading = new ProgressDialog(this);
        loading.setMessage("Yuborilmoqda");
        loading.setCanceledOnTouchOutside(false);
        String address = Paper.book().read("address");
        String lat = Paper.book().read("lat");
        String lon = Paper.book().read("lon");
        TOKEN = Paper.book().read("token");
        LANG = Paper.book().read("lang");
        city_id = Paper.book().read("city_id");
        if (lat != null && lon != null) {
            ADDRESS = address;
            LATITUDE = lat;
            LONGITUDE = lon;
        }
        if (LANG == null) {
            Paper.book().write("lang", "uz");
            LANG = "uz";
        }
        setLocale(LANG);
        if (TOKEN == null || TOKEN.equals("")) {
            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            SplashActivity.this.finish();
        } else {
            locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,}, 10);
                    }
                } else {
                    GPSTracker gpsTracker = new GPSTracker(context); //GPSTracker is class that is used for retrieve user current location
                }
                return;
            }
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            initViews();
            if (ADDRESS.equals("") || ADDRESS == null) {
                initLocation(currentLocation);
            } else {
                getCurrentTime();
            }
        }

    }



    private void initLocation(Location location) {
        double lat = 40.385788;
        double lon = 71.785002;

        if (SplashActivity.this.currentLocation != null){
            lat = SplashActivity.this.currentLocation.getLatitude();
            lon = SplashActivity.this.currentLocation.getLongitude();
        }
        Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(
                    lat, lon, 1);
            double finalLat = lat;
            double finalLon = lon;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (list.size() > 0) {
                            ADDRESS = list.get(0).getAddressLine(0);
                            getCities(ADDRESS, finalLat, finalLon);
                        } else {
                            ADDRESS = getString(R.string.get_address_from_coordinates);
                            getCities(ADDRESS, finalLat, finalLon);
                        }
                        LATITUDE = "" + finalLat;
                        LONGITUDE = "" + finalLon;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        } catch (IOException e) {
            getLocation();
        }
    }

    private void initViews() {
        splashLogo = findViewById(R.id.splashLogo);
        Glide.with(this).asGif().load(R.raw.logo_gif).into(splashLogo);
    }

    private void getCurrentTime() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, GET_CURRENT_TIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CURRENT_TIME = response.substring(1, 6);
                        startActivity(new Intent(SplashActivity.this, ServiceFiltrActivity.class));
                        SplashActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
                        SplashActivity.this.finish();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", TOKEN);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public void getLocation() {
//        Intent intent = new com.app.akplacepicker.utilities.PlacePicker.IntentBuilder()
//                .setGoogleMapApiKey(getString(R.string.google_map_api))
//                .setLatLong(Double.parseDouble(SplashActivity.LATITUDE), Double.parseDouble(SplashActivity.LONGITUDE))
//                .setMapZoom(18.0f)
//                .setAddressRequired(false)
//                .setPrimaryTextColor(R.color.blackText)
//                .setMarkerDrawable(R.drawable.place_picker)
//                .build(this);
//        startActivityForResult(intent, PLACE_PICKER_REQUEST);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                double latitude = data.getExtras().getDouble("lat");
                double longitude = data.getExtras().getDouble("lon");

                Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(
                            latitude, longitude, 1);

                    SplashActivity.ADDRESS = list.get(0).getAddressLine(0);
                    SplashActivity.LATITUDE = "" + latitude;
                    SplashActivity.LONGITUDE = "" + longitude;

                    getCities(ADDRESS, Double.parseDouble(LATITUDE), Double.parseDouble(LONGITUDE));
                } catch (IOException e) {
                    getCities(LATITUDE+" "+LONGITUDE, Double.parseDouble(LATITUDE), Double.parseDouble(LONGITUDE));
                    e.printStackTrace();
                }


            }
        }
    }

    private void getCities(final String address, double lat1, double lon1) {
        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.CITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject cityJson = jsonArray.getJSONObject(i);
                                    String lat = cityJson.getString("latitude");
                                    String lon = cityJson.getString("longitude");
                                    double distance2 = distance(Double.parseDouble(lat1 + ""), Double.parseDouble(lon1 + ""), Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (distance > distance2) {
                                        distance = distance2;
                                        city_id = cityJson.getInt("id")+"";
                                    }
                                }
                                if (distance < 50000) {
                                    sendAddress(address, city_id + "", lat1, lon1);
                                } else {
                                    Paper.init(SplashActivity.this);
                                    Paper.book().write("address", "");
                                    Paper.book().write("lat", "");
                                    Paper.book().write("lon", "");
                                    Toast.makeText(SplashActivity.this, R.string.not_serviced, Toast.LENGTH_SHORT).show();
                                    getLocation();
                                }

                            } else {
                                getCities(address, lat1, lon1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getCities(address, lat1, lon1);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
                SplashActivity.this.finish();
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void sendAddress(final String address, final String city_id, double lat, double lon) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.SET_CUSTOMER_ADDRESS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                initViews();
                                ADDRESS = address;
                                Paper.init(SplashActivity.this);
                                Paper.book().write("address", address);
                                Paper.book().write("lat", lat + "");
                                Paper.book().write("lon", lon + "");
                                getCurrentTime();
                            } else {
                                sendAddress(address, city_id, lat, lon);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sendAddress(address, city_id, lat, lon);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
                SplashActivity.this.finish();
            }
        }) {
            @Override
            public byte[] getBody() {
                Map<String, String> addr = new HashMap<>();
                addr.put("address", address);
                addr.put("city_id", city_id);
                return new JSONObject(addr).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", TOKEN);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(SplashActivity.this,SplashActivity.class));
                this.finish();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Dastur faqat joylashuvni aniqlashga ruxsat berilganda to'g'ri ishlaydi")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                  startActivity(new Intent(SplashActivity.this,SplashActivity.class));
                            }
                        });
                alert.create();
                alert.show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}
