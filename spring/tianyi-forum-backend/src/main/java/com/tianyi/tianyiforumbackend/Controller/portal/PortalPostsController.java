package com.tianyi.tianyiforumbackend.Controller.portal;

import com.tianyi.tianyiforumbackend.DTO.*;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.PostService;
import com.tianyi.tianyiforumbackend.VO.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 帖子接口
 * 无需登录即可访问
 */
@RestController
@RequestMapping("/portal/posts")
@Slf4j
public class PortalPostsController {

	@Autowired
	private PostService postService;

	/**
	 * 获取帖子列表（公开接口）
	 * 使用POST方法接收DTO
	 */
	@PostMapping("/query")
	public Result<PageResponse<PortalPostVO>> listPosts(@RequestBody PortalPostPageQuery query) {
		log.info("用户查询帖子列表: {}", query);
		if (query.getStatus() == null) {
			query.setStatus("normal"); // 用户只能看到正常状态的帖子
		}
		return Result.success(postService.portalPostFindAll(query));
	}

	/**
	 * 获取帖子详情（公开接口）
	 */
	@GetMapping("/{postId}")
	public Result<PostVo> getPostDetail(@PathVariable Long postId) {
		log.info("用户查询帖子详情: postId={}", postId);
		return Result.success(postService.findDetail(postId));
	}

	/*
	 * 发布帖子
	 */

	@PostMapping()
	public Result createPost(@RequestBody CreatePostRequestDTO createPostRequestDTO){
		log.info("用户发布帖子: {}", createPostRequestDTO);
		postService.createPost(createPostRequestDTO);
		return Result.success();
	}

	/*
	 * 获取评论列表
	 */


}
