package com.revconnect.dao;

import com.revconnect.model.SecurityQuestion;
import com.revconnect.model.UserSecurityAnswer;
import java.util.List;

public interface SecurityQuestionDAO {
    List<SecurityQuestion> getAllQuestions();
    boolean saveAnswer(UserSecurityAnswer answer);
    UserSecurityAnswer findAnswer(int userId, int questionId);
}