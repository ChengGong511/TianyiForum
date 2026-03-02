package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// 管理员操作日志
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLog {
    private Long id;
    private Long adminId;
    private String action;
    private String targetType;
    private Long targetId;
    private String reason;
    private String ipAddress;
    private LocalDateTime createdAt;
}