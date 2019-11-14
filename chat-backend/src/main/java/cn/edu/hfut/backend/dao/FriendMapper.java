package cn.edu.hfut.backend.dao;

import cn.edu.hfut.backend.entity.Message;
import cn.edu.hfut.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendMapper {

    @Select("SELECT * from user WHERE ID in ( " +
            "SELECT friendId FROM friend WHERE friend.userId= #{userId})")
    List<User> getAllFriendById(Integer userId);

    @Select("SELECT * from user where ID = #{id}")
    User getInformationById(Integer id);
}