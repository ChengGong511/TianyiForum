package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintRequestDTO {
	private String targetType; // post/comment/user
	private Long targetId;
	private String reason;
	private String evidence;
}
