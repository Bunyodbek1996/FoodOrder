package info.texnoman.foodorder.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;

import info.texnoman.foodorder.R;
import info.texnoman.foodorder.SplashActivity;

import io.paperdb.Paper;

public class MyDialogFragment extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static MyDialogFragment newInstance(int num) {
        MyDialogFragment f = new MyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        final RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        RadioButton radio_rus = v.findViewById(R.id.radio_rus);
        RadioButton radio_uzbek = v.findViewById(R.id.radio_uzbek);
        RadioButton radio_en = v.findViewById(R.id.radio_en);

        Paper.init(getContext());
        String lang = Paper.book().read("lang");
        if (lang != null){
            if (lang.equals("uz")){
                radio_uzbek.setChecked(true);
            }else if (lang.equals("ru")){
                radio_rus.setChecked(true);
            }else if (lang.equals("en")){
                radio_en.setChecked(true);
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.radio_uzbek){
                    Paper.book().write("lang","uz");
                    MyDialogFragment.this.dismiss();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    getContext().startActivity(intent);
                    getActivity().finishAffinity();
                }else if (id == R.id.radio_rus){
                    Paper.book().write("lang","ru");
                    MyDialogFragment.this.dismiss();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    getContext().startActivity(intent);
                    getActivity().finishAffinity();
                }else if (id == R.id.radio_en){
                    Paper.book().write("lang","en");
                    MyDialogFragment.this.dismiss();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    getContext().startActivity(intent);
                    getActivity().finishAffinity();
                }
            }
        });
        return v;
    }
}