package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVo {
	private Long id;
	private String title;
	private String content;
	private String author; // username
	private Long authorId;
	private Long boardId;
	private String status; // normal/pending/archived
	private Boolean isTop;
	private Boolean isFeatured;
	private Integer hotScore;
	private Integer violationScore;
	private Integer viewCount;
	private Integer commentCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
