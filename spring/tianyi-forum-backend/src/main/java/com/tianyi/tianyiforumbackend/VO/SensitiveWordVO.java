package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordVO {
	private Long id;
	private String word;
	private String level; // 高/中/低
	private LocalDateTime createdAt;
}
