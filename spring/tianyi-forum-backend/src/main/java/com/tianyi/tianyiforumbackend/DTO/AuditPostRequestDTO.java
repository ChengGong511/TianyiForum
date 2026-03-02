package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditPostRequestDTO {
	/** 审核/处置动作：approve/top/featured/reject/ban */
	private String action;
	/** 处置原因，驳回/下架时建议填写 */
	private String reason;
}
