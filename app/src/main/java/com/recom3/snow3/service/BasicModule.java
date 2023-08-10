package com.recom3.snow3.service;

import android.app.Application;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.recom3.snow3.MainViewPagerActivity;
import com.recom3.snow3.repository.UserRepository;

/**
 * Created by Recom3 on 06/03/2022.
 */

public class BasicModule extends AbstractModule {

    //@Inject
    Context context;

    public BasicModule(Context context)
    {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(UserRepository.class)
                .toInstance(new UserRepository(context));
    }
}
