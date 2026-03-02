package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.DTO.UserPageQuery;
import com.tianyi.tianyiforumbackend.DTO.UserStatusQuery;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.UserService;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/users")
	public Result<PageResponse<UserVO>> listUsers(@RequestBody UserPageQuery userPageQuery) {
		PageResponse<UserVO> pageResp = userService.findALl(userPageQuery);
		return Result.success(pageResp);
	}

	@PutMapping("/users/{userId}/status")
	public Result<PageResponse<UserVO>> updateUsers(@PathVariable Integer userId, @RequestBody UserStatusQuery userStatusQuery) {
			userService.updateUsersStatus(userId,userStatusQuery);
		return Result.success();
	}
}
