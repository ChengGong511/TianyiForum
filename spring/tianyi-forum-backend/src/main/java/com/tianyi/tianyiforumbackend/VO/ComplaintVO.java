package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintVO {
	private Long id;
	private String targetType; // post/comment/user
	private Long targetId;
	private String reason;
	private String evidence;
	private String reporter;
	private Long reporterId;
	private String status; // pending/resolved
	private String handleNote;
	private LocalDateTime createdAt;
	private LocalDateTime handledAt;
}
