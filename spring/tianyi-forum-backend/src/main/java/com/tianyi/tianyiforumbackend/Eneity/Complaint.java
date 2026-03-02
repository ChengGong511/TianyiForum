package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 举报实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {
    private Long id;
    private String targetType;  // post/comment/user
    private Long targetId;
    private String reason;
    private String evidence;
    private Long reporterId;
    private String status;      // pending/resolved
    private String handleNote;
    private Long handlerId;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
}
