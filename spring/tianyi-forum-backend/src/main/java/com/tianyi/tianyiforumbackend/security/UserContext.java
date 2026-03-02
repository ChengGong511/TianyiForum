package com.tianyi.tianyiforumbackend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户的信息
 */
@Component
public class UserContext {

	/**
	 * 获取当前登录用户的 ID
	 * 
	 * @return 用户 ID，如果未登录返回 null
	 */
	public static Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& authentication.getPrincipal() instanceof Long) {
			return (Long) authentication.getPrincipal();
		}
		return null;
	}

	/**
	 * 获取当前用户的认证对象
	 * 
	 * @return Authentication 对象
	 */
	public static Authentication getCurrentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * 检查当前用户是否已认证
	 * 
	 * @return true 表示已认证，false 表示未认证
	 */
	public static boolean isAuthenticated() {
		Authentication authentication = getCurrentAuthentication();
		return authentication != null && authentication.isAuthenticated();
	}

	/**
	 * 检查当前用户是否有指定角色
	 * 
	 * @param role 角色名称（不需要 ROLE_ 前缀）
	 * @return true 表示有该角色，false 表示没有
	 */
	public static boolean hasRole(String role) {
		Authentication authentication = getCurrentAuthentication();
		if (authentication == null) {
			return false;
		}
		return authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
	}
}
