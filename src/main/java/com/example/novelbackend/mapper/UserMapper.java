package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 根据账号查询用户（登录用）
    User findByAccount(@Param("account") String account);

    // 根据用户名查询用户（检查昵称是否存在）
    User findByUsername(@Param("username") String username);

    // 新增用户（注册用）
    int insert(User user);

    // 修改密码
    int updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    // 修改头像
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);

    // 根据ID查询用户
    User findById(@Param("id") Integer id);

    // 修改用户名
    int updateUsername(@Param("id") Integer id, @Param("newUsername") String newUsername);
}