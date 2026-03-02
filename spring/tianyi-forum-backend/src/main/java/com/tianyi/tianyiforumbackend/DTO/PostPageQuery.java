package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPageQuery {
	private Integer page;
	private Integer pageSize;
	private String keyword;
	private String status; // normal/pending/archived
	private String sortBy; // hot/violation
}
