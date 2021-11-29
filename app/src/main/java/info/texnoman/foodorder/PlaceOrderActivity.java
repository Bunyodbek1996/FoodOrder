package info.texnoman.foodorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;


public class PlaceOrderActivity extends AppCompatActivity {
    String min_distance, min_distance_price, one_km_price;
    ProgressDialog dialog;
    int PLACE_PICKER_REQUEST = 1001;
    String restaurant_id;
    String order_quantity, products_price, name = "Noname";
    Button send_button;
    private EditText name_edittext, phone_edittext, address_edittext, comment_edittext, promo_code_edittext;
    private TextView delivery_price_textview, order_quantity_textview, orders_price_textview, total_price_textview,
            phone_tv, name_tv, address_tv, phone_998;
    private LinearLayout linearlayout_editable;
    private boolean editable = false;
    private double distance = 999999999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        setStatusBarColor();
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending));
        dialog.setCanceledOnTouchOutside(false);
        initViews();

    }

    private void initViews() {
        name_tv = findViewById(R.id.name_tv);
        phone_tv = findViewById(R.id.phone_tv);
        address_tv = findViewById(R.id.address_tv);
        phone_998 = findViewById(R.id.phone_998);
        linearlayout_editable = findViewById(R.id.linearlayout_editable);

        send_button = findViewById(R.id.send_button);
        name_edittext = findViewById(R.id.name_edittext);
        phone_edittext = findViewById(R.id.phone_edittext);
        address_edittext = findViewById(R.id.address_edittext);
        comment_edittext = findViewById(R.id.comment_edittext);
        promo_code_edittext = findViewById(R.id.promo_code_edittext);
        promo_code_edittext = findViewById(R.id.promo_code_edittext);
        delivery_price_textview = findViewById(R.id.delivery_price_textview);
        order_quantity_textview = findViewById(R.id.order_quantity_textview);
        orders_price_textview = findViewById(R.id.orders_price_textview);
        total_price_textview = findViewById(R.id.total_price_textview);

        Paper.init(this);
        restaurant_id = Paper.book().read("restaurant_id");
        double dis = CalculationByDistance();
        getPrices(dis);
        order_quantity = getIntent().getExtras().getString("order_quantity");
        products_price = getIntent().getExtras().getString("total_price");
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phone_edittext.getText().toString();
                String address = address_edittext.getText().toString();
                if (phone.equals("")) {
                    Toast.makeText(PlaceOrderActivity.this, R.string.phone_is_emty, Toast.LENGTH_SHORT).show();
                } else if (address.equals("")) {
                    Toast.makeText(PlaceOrderActivity.this, R.string.address_is_empty, Toast.LENGTH_SHORT).show();
                } else if (delivery_price_textview.getText().toString().equals("null")) {
                    Toast.makeText(PlaceOrderActivity.this, R.string.delivery_price_null, Toast.LENGTH_SHORT).show();
                } else {
                    showSendAlert();
                }
            }
        });

        setTexts();
    }

    private void setTexts() {
        Paper.init(this);
        String name = Paper.book().read("name");
        String phone = Paper.book().read("phone");

        address_edittext.setText(""+SplashActivity.ADDRESS);
        name_edittext.setText(name);
        phone_edittext.setText(phone);
        order_quantity_textview.setText(order_quantity + getString(R.string.ta));
        orders_price_textview.setText(products_price + " " + getString(R.string.sum));

    }

    public void getLocation() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent, PLACE_PICKER_REQUEST);
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

                Geocoder geocoder = new Geocoder(PlaceOrderActivity.this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(
                            latitude, longitude, 1);

                    String address = list.get(0).getAddressLine(0);
                    if (address.length()>40){
                        address_edittext.setText(address.substring(0,37)+"...");
                    }else{
                        address_edittext.setText(address);
                    }
                } catch (IOException e) {
                    String lat = (latitude+"").substring(0,9);
                    String lon = (longitude+"").substring(0,9);
                    address_edittext.setText(lat+" "+lon);
                    e.printStackTrace();
                }
            }
        }
    }


    public void sendOrder() {
        String customer_name = name_edittext.getText().toString();
        if (!customer_name.equals("")){
            name = customer_name;
        }
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.CREATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String name = name_edittext.getText().toString();
                                String phone = phone_edittext.getText().toString();
                                Paper.init(PlaceOrderActivity.this);
                                Paper.book().write("name", name);
                                Paper.book().write("phone", phone);
                                Paper.book().write("badge", "0");
                                showAlert();
                            } else {
                                int error_code = jsonObject.getInt("error_code");
                                if (error_code == 10) {
                                    showAlertInvalidOrder();
                                } else {
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PlaceOrderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(PlaceOrderActivity.this, "error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("auth_token", SplashActivity.TOKEN);
                return map;
            }

            @Override
            public byte[] getBody() {
                Map<String, String> order = new HashMap<>();
                order.put("restaurant_id", restaurant_id);
                order.put("delivery_price", delivery_price_textview.getText().toString());
                order.put("phone", phone_edittext.getText().toString());
                order.put("name", name);
                order.put("address", SplashActivity.ADDRESS);
                order.put("latitude", SplashActivity.LATITUDE);
                order.put("longitude", SplashActivity.LONGITUDE);
                order.put("comment", comment_edittext.getText().toString());
                return new JSONObject(order).toString().getBytes();
            }
        };
        queue.add(request);
    }

    private void showAlertInvalidOrder() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        alert.setIcon(R.drawable.ic_warning).setMessage(R.string.min_order_amount_less)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alert.create();
        alert.show();
    }

    private void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.order_send_successfuly)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PlaceOrderActivity.this.finish();
                    }
                });
        alert.create();
        alert.show();
    }
    private void showSendAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.send_order)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendOrder();
                    }
                });
        alert.create();
        alert.show();
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

    public double CalculationByDistance() {
        Paper.init(this);
        String res_lat = Paper.book().read("res_lat");
        String res_lon = Paper.book().read("res_lon");
        LatLng StartP = new LatLng(Double.parseDouble(res_lat), Double.parseDouble(res_lon));
        LatLng EndP = new LatLng(Double.parseDouble(SplashActivity.LATITUDE), Double.parseDouble(SplashActivity.LONGITUDE));
        int Radius = 6371;
        // radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + " KM " + kmInDec + " Meter " + meterInDec);
        return Radius * c;
    }

    private void getPrices(final double distance) {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SplashActivity.GET_PRICES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                min_distance = data.getString("min_distance");
                                min_distance_price = data.getString("min_price");
                                one_km_price = data.getString("one_km_price");
                                double price = 0;
                                if (min_distance != null && min_distance_price != null && one_km_price != null) {
                                    if (distance > (Double.parseDouble(min_distance) / 1000)) {
                                        price = Double.parseDouble(min_distance_price)
                                                + (distance - (Double.parseDouble(min_distance) / 1000)) * Double.parseDouble(one_km_price);
                                    } else {
                                        price = Double.parseDouble(min_distance_price);
                                    }
                                    price = price / 100;
                                    int price_int = (int) price;
                                    price_int = price_int * 100;
                                    delivery_price_textview.setText(price_int + "");
                                    int total_price = Integer.parseInt(products_price) + price_int;
                                    total_price_textview.setText(total_price + " " + getString(R.string.sum));
                                }
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PlaceOrderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(PlaceOrderActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        queue.add(request);
    }

    public void onBackPress(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}