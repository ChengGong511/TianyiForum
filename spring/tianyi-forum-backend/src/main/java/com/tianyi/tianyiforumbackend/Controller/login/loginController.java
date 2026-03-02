package com.tianyi.tianyiforumbackend.Controller.login;

import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.AdminSerivce;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
@Slf4j
public class loginController {

	@Autowired
	AdminSerivce adminSerivce;

	@PostMapping("/login")
	public Result<LoginResponseVO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
		try {
			log.info("管理员登录：{}", loginRequestDTO);
			LoginResponseVO response = adminSerivce.login(loginRequestDTO);
			return Result.success(response);
		} catch (RuntimeException e) {
			return Result.error(e.getMessage());
		}
	}
}
