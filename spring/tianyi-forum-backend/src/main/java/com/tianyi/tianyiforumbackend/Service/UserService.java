package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.RegisterRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UserPageQuery;
import com.tianyi.tianyiforumbackend.DTO.UserStatusQuery;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import com.tianyi.tianyiforumbackend.VO.UserVO;

import java.util.List;

public interface UserService {

	PageResponse<UserVO> findALl(UserPageQuery userPageQuery);

	UserVO findById(int id);

	SuccessResponse updateUsersStatus(Integer userId, UserStatusQuery userStatusQuery);

	LoginResponseVO login(LoginRequestDTO loginRequestDTO);

	LoginResponseVO register(RegisterRequestDTO registerRequestDTO);


}
