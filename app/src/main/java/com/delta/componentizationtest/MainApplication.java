package com.delta.componentizationtest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.delta.commonlibs.ApplicationAsLibrary;
import com.delta.faultprocessing.FaultApplicaiton;
import com.delta.produceline.ProductApplication;
import com.github.mzule.activityrouter.router.RouterCallback;
import com.github.mzule.activityrouter.router.RouterCallbackProvider;
import com.github.mzule.activityrouter.router.SimpleRouterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @description :
 * @autHor :  V.Wenju.Tian
 * @date : 2017/4/24 16:53
 */


public class MainApplication extends Application implements RouterCallbackProvider {

    public List<ApplicationAsLibrary> applicationAsLibraries = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: 2017/4/24 在自动化配置的时候此处代码该如何生产？？ apt（添加注解的方式）
        add(new ProductApplication());
        add(new FaultApplicaiton());

        for (ApplicationAsLibrary applicationAsLibrary : applicationAsLibraries) {
            applicationAsLibrary.onCreateAsLibrary(this);
        }
    }

    public void add(ApplicationAsLibrary application) {

        applicationAsLibraries.add(application);
    }

    @Override
    public RouterCallback provideRouterCallback() {
        return new SimpleRouterCallback() {
            @Override
            public boolean beforeOpen(Context context, Uri uri) {
                if (uri.toString().startsWith("mzule://")) {
                    context.startActivity(new Intent(context, LaunchActivity.class));
                    return true;
                }
                return false;
            }

            @Override
            public void notFound(Context context, Uri uri) {
                context.startActivity(new Intent(context, NotFoundActivity.class));
            }

            @Override
            public void error(Context context, Uri uri, Throwable e) {
                context.startActivity(ErrorStackActivity.makeIntent(context, uri, e));
            }
        };
    }
}
