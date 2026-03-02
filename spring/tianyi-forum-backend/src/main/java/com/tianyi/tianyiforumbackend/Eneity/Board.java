package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// 版块实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private Long id;
    private String name;
    private String description;
    private Long moderatorId;
    private Integer postCount;
    private Integer sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}