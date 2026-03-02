package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequestDTO {
	private Long authorId;
	private String status;
	private String title;
	private String content;
	private Long boardId;
	private LocalDateTime createdAt;
}
