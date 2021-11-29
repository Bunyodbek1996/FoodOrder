package info.texnoman.foodorder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import info.texnoman.foodorder.forretrofit.GetToken;
import info.texnoman.foodorder.network.ApiClient;
import info.texnoman.foodorder.network.ApiInterface;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;

public class ConfirmActivity extends AppCompatActivity {
    private ImageView splashLogo;

    EditText confirm_code;
    TextView back_count_text;
    TextView confirm_btn;
    private String phone = "";
    int count = 119;
    public String tokenG;
    private ProgressBar progressBar;
    ProgressDialog loading;
    final Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Paper.init(ConfirmActivity.this);

        initVars();
        initViews();
        initBackCount();
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.show();
                sendConfirmCode();
            }
        });

        confirm_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==4){
                    loading.show();
                    sendConfirmCode();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void initVars() {
        loading = new ProgressDialog(this);
        loading.setMessage("Yuborilmoqda");
        loading.setCanceledOnTouchOutside(false);
    }

    private void initBackCount() {
        runnable = new Runnable() {
            public void run() {
                if (count-- > 0) {
                    handler.postDelayed(this, 1000);
                    if (count > 59) {
                        int c = count - 60;
                        if (c > 9) {
                            back_count_text.setText("01:" + c);
                        } else {
                            back_count_text.setText("01:0" + c);
                        }

                    } else {
                        if (count > 9) {
                            back_count_text.setText("00:" + count);
                        } else {
                            back_count_text.setText("00:0" + count);
                        }
                        if (count == 1){
                            startActivity(new Intent(ConfirmActivity.this, SignUpActivity.class));
                            ConfirmActivity.this.finish();
                        }
                    }

                }
            }
        };
        handler.post(runnable);
    }


    private void initViews() {

        progressBar = findViewById(R.id.progressBar);


        confirm_btn = findViewById(R.id.confirm_btn);
        confirm_code = findViewById(R.id.confirm_code);
        back_count_text = findViewById(R.id.back_count_text);
        phone = getIntent().getStringExtra("phone");

    }

    private void sendConfirmCode() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Map<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",confirm_code.getText().toString());
        Call<GetToken> call = apiInterface.getToken(map);
        call.enqueue(new Callback<GetToken>() {
            @Override
            public void onResponse(Call<GetToken> call, retrofit2.Response<GetToken> response) {
                GetToken getToken = response.body();
                if (getToken.isSuccess()){
                    String token = getToken.getData().getToken();
                    SplashActivity.TOKEN = token;
                    Paper.book().write("token", token);
                    startActivity(new Intent(ConfirmActivity.this, SplashActivity.class));
                    ConfirmActivity.this.finish();
                }else{
                    loading.dismiss();
                    int error_code = getToken.getError_code();
                    if (error_code == 1) {
                        Toast.makeText(ConfirmActivity.this, R.string.no_params, Toast.LENGTH_SHORT).show();
                    }

                    if (error_code == 7) {
                        Toast.makeText(ConfirmActivity.this, R.string.error_code, Toast.LENGTH_SHORT).show();
                    }
                    if (error_code == 5) {
                        Toast.makeText(ConfirmActivity.this, R.string.phone_number_not_found, Toast.LENGTH_SHORT).show();
                    }
                    if (error_code == 6) {
                        Toast.makeText(ConfirmActivity.this, R.string.reenter_phone_number, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetToken> call, Throwable t) {
                sendConfirmCode();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}