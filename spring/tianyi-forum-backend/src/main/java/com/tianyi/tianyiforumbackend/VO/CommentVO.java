package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
	private Long id;
	private Long postId;
	private Long parentId;
	private String content;
	private Long authorId;
	private String author;
	private LocalDateTime createdAt;
}
