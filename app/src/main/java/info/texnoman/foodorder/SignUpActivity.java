package info.texnoman.foodorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.PhoneAuthProvider;

import info.texnoman.foodorder.forretrofit.UserResponse;
import info.texnoman.foodorder.network.ApiClient;
import info.texnoman.foodorder.network.ApiInterface;
import info.texnoman.foodorder.other.GPSTracker;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;


public class SignUpActivity extends AppCompatActivity {
    private ImageView splashLogo;
    private TextView confirm_btn,support_phone_tv;
    private TextInputEditText phone;
    private ProgressBar progressBar;
    ApiInterface apiInterface;
    UserResponse userResponse;
    Context context;
    GPSTracker gpsTracker;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setStatusBarColor();
        initVars();
        initViews();
        displayLocationSettingsRequest(this);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
                String phoneNumber = "998"+phone.getText().toString();

                Paper.init(SignUpActivity.this);
                Paper.book().write("phone",phoneNumber.substring(3));
                sendPhoneRetrofit();
            }
        });

        checkRunTimePermission();
    }
    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(context);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,}, 10);
            }
        } else {
            gpsTracker = new GPSTracker(context); //GPSTracker is class that is used for retrieve user current location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gpsTracker = new GPSTracker(context);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    context.getPackageName(), null));
                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i, 1001);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
                //code for deny
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case 1001:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        gpsTracker = new GPSTracker(context);
                        if (gpsTracker.canGetLocation()) {
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},10);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void sendPhoneRetrofit() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> call = apiInterface.getUserResponse("998"+phone.getText().toString());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                userResponse = response.body();
                if (userResponse.isSuccess()){
                    Intent intent = new Intent(SignUpActivity.this,ConfirmActivity.class);
                    intent.putExtra("code",userResponse.getData().getSms_code());
                    intent.putExtra("phone","998"+phone.getText().toString());
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }else{
                    int error_code = userResponse.getError_code();
                    if (error_code == 1){
                        Toast.makeText(SignUpActivity.this, R.string.not_enough_parametrs, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 2){
                        Toast.makeText(SignUpActivity.this, R.string.phone_number_invalid, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 3){
                        Toast.makeText(SignUpActivity.this, R.string.too_many_tries, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 4){
                        Toast.makeText(SignUpActivity.this, R.string.unknow_query_error, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 5){
                        Toast.makeText(SignUpActivity.this, R.string.phone_number_not_found, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 6){
                        Toast.makeText(SignUpActivity.this, R.string.code_is_expired, Toast.LENGTH_SHORT).show();
                    }else if (error_code == 7){
                        Toast.makeText(SignUpActivity.this, R.string.error_code, Toast.LENGTH_SHORT).show();
                    }else if(error_code == 8){
                        Toast.makeText(SignUpActivity.this, R.string.token_not_found, Toast.LENGTH_SHORT).show();
                    }else if(error_code == 9){
                        Toast.makeText(SignUpActivity.this, R.string.customer_not_found, Toast.LENGTH_SHORT).show();
                    }else if(error_code == 10){
                        Toast.makeText(SignUpActivity.this, R.string.invalid_order_amount, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                String error = t.getMessage();
                if (error.equals("timeout")){
                    Intent intent = new Intent(SignUpActivity.this,ConfirmActivity.class);
                    intent.putExtra("phone","998"+phone.getText().toString());
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }
            }
        });
    }

    private void initVars() {
        RequestQueue queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);
    }


    private void initViews() {
        context = this;
        support_phone_tv = findViewById(R.id.support_phone_tv);
        splashLogo = findViewById(R.id.splashLogo);
        phone = findViewById(R.id.phone);
        confirm_btn = findViewById(R.id.confirm_btn);


        support_phone_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPhone();
            }
        });

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
                            status.startResolutionForResult(SignUpActivity.this, 111);
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
    public void intentPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:889651000"));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111){
            if (resultCode == RESULT_OK){
            }else{
                this.finish();
            }
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
}