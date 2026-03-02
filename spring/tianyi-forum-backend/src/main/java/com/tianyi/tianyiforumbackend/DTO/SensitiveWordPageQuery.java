package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordPageQuery {
	private Integer page;
	private Integer pageSize;
	private String keyword;
}
