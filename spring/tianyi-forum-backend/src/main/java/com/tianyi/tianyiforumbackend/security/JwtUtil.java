package com.tianyi.tianyiforumbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和解析 JWT Token
 */
@Component
public class JwtUtil {

	// JWT 密钥（生产环境应该从配置文件中读取）
	private static final String SECRET_KEY = "tianyi-forum-secret-key-for-jwt-token-generation-2024";

	// Token 有效期：7天（单位：毫秒）
	private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;

	// 获取签名密钥
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 生成 JWT Token
	 * 
	 * @param userId   用户ID
	 * @param username 用户名
	 * @param role     用户角色
	 * @return JWT Token
	 */
	public String generateToken(Long userId, String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("username", username);
		claims.put("role", role);

		return Jwts.builder()
				.claims(claims)
				.subject(username)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * 从 Token 中解析用户信息
	 * 
	 * @param token JWT Token
	 * @return Claims 对象，包含用户信息
	 */
	public Claims parseToken(String token) {
		try {
			return Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 验证 Token 是否有效
	 * 
	 * @param token JWT Token
	 * @return true 表示有效，false 表示无效
	 */
	public boolean validateToken(String token) {
		try {
			Claims claims = parseToken(token);
			return claims != null && !isTokenExpired(claims);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 检查 Token 是否过期
	 * 
	 * @param claims Token 中的声明信息
	 * @return true 表示已过期，false 表示未过期
	 */
	private boolean isTokenExpired(Claims claims) {
		return claims.getExpiration().before(new Date());
	}

	/**
	 * 从 Token 中获取用户 ID
	 * 
	 * @param token JWT Token
	 * @return 用户 ID
	 */
	public Long getUserIdFromToken(String token) {
		Claims claims = parseToken(token);
		if (claims != null) {
			Object userId = claims.get("userId");
			if (userId instanceof Integer) {
				return ((Integer) userId).longValue();
			} else if (userId instanceof Long) {
				return (Long) userId;
			}
		}
		return null;
	}

	/**
	 * 从 Token 中获取用户名
	 * 
	 * @param token JWT Token
	 * @return 用户名
	 */
	public String getUsernameFromToken(String token) {
		Claims claims = parseToken(token);
		return claims != null ? claims.get("username", String.class) : null;
	}

	/**
	 * 从 Token 中获取用户角色
	 * 
	 * @param token JWT Token
	 * @return 用户角色
	 */
	public String getRoleFromToken(String token) {
		Claims claims = parseToken(token);
		return claims != null ? claims.get("role", String.class) : null;
	}

	/**
	 * 获取 Token 过期时间（秒）
	 * 
	 * @return 过期时间（秒）
	 */
	public long getExpirationSeconds() {
		return EXPIRATION_TIME / 1000;
	}
}
