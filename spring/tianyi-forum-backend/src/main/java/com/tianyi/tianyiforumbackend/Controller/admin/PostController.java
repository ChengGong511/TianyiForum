package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.DTO.AuditPostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.PostPageQuery;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.PostService;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.PostVo;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/posts")
@Slf4j
public class PostController {
	@Autowired
	private PostService postService;

	@PostMapping()
	public Result<PageResponse<PostVo>> listPosts(@RequestBody(required = false) PostPageQuery postPageQuery) {
		PostPageQuery safeQuery = postPageQuery == null ? new PostPageQuery() : postPageQuery;
		log.info("管理员查询帖子列表: {}", safeQuery);
		return Result.success(postService.findAll(safeQuery));
	}

	@GetMapping("/{postId}")
	public Result<PostVo> getDetail(@PathVariable Long postId) {
		log.info("管理员查看帖子详情: {}", postId);
		return Result.success(postService.findDetail(postId));
	}

	@PostMapping("/{postId}/review")
	public Result<SuccessResponse> review(@PathVariable Long postId,
			@RequestBody AuditPostRequestDTO auditPostRequestDTO) {
		log.info("管理员审核帖子: {}, payload: {}", postId, auditPostRequestDTO);
		postService.reviewPost(postId, auditPostRequestDTO);
		return Result.success();
	}


}
