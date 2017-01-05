package com.rohmanhakim.androidreactivedemo;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MultipleTapDemoActivity extends AppCompatActivity {

    TextView textCount;
    TextView textTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_tap_demo);
        getSupportActionBar().setTitle("Multiple Click Demo");

        final Button btnTap = (Button) findViewById(R.id.btn_tap);
        textCount = (TextView) findViewById(R.id.text_count);
        textTotal = (TextView) findViewById(R.id.text_total);

        Observable<Void> tapStream = RxView.clicks(btnTap).share();

        Observable<Integer> multipleTapCountStream = tapStream.buffer(tapStream
                .debounce(1,TimeUnit.SECONDS))
                .map(new Func1<List<Void>, Integer>() {
                    @Override
                    public Integer call(List<Void> voids) {
                        Log.d("multipleTap map",String.valueOf(voids.size()));
                        return voids.size();
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        Log.d("multipleTap filter",String.valueOf(integer));
                        return integer >= 2;
                    }
                });

        Observer<Integer> multipleTapObserver = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.d("multipleTapStream","Tap Stream Completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("multipleTapStream",e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                showTapCount(integer);
            }
        };

        multipleTapCountStream.observeOn(AndroidSchedulers.mainThread())
                .subscribe(multipleTapObserver);

    }

    private void showTapCount(int size) {
        textCount.setText(String.valueOf(size) + " x Taps");
        textCount.setVisibility(View.VISIBLE);
        textCount.setScaleX(1f);
        textCount.setScaleY(1f);
        ViewCompat.animate(textCount)
                .scaleXBy(-1f)
                .scaleYBy(-1f)
                .setDuration(800)
                .setStartDelay(100);
    }

}
