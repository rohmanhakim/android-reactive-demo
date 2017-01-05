package com.rohmanhakim.androidreactivedemo;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface SampleService {
    @GET("emails")
    Observable<List<String>> getEmails();
}
