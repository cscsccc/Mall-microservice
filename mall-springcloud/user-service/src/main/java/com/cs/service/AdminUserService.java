package com.cs.service;

import com.cs.domain.po.AdminUser;
import com.cs.pojo.AdminUserToken;

import javax.validation.constraints.NotEmpty;

public interface AdminUserService {

    String login(String username, String password);

    AdminUser getDetailsById(Long adminUserId);

    boolean updatePasswordById(Long adminUserId, String originalPassword, String newPassword);

    boolean logout(Long adminUserId);

    String updateAdminUserName(Long id, @NotEmpty(message = "loginUserName不能为空") String loginUserName, @NotEmpty(message = "nickName不能为空") String nickName);

    AdminUserToken getAdminUserToken(String token);
}
