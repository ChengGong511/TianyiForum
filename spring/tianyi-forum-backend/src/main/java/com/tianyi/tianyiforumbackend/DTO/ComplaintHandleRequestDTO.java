package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintHandleRequestDTO {
	/** 处理结果：dismiss(投诉不成立)/takedown(下架)/reject_post(驳回帖子) */
	private String action;
	/** 处理说明 */
	private String handleNote;
	/** 处理人ID，可根据登录上下文传入 */
	private Long handlerId;
}
