package com.library.factory;

import com.library.model.User;
import java.time.LocalDate;

public class UserFactory implements EntityFactory<User> {
    @Override
    public User create(Object... params) {
        // params: userId, name, email, phoneNumber, address, membershipDate(LocalDate)
        if (params.length < 6) throw new IllegalArgumentException("UserFactory requires 6 parameters");
        return new User(
                (String)    params[0],
                (String)    params[1],
                (String)    params[2],
                (String)    params[3],
                (String)    params[4],
                (LocalDate) params[5]
        );
    }
}
