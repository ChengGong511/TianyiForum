package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.CommentPageQuery;
import com.tianyi.tianyiforumbackend.DTO.CreateCommentRequestDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.VO.CommentVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import org.apache.coyote.Response;

public interface CommentService {
    public PageResponse<CommentVO> findComments(CommentPageQuery commentPageQuery);
    Result createComment(CreateCommentRequestDTO createCommentRequestDTO);
}
