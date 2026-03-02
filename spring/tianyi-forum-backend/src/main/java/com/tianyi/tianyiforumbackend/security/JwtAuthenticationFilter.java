package com.tianyi.tianyiforumbackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 拦截所有请求，验证 JWT Token
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		// 从请求头中获取 Token
		String token = extractToken(request);

		// 如果 Token 存在且有效，则设置认证信息
		if (token != null && jwtUtil.validateToken(token)) {
			// 解析 Token 获取用户信息
			Claims claims = jwtUtil.parseToken(token);
			if (claims != null) {
				String role = claims.get("role", String.class);
				Long userId = getUserId(claims);

				// 创建认证对象
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userId, // principal 存储用户 ID
						null, // credentials 不需要密码
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));

				// 设置请求详情
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// 将认证信息存入 SecurityContext
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		// 继续执行过滤器链
		filterChain.doFilter(request, response);
	}

	/**
	 * 从请求头中提取 Token
	 * 支持 Authorization: Bearer <token> 格式
	 */
	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	/**
	 * 从 Claims 中获取用户 ID
	 */
	private Long getUserId(Claims claims) {
		Object userId = claims.get("userId");
		if (userId instanceof Integer) {
			return ((Integer) userId).longValue();
		} else if (userId instanceof Long) {
			return (Long) userId;
		}
		return null;
	}
}
