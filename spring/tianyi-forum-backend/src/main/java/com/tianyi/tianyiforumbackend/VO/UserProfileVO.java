package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {
	private Long id;
	private String username;
	private String email;
	private String avatarUrl;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
}
