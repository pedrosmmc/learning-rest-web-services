package com.pedrocoelho.restwebservices.learningrestwebservices.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class UserDaoService {
    private static List<User> users = new ArrayList<>();
    private static long usersCount = 1;

    // hardcoded users
    static {
        users.add(new User(1L, "Adam", "adam@gmail.coz", new Date()));
        users.add(new User(2L, "Eve", "eve@gmail.coz", new Date()));
        users.add(new User(3L, "Jonathan", "jona@gmail.coz", new Date()));
        users.add(new User(4L, "Nathaniel", "nate@gmail.coz", new Date()));
        users.add(new User(5L, "David", "david@gmail.coz", new Date()));
        users.add(new User(6L, "Methusalem", "met@gmail.coz", new Date()));
        users.add(new User(7L, "Enoch", "enoch@gmail.coz", new Date()));
        users.add(new User(8L, "Judah", "judah@gmail.coz", new Date()));
        usersCount = 9;
    }

    public List<User> getAll() {
        return users;
    }

    public User add(User user) {
        if (user.getId() == null) user.setId(++usersCount);
        users.add(user);
        return user;
    }

    public User get(long id) {
        for (User user : users)
            if (user.getId() == id)
                return user;
        return null;
    }

    public User delete(long id) {
        Iterator<User> it = users.iterator();
        while(it.hasNext()) {
            User user = it.next();
            if(user.getId() == id) {
                it.remove();
                return user;
            }
        }
        return null;
    }
}