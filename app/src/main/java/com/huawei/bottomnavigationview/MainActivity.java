package com.huawei.bottomnavigationview;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    String [] mTitle=new String[]{"推荐","应用","游戏","管理","我的"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HwBottomNavigationView view=findViewById(R.id.view);
        Drawable[] drawables= getTabIcons();
        for (int i = 0; i < 5; i++) {
            view.initItems(mTitle[i],drawables[i]);
        }
        view.setItemChecked(0);
        view.setItemHasMessage(1,true);
        view.setBottomNavListener(new HwBottomNavigationView.BottomNavListener() {
            @Override
            public void OnSelect() {
                Log.e("HwBottomNavigationView","OnSelect()");
            }

            @Override
            public void Cancel() {
                Log.e("HwBottomNavigationView","Cancel()");
            }

            @Override
            public void OnActive(int index) {

                Log.e("HwBottomNavigationView","OnActive:"+index);

            }
        });
    }

    protected Drawable[] getTabIcons() {
        return new Drawable[]{getResources().getDrawable(R.drawable.wisedist_mainscreen_bottomtab_recommend_selector),
                getResources().getDrawable(R.drawable.wisedist_mainscreen_bottomtab_classification_selector),
                getResources().getDrawable(R.drawable.wisedist_mainscreen_bottomtab_ranking_selector),
                getResources().getDrawable(R.drawable.wisedist_mainscreen_bottomtab_manager_selector),
                getResources().getDrawable(R.drawable.wisedist_mainscreen_bottomtab_my_selector)};
    }

}
