package com.tianyi.tianyiforumbackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 用于生成 BCrypt 加密的密码，方便创建测试账号
 * 
 * 运行此类的 main 方法即可生成加密密码
 */
public class PasswordGenerator {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// 生成 "admin123" 的 BCrypt 密码
		String password = "admin123";
		String encodedPassword = encoder.encode(password);

		System.out.println("===========================================");
		System.out.println("原始密码: " + password);
		System.out.println("BCrypt 加密后: " + encodedPassword);
		System.out.println("===========================================");
		System.out.println("\n复制上面的加密密码，插入到数据库中：");
		System.out.println("\nINSERT INTO users (username, email, password, role, status) VALUES");
		System.out.println("('admin', 'admin@tianyi.com', '" + encodedPassword + "', 'admin', 'active');");
		System.out.println("===========================================");
	}
}
