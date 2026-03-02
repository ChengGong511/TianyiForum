package com.tianyi.tianyiforumbackend.config;

import com.tianyi.tianyiforumbackend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * 配置安全过滤器链
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 禁用 CSRF（因为使用 JWT，不需要 CSRF 保护）
				.csrf(csrf -> csrf.disable())

				// 配置 CORS
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// 配置会话管理：使用无状态会话
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// 配置请求授权
				.authorizeHttpRequests(auth -> auth
						// 允许登录接口无需认证
						.requestMatchers("/admin/auth/login", "/user/auth/login", "/user/auth/register").permitAll()
						// 用户端公开阅读接口放行
						.requestMatchers(HttpMethod.GET, "/portal/**").permitAll()

						// 管理员接口需要 ADMIN 角色
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// 其他所有请求都需要认证
						.anyRequest().authenticated())

				// 添加 JWT 过滤器
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 配置 CORS（跨域资源共享）
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 允许的源（开发环境）
		configuration.setAllowedOrigins(Arrays.asList(
				"http://localhost:5173",
				"http://localhost:5174",
				"http://localhost:3000",
				"https://09dd68b1aa4e.ngrok-free.app"));

		// 允许的 HTTP 方法
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

		// 允许的请求头
		configuration.setAllowedHeaders(Arrays.asList("*"));

		// 允许携带凭证
		configuration.setAllowCredentials(true);

		// 暴露的响应头
		configuration.setExposedHeaders(Arrays.asList("Authorization"));

		// 预检请求的有效期（秒）
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	/**
	 * 密码编码器
	 * 使用 BCrypt 加密算法
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
