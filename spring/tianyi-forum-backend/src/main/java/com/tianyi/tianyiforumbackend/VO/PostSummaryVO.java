package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryVO {
	private Long id;
	private String title;
	private Long boardId;
	private String boardName;
	private String author;
	private Long authorId;
	private Integer hotScore;
	private Boolean isTop;
	private Boolean isFeatured;
	private LocalDateTime createdAt;
}
