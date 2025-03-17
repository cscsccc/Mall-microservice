package com.cs.mapper;

import com.cs.domain.po.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserMapper {
    AdminUser login(@Param("username") String username, @Param("password") String password);

    AdminUser getDetailsById(Long id);

    int updateByIdSelective(AdminUser adminUser);

    AdminUser selectAdminUserByLoginUserName(String loginUserName);

    int updateNameByPrimaryKey(@Param("id") Long id, @Param("loginUserName") String loginUserName, @Param("nickName") String nickName);
}
