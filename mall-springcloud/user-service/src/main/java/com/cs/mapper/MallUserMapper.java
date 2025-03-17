package com.cs.mapper;

import com.cs.domain.po.MallUser;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallUserMapper {


    List<MallUser> selectList(PageQueryUtil pageUtil);

    int getTotalList(PageQueryUtil pageUtil);

    int updateLockedFlagByPrimaryKey(@Param("lockedFlag") Byte lockedFlag, @Param("ids") Long[] ids);

    MallUser selectByLoginName(String loginName);

    int saveUser(MallUser mallUser);

    MallUser selectByLoginNameAndPassword(@Param("loginName") String loginName, @Param("passwordMd5") String passwordMd5);

    MallUser selectByPrimaryKey(Long userId);

    int updateUserInfoByPrimaryKey(MallUser mallUser);
}
