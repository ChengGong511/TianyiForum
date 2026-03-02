package com.tianyi.tianyiforumbackend.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.tianyi.tianyiforumbackend.DTO.CommentPageQuery;
import com.tianyi.tianyiforumbackend.DTO.CreateCommentRequestDTO;
import com.tianyi.tianyiforumbackend.Mapper.CommentMapper;
import com.tianyi.tianyiforumbackend.Mapper.PostMapper;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.CommentService;
import com.tianyi.tianyiforumbackend.Service.PostService;
import com.tianyi.tianyiforumbackend.VO.CommentVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private PostMapper postMapper;

    @Override
    public PageResponse<CommentVO> findComments(CommentPageQuery commentPageQuery) {

        PageHelper.startPage(commentPageQuery.getPage(), commentPageQuery.getPageSize());
        List<CommentVO> commentVOList = commentMapper.findCommentsByPostId(commentPageQuery.getPostId());
        Long total = commentMapper.countCommentsByPostId(commentPageQuery.getPostId());

        PageResponse<CommentVO> response = new PageResponse<>();
        response.setItems(commentVOList);
        response.setTotal(total);
        return response;
    }

    @Override
    public Result createComment(CreateCommentRequestDTO createCommentRequestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Result.error("未登录");
        }
        Long userId = (Long) auth.getPrincipal();
        log.info("获取用户信息: userId={}", userId);
        commentMapper.createComment(createCommentRequestDTO);
        postMapper.addCommentCount(createCommentRequestDTO.getPostId());
        postService.calculateHeatScore(createCommentRequestDTO.getPostId());
        return  Result.success("评论创建成功");

    }
}
