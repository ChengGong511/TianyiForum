package com.tianyi.tianyiforumbackend.Service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.RegisterRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UserPageQuery;
import com.tianyi.tianyiforumbackend.DTO.UserStatusQuery;
import com.tianyi.tianyiforumbackend.Eneity.User;
import com.tianyi.tianyiforumbackend.Mapper.UserMapper;
import com.tianyi.tianyiforumbackend.Service.UserService;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import com.tianyi.tianyiforumbackend.VO.UserVO;
import com.tianyi.tianyiforumbackend.security.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSerivceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public PageResponse<UserVO> findALl(UserPageQuery userPageQuery) {
		PageHelper.startPage(userPageQuery.getPage(), userPageQuery.getPageSize());

		List<UserVO> userVOList = userMapper.findALL(userPageQuery);
		PageResponse<UserVO> pageResponse = new PageResponse<UserVO>();
		pageResponse.setItems(userVOList);
		long t = userMapper.findCount();
		pageResponse.setTotal(t);
		return pageResponse;

	}

	@Override
	public UserVO findById(int id) {
		return userMapper.findById(id);
	}

	@Override
	public SuccessResponse updateUsersStatus(Integer userId, UserStatusQuery userStatusQuery) {
		UserVO userVO = userMapper.findById(userId);
		userVO.setStatus(userStatusQuery.getStatus());
		userMapper.update(userVO);
		return null;
	}

	@Override
	public LoginResponseVO login(LoginRequestDTO loginRequestDTO) {
		// 1. 根据用户名查询用户
		User user = userMapper.findByUsername(loginRequestDTO.getUsername());

		// 2. 验证用户是否存在
		if (user == null) {
			throw new RuntimeException("用户名或密码错误");
		}

		// 3. 验证密码是否正确

		// 先对当前输入的密码进行加密，然后与数据库中的加密密码进行匹配
		// loginRequestDTO.setPassword(passwordEncoder.encode(loginRequestDTO.getPassword()));
		/*
		 * if (!passwordEncoder.matches(loginRequestDTO.getPassword(),
		 * user.getPassword())) {
		 * throw new RuntimeException("用户名或密码错误");
		 * }
		 */
		if (!user.getPassword().equals(loginRequestDTO.getPassword())) {
			throw new RuntimeException("用户名或密码错误");
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

	@Override
	public LoginResponseVO register(RegisterRequestDTO registerRequestDTO) {
		User user=userMapper.getByUsername(registerRequestDTO.getUsername());
		if(user!=null){
			throw new RuntimeException("用户名已存在");
		}

		User newUser = new User();
		BeanUtils.copyProperties(registerRequestDTO, newUser);
		newUser.setRole("user");
		newUser.setStatus("active");
		newUser.setCreatedAt(LocalDateTime.now());
		newUser.setUpdatedAt(LocalDateTime.now());
		userMapper.insertUser(newUser);

		LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setUsername(registerRequestDTO.getUsername());
		loginRequestDTO.setPassword(registerRequestDTO.getPassword());
		return this.login(loginRequestDTO);

	}


}
