package com.recom3.snow3.service;

import com.google.inject.Inject;
import com.recom3.snow3.model.User;
import com.recom3.snow3.repository.UserRepository;
import com.recom3.snow3.util.validation.ICallback;
import com.recom3.snow3.util.validation.StringValidation;

/**
 * Created by Recom3 on 25/01/2022.
 */

//@ContextSingleton
public class UserService {
    //@Inject
    private UserRepository mUserRepository;

    public UserService(UserRepository userRepository)
    {
        this.mUserRepository = userRepository;
    }

    public void delete() {
        this.mUserRepository.delete();
    }

    public void executeWhenUserIsLogged(ICallback paramICallback) {
        if (isLogged())
            paramICallback.execute();
    }

    public void executeWhenUserIsNotLogged(ICallback paramICallback) {
        if (!isLogged())
            paramICallback.execute();
    }

    public boolean isLogged() {
        return StringValidation.isNotEmpty(load().getToken());
    }

    public User load() {
        return this.mUserRepository.load();
    }

    public void save(User paramUser) {
        this.mUserRepository.save(paramUser);
    }
}
