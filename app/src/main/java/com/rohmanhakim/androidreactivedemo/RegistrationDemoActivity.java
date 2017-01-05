package com.rohmanhakim.androidreactivedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

public class RegistrationDemoActivity extends AppCompatActivity {

    EditText etEmail;
    TextView textEmailAlert;
    EditText etPassword;
    TextView textPasswordAlert;
    EditText etPasswordConfirmation;
    TextView textPasswordConfirmationAlert;
    Button btnSubmit;

    Retrofit retrofit;

    SampleService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_demo);

        // Inisialisasi form
        etEmail = (EditText) findViewById(R.id.et_email);
        textEmailAlert = (TextView) findViewById(R.id.text_email_alert);
        etPassword = (EditText) findViewById(R.id.et_password);
        textPasswordAlert = (TextView) findViewById(R.id.text_password_alert);
        etPasswordConfirmation = (EditText) findViewById(R.id.et_password_confirmation);
        textPasswordConfirmationAlert = (TextView) findViewById(R.id.text_password_confirmation_alert);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        // Inisialisasi state (keadaan) default form
        textEmailAlert.setVisibility(View.GONE);
        textPasswordAlert.setVisibility(View.GONE);
        textPasswordConfirmationAlert.setVisibility(View.GONE);
        btnSubmit.setEnabled(false);

        // Inisialisasi retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inisialisasi service sample
        service = retrofit.create(SampleService.class);

        // ================================================================================
        // OBSERVABLES
        // ================================================================================

        // Return true jika email user sudah terpakai
        Observable<Boolean> emailStream = RxTextView.textChanges(etEmail)
                .map(new Func1<CharSequence, String>() {
                    @Override
                    public String call(CharSequence charSequence) {
                        return charSequence.toString();
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String input) {
                        return input.length() > 3;
                    }
                })
                .debounce(100,TimeUnit.MILLISECONDS)
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String input) {
                        return checkIfEmailExistFromAPI(input);
                    }
                });

        // Return true jika password yang diketik user < 6 karakter
        Observable<Boolean> passwordStream = RxTextView.textChanges(etPassword)
                .map(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return !TextUtils.isEmpty(charSequence)
                                && charSequence.toString().trim().length() < 6;
                    }
                });

        Observable<Boolean> passwordConfirmationStream = Observable.merge(
                RxTextView.textChanges(etPassword)
                        .map(new Func1<CharSequence, Boolean>() {
                            @Override
                            public Boolean call(CharSequence charSequence) {
                                return !charSequence.toString().trim().equals(etPasswordConfirmation.getText().toString());
                            }
                        }),
                RxTextView.textChanges(etPasswordConfirmation)
                        .map(new Func1<CharSequence, Boolean>() {
                            @Override
                            public Boolean call(CharSequence charSequence) {
                                return !charSequence.toString().trim().equals(etPassword.getText().toString());
                            }
                        })
        );


        Observable<Boolean> emptyFieldStream = Observable.combineLatest(
                RxTextView.textChanges(etEmail)
                        .map(new Func1<CharSequence, Boolean>() {
                            @Override
                            public Boolean call(CharSequence charSequence) {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                RxTextView.textChanges(etPassword)
                        .map(new Func1<CharSequence, Boolean>() {
                            @Override
                            public Boolean call(CharSequence charSequence) {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                RxTextView.textChanges(etPasswordConfirmation)
                        .map(new Func1<CharSequence, Boolean>() {
                            @Override
                            public Boolean call(CharSequence charSequence) {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                new Func3<Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean emailEmpty, Boolean passwordEmpty, Boolean passwordConfirmationEmpty) {
                        return emailEmpty || passwordEmpty || passwordConfirmationEmpty;
                    }
                }
        );

        Observable<Boolean> invalidFieldsStream = Observable.combineLatest(
                emailStream,
                passwordStream,
                passwordConfirmationStream,
                emptyFieldStream, new Func4<Boolean, Boolean, Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean emailInvalid, Boolean passwordInvalid, Boolean passwordConfirmationInvalid, Boolean emptyFieldExist) {
                return !emailInvalid && !passwordInvalid && !passwordConfirmationInvalid && !emptyFieldExist;
            }
        });


        // ================================================================================
        // OBSERVERS
        // ================================================================================

        // Inisialisasi observer untuk field email
        // subscribe ke stream email, menampilkan peringatan ketika email yg diketik user sudah dipakai di backend
        Observer<Boolean> emailObserver = new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("rx","Email stream completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("rx",e.getMessage());
            }

            @Override
            public void onNext(Boolean emailExist) {
                Log.d("emailObserver",String.valueOf(emailExist.booleanValue()));
                showEmailExistAlert(emailExist.booleanValue());
            }
        };

        Observer<Boolean> passwordObserver = new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("rx","Password stream completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("rx",e.getMessage());
            }

            @Override
            public void onNext(Boolean passwordLessThanLimit) {
                Log.d("passwordObserver",String.valueOf(passwordLessThanLimit.booleanValue()));
                showPasswordMinimalAlert(passwordLessThanLimit.booleanValue());
            }
        };

        Observer<Boolean> passwordConfirmationObserver = new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("rx","Password confirmation stream completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("rx",e.getMessage());
            }

            @Override
            public void onNext(Boolean passwordConfirmationDontMatch) {
                Log.d("passwordConfirmation",String.valueOf(passwordConfirmationDontMatch.booleanValue()));
                showPasswordConfirmationAlert(passwordConfirmationDontMatch.booleanValue());
            }
        };

        Observer<Boolean> invalidFieldsObserver = new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("rx","All field valid stream completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("rx",e.getMessage());
            }

            @Override
            public void onNext(Boolean invalidFieldExist) {
                Log.d("invalidFieldsStream",String.valueOf(invalidFieldExist.booleanValue()));
                btnSubmit.setEnabled(invalidFieldExist);
            }
        };

        // ================================================================================
        // SUBSCRIBING
        // ================================================================================

        emailStream.subscribe(emailObserver);

        passwordStream.subscribe(passwordObserver);

        passwordConfirmationStream.subscribe(passwordConfirmationObserver);

        invalidFieldsStream.subscribe(invalidFieldsObserver);

    }

    // Ambil daftar email dari APi, kemudian cek apakah email user sudah dipakai
    public Observable<Boolean> checkIfEmailExistFromAPI(final String input){
        return service.getEmails()
                .flatMap(new Func1<List<String>, Observable<String>>() { // Mengubah stream of List<String> menjadi stream of String
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                }).contains(input) // Cek apakah email di emit oleh stream sebelumnya
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void showEmailExistAlert(boolean value){
        if(value) {
            textEmailAlert.setText(getString(R.string.email_exist_alert));
            textEmailAlert.setVisibility(View.VISIBLE);
        } else {
            textEmailAlert.setVisibility(View.GONE);
        }
    }

    public void showPasswordMinimalAlert(boolean value){
        if(value) {
            textPasswordAlert.setText(getString(R.string.password_minimal_alert));
            textPasswordAlert.setVisibility(View.VISIBLE);
        } else {
            textPasswordAlert.setVisibility(View.GONE);
        }
    }

    public void showPasswordConfirmationAlert(boolean value){
        if(value){
            textPasswordConfirmationAlert.setText(R.string.password_confirmation_does_not_match_alert);
            textPasswordConfirmationAlert.setVisibility(View.VISIBLE);
        } else {
            textPasswordConfirmationAlert.setVisibility(View.GONE);
        }
    }
}
