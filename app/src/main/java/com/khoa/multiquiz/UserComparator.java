package com.khoa.multiquiz;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        // To sort in descending order, we return the opposite of the natural ordering.
        return user2.getUserPoint().compareTo(user1.getUserPoint());
    }
}