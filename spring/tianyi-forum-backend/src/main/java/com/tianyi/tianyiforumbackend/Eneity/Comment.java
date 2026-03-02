package com.tianyi.tianyiforumbackend.Eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// 评论实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private Long parentId;
    private String status;      // normal/deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
