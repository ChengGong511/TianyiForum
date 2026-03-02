package com.tianyi.tianyiforumbackend.Service.Impl;

import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.Eneity.User;
import com.tianyi.tianyiforumbackend.Mapper.UserMapper;
import com.tianyi.tianyiforumbackend.Service.AdminSerivce;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import com.tianyi.tianyiforumbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminSerivce {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public LoginResponseVO login(LoginRequestDTO loginRequestDTO) {
		// 1. 根据用户名查询用户
		User user = userMapper.findByUsername(loginRequestDTO.getUsername());

		// 2. 验证用户是否存在
		if (user == null) {
			throw new RuntimeException("用户名或密码错误");
		}

		// 3. 验证密码是否正确

		//先对当前输入的密码进行加密，然后与数据库中的加密密码进行匹配
		//loginRequestDTO.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));
		/*if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
			throw new RuntimeException("用户名或密码错误");
		}*/
		if (!user.getPassword().equals(loginRequestDTO.getPassword())) {
			throw new RuntimeException("用户名或密码错误");
		}
		// 4. 验证用户角色是否为管理员
		if (!"admin".equalsIgnoreCase(user.getRole())) {
			throw new RuntimeException("无管理员权限");
		}

		// 5. 验证用户状态是否正常
		if (!"active".equalsIgnoreCase(user.getStatus())) {
			throw new RuntimeException("账号已被禁用");
		}

		// 6. 生成 JWT Token
		String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

		// 7. 构造响应对象
		LoginResponseVO response = new LoginResponseVO();
		response.setToken(token);
		response.setExpiresIn((int) jwtUtil.getExpirationSeconds());

		return response;
	}
}
