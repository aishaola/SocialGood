package com.example.socialgood;

import android.app.Application;

import com.example.socialgood.models.Comment;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES);// read timeout;

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(ParseUserSocial.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Donation.class);
        ParseObject.registerSubclass(Fundraiser.class);
        ParseUser.registerSubclass(ParseUserSocial.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("socialGood") // should correspond to APP_ID env variable
                .clientKey("myMasterKey")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://social-good-app.herokuapp.com/parse/").build());

    }
}