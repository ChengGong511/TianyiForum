package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// 敏感词实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWord {
    private Long id;
    private String word;
    private String level;       // 高/中/低
    private LocalDateTime createdAt;
}