package com.tianyi.tianyiforumbackend.config;

import com.tianyi.tianyiforumbackend.Eneity.User;
import com.tianyi.tianyiforumbackend.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时自动执行，用于创建默认管理员账号
 * 
 * 注意：生产环境中应该删除或禁用此组件
 */
@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		// 检查是否已存在管理员账号
		User existingAdmin = userMapper.findByUsername("admin");

		if (existingAdmin == null) {
			// 创建默认管理员账号
			// 注意：这里仅用于开发测试，生产环境需要删除或修改
			System.out.println("===========================================");
			System.out.println("创建默认管理员账号:");
			System.out.println("用户名: admin");
			System.out.println("密码: admin123");
			System.out.println("请在生产环境中修改默认密码！");
			System.out.println("===========================================");

			// 注：这里只是打印提示，实际插入需要取消下面的注释
			// 并确保数据库中有 users 表

			/*
			 * User admin = new User();
			 * admin.setUsername("admin");
			 * admin.setEmail("admin@tianyi.com");
			 * admin.setPassword(passwordEncoder.encode("admin123"));
			 * admin.setRole("admin");
			 * admin.setStatus("active");
			 * admin.setCreatedAt(LocalDateTime.now());
			 * admin.setUpdatedAt(LocalDateTime.now());
			 * 
			 * // 使用 UserMapper 的插入方法
			 * // userMapper.insert(admin);
			 */
		}
	}
}
