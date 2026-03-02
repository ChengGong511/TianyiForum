package com.tianyi.tianyiforumbackend.Controller.user;

import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.UserService;
import com.tianyi.tianyiforumbackend.VO.UserProfileVO;
import com.tianyi.tianyiforumbackend.VO.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 用户个人信息接口
 * 需要登录才能访问
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserProfileController {

	@Autowired
	private UserService userService;

	/**
	 * 获取当前登录用户的个人信息
	 */
	@GetMapping("/profile")
	public Result<UserProfileVO> getProfile() {
		// 从 SecurityContext 获取当前用户 ID
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return Result.error("未登录");
		}

		Long userId = (Long) auth.getPrincipal();
		log.info("获取用户信息: userId={}", userId);

		UserVO userVO = userService.findById(userId.intValue());
		if (userVO == null) {
			return Result.error("用户不存在");
		}

		// 转换为 UserProfileVO（隐藏敏感信息）
		UserProfileVO profile = new UserProfileVO();
		profile.setId(userVO.getId());
		profile.setUsername(userVO.getUsername());
		profile.setEmail(userVO.getEmail());
		profile.setAvatarUrl(userVO.getAvatarUrl());
		profile.setCreatedAt(userVO.getCreatedAt());
		profile.setLastLoginAt(userVO.getLastLoginAt());

		return Result.success(profile);
	}
}
