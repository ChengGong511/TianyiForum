package com.tianyi.tianyiforumbackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalPostPageQuery {

    private Integer page;
    private Integer pageSize;
    private String keyword;
    private String status; // normal/pending/archived
    private String sortBy; // hot/violatio

    private Long boardId;
}
