package com.tianyi.tianyiforumbackend.Controller.portal;

import com.tianyi.tianyiforumbackend.DTO.CommentPageQuery;
import com.tianyi.tianyiforumbackend.DTO.CreateCommentRequestDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.CommentService;
import com.tianyi.tianyiforumbackend.VO.CommentVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/posts")
@Slf4j
public class PortalCommentController {
    @Autowired
    private CommentService commentService;


    @PostMapping("/{id}/comments/query")
    public Result<PageResponse<CommentVO>> listComments(@PathVariable Integer id, @RequestBody CommentPageQuery commentPageQuery){
        log.info("用户查询评论列表id: {} 页码和页面数量:{}", id,commentPageQuery);
        PageResponse response=commentService.findComments(commentPageQuery);
        return Result.success(response);
    }


    /*
     *发表评论
     */
    @PostMapping("/{id}/comments")
    public Result<SuccessResponse> creatComments(@PathVariable Integer id, @RequestBody CreateCommentRequestDTO createCommentRequestDTO){
        createCommentRequestDTO.setPostId(id.longValue());
        log.info("用户发布评论: {}",createCommentRequestDTO);
        commentService.createComment(createCommentRequestDTO);
        return Result.success();
    }
}
