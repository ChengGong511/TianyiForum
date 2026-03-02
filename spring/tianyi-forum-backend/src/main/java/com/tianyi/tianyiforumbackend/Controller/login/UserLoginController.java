package com.tianyi.tianyiforumbackend.Controller.login;

import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.RegisterRequestDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.UserService;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/auth")
@Slf4j
public class UserLoginController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public Result<LoginResponseVO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
		try {
			log.info("用户登录：{}", loginRequestDTO);
			LoginResponseVO response = userService.login(loginRequestDTO);
			return Result.success(response);
		} catch (RuntimeException e) {
			return Result.error(e.getMessage());
		}
	}


	@PostMapping("/register")
	public Result<LoginResponseVO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
		try {
			log.info("用户注册：{}", registerRequestDTO);
			LoginResponseVO response=userService.register(registerRequestDTO);
			return Result.success(response);
		} catch (RuntimeException e) {
			return Result.error(e.getMessage());
		}
	}


}
