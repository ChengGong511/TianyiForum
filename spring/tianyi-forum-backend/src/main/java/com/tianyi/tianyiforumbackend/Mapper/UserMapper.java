package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.UserPageQuery;
import com.tianyi.tianyiforumbackend.Eneity.User;
import com.tianyi.tianyiforumbackend.VO.UserVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface UserMapper {

	List<UserVO> findALL(UserPageQuery userPageQuery);

	@Select("select * from users where username = #{username}")
	User findByUsername(String username);

	@Select("select * from users where id=#{id}")
	UserVO findById(int id);

	@Update("update users set username = #{username}, email = #{email}, role = #{role}, status = #{status}, avatar_url = #{avatarUrl} where id = #{id}")
	int update(UserVO userVO);

	@Insert("insert into users (username,email,password, role, status, avatar_url, created_at,last_login_at, updated_at) values (#{username},#{email},#{password},#{role},#{status},#{avatarUrl},#{createdAt},#{lastLoginAt},#{updatedAt})")
	int insert(UserVO userVO);

	@Insert("insert into users (username, email, password, role, status,created_at,updated_at) values (#{username}, #{email}, #{password}, #{role}, #{status},#{createdAt},#{updatedAt})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insertUser(User user);

	@Delete("delete from users where id=#{id}")
	int delete(int id);

	@Select("SELECT COUNT(*) AS total_users FROM users")
	long findCount();

    User getByUsername(String username);
}
