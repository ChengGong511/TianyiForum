package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// 帖子实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Long boardId;
    private String status;      // normal/pending/archived
    private Boolean isTop;
    private Boolean isFeatured;
    private Integer hotScore;
    private Integer violationScore;
    private Integer viewCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}