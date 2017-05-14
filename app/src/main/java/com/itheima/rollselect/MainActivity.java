package com.itheima.rollselect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        MyView view = (MyView) findViewById(R.id.view);
        ArrayList<String> list = new ArrayList<>();
        list.add("北京");
        list.add("天津");
        list.add("河北");
        list.add("保定");
        list.add("南京");
        list.add("厦门");
        list.add("三亚");
        list.add("海南");
        list.add("成都");
        list.add("重庆");
        list.add("安徽");
        list.add("上海");
        list.add("新疆");
        list.add("拉萨");
        list.add("青海");
        list.add("东南");
        list.add("西南");
        list.add("东北");
        list.add("东西");
        list.add("东东");
        list.add("南北");
        list.add("南西");
        list.add("西北");
        list.add("西东");
        list.add("北南");
        list.add("北东");
        list.add("北西");
        list.add("哈哈");
        list.add("呵呵");
        list.add("嘿嘿");
        list.add("哈啊哈");

        view.addData(list);
        view.setOnIsSelectedListener(new MyView.OnIsSelectedListener() {
            @Override
            public void onIsSelected(int id,final String desc) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "选择了"+desc, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
