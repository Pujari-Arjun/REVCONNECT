package com.revconnect.dao;

import com.revconnect.model.Share;
import java.util.List;

public interface ShareDAO {
    boolean sharePost(int postId, int userId);
    int getShareCount(int postId);
    List<Share> findByPostId(int postId);
}