package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequestDTO {
	private String name;
	private String description;
	private Long moderatorId;
	private Integer sort;
}
