package com.revconnect.dao;

import com.revconnect.model.Profile;

public interface ProfileDAO {
    boolean createProfile(Profile profile);
    Profile findByUserId(int userId);
    boolean updateProfile(Profile profile);
}