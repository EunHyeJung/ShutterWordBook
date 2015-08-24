package org.androidtown.shutterwordbook.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Activity.LockScreenActivity;
import org.androidtown.shutterwordbook.Helper.ScreenService;
import org.androidtown.shutterwordbook.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    private CheckBox checkboxLockScreen;
    private boolean lockScreenSetting;
    private SharedPreferences pref;

    boolean test = false;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_setting, container, false);

        checkboxLockScreen = (CheckBox) rootView.findViewById(R.id.checkBox_lockscreen);

        // 기존에 사용자가 했던 설정을 sharedPrefences에 저장해두었다가 가져온다
        pref = getActivity().getSharedPreferences("lockscreen", Context.MODE_PRIVATE);
        // getSharedPreference는 Activity에서만 호출이 가능하기 때문에 getActivity()부터 호출해야한다

        lockScreenSetting = pref.getBoolean("lockOn", false); // default : false
        checkboxLockScreen.setChecked(lockScreenSetting);

        // 체크박스 리스너 등록
        checkboxLockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {  // 사용자가 체크한 경우

                    // sharedPrefences설정 변경
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("lockOn", true);
                    editor.commit();

                    // lockscreen service 시작
                    Intent intent = new Intent(getActivity(), ScreenService.class);
                    getActivity().startService(intent);
                    // fragment에서는 호출이 안됨


                } else {
                    // sharedPrefences설정 변경
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("lockOn", false);
                    editor.commit();

                    // lockscreen service 중지
                    Intent intent = new Intent(getActivity(), ScreenService.class);
                    getActivity().stopService(intent);
                }
                if(test) {
                    Toast.makeText(getActivity(), isChecked + "", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //// test용
        Button buttonLockScreen = (Button) rootView.findViewById(R.id.button_setting_lock);
        if(test) {
            buttonLockScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LockScreenActivity.class);
                    startActivity(intent);
                    }
            });

        } else {
            buttonLockScreen.setVisibility(View.GONE);
        }

        return rootView;
    }
}
