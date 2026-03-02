package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQuery {
	private Integer page=1;
	private Integer pageSize=10;
	private String keyword;
	private String status; // active/banned
}
