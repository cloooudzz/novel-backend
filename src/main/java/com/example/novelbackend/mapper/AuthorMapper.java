package com.example.novelbackend.mapper;

import com.example.novelbackend.entity.Author;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AuthorMapper {

    // 根据用户ID查询作者信息
    @Select("SELECT * FROM author WHERE user_id = #{userId}")
    Author findByUserId(@Param("userId") Integer userId);

    // 根据笔名查询
    @Select("SELECT * FROM author WHERE pen_name = #{penName}")
    Author findByPenName(@Param("penName") String penName);

    // 插入作者信息
    @Insert("INSERT INTO author (user_id, pen_name, contact, genre, intro, status, create_time) " +
            "VALUES (#{userId}, #{penName}, #{contact}, #{genre}, #{intro}, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Author author);

    // 检查用户是否是作者
    @Select("SELECT COUNT(*) FROM author WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Integer userId);
}