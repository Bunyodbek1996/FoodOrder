package info.texnoman.foodorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import info.texnoman.foodorder.other.MyDialogFragment;
import io.paperdb.Paper;


public class AccountActivity extends AppCompatActivity {
    ImageView place_picker,language_imageview;
    TextView address_edt,lang_edt,save_btn,support_phone_tv,log_out_tv;
    public static EditText name_edt,phone_edt;
    int PLACE_PICKER_REQUEST = 1001;
    Button save_name_btn;
    double distance = 999999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        setStatusBarColor();


        initViews();

    }

    private void initViews() {
        log_out_tv = findViewById(R.id.log_out_tv);
        support_phone_tv = findViewById(R.id.support_phone_tv);
        save_btn = findViewById(R.id.save_btn);
        place_picker = findViewById(R.id.place_picker);
        phone_edt = findViewById(R.id.phone_edt);
        language_imageview = findViewById(R.id.language_imageview);
        address_edt = findViewById(R.id.address_edt);
        name_edt = findViewById(R.id.name_edt);
        lang_edt = findViewById(R.id.lang_edt);

        Paper.init(this);
        String name = getString(R.string.name);
        String address = Paper.book().read("address");
        String lang = Paper.book().read("lang");
        String phone = Paper.book().read("phone");
        name = Paper.book().read("name");
        if (name != null){
            name_edt.setText(name);
        }
        if (phone != null){
            phone_edt.setText(phone);
        }
        if (lang.equals("uz")){
            lang_edt.setText(getString(R.string.uzbek));
        }else if(lang.equals("ru")){
            lang_edt.setText(getString(R.string.russian));
        }else{
            lang_edt.setText(getString(R.string.english));
        }
        if (address.length()>35){
            address_edt.setText(address.substring(0,32)+"...");
        }else{
            address_edt.setText(address);
        }
        log_out_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        support_phone_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPhone();
            }
        });


        language_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_edt.getText().toString();
                String phone = phone_edt.getText().toString();
                Paper.init(AccountActivity.this);
                Paper.book().write("name",name);
                Paper.book().write("phone",phone);
                Toast.makeText(AccountActivity.this, R.string.saved_chenges, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                AccountActivity.this.finish();
            }
        });

        save_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }


    void showDialog() {
        FragmentManager fm = getSupportFragmentManager();
        MyDialogFragment dialogFragment = MyDialogFragment.newInstance(2);
        dialogFragment.show(fm, "fragment_edit_name");
    }
    public void intentPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:889651000"));
        startActivity(intent);
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
    public void logOut(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.log_out)
                .setMessage(R.string.delete_account)
                .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Paper.init(AccountActivity.this);
                        Paper.book().write("address","");
                        Paper.book().write("token","");
                        finishAndRemoveTask();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alert.create();
        alert.show();
    }

}