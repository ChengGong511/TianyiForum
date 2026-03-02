package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  PortalComplaintDTO {
    private String targetType;
    private Long targetId;
    private String reason;
    private String evidence;
    private Long reportId;

}
