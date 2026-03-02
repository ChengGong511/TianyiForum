package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalPostVO {
    private Long id;
    private String title;
    private String author; // username
    private Long authorId;
    private Long boardId;
    private String boardName;
    private Integer hotScore;
    private Integer isTop;
    private Integer isFeatured;
    private LocalDateTime createdAt;
}
