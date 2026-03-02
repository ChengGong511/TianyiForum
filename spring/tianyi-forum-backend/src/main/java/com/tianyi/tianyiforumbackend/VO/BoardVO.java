package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardVO {
	private Long id;
	private String name;
	private String description;
	private String moderator;
	private Long moderatorId;
	private Integer postCount;
	private Integer sort;
	private LocalDateTime createdAt;
}
