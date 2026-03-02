package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailVO extends PostSummaryVO {
	private String content;
	private Integer violationScore;
	private String status; // normal/pending/archived
	private LocalDateTime updatedAt;
}
