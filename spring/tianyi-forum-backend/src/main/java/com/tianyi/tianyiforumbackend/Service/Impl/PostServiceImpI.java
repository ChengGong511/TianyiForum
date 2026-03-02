package com.tianyi.tianyiforumbackend.Service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tianyi.tianyiforumbackend.DTO.AuditPostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.CreatePostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.PortalPostPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PostPageQuery;
import com.tianyi.tianyiforumbackend.Eneity.Post;
import com.tianyi.tianyiforumbackend.Mapper.PostMapper;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.PostService;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.PortalPostVO;
import com.tianyi.tianyiforumbackend.VO.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PostServiceImpI implements PostService {
	@Autowired
	private PostMapper postMapper;

	@Override
	public PageResponse<PostVo> findAll(PostPageQuery postPageQuery) {
		PageHelper.startPage(postPageQuery.getPage(), postPageQuery.getPageSize());
		List<PostVo>postVos = postMapper.findAll(postPageQuery);
		PageResponse<PostVo> pageResponse = new PageResponse<>();
		pageResponse.setItems(postVos);
		pageResponse.setTotal(postMapper.count(postPageQuery.getKeyword(),null, postPageQuery.getStatus()));
		return pageResponse;
	}

	@Override
	public PostVo findDetail(Long postId) {
		postMapper.addViewCount(postId);
		this.calculateHeatScore(postId);
		return postMapper.findById(postId);
	}

	@Override
	public void reviewPost(Long postId, AuditPostRequestDTO auditPostRequestDTO) {
		PostVo post = postMapper.findById(postId);
		if (post == null) {
			throw new IllegalArgumentException("帖子不存在");
		}

		String action = auditPostRequestDTO.getAction();
		if (action == null) {
			throw new IllegalArgumentException("审核动作不能为空");
		}
		String normalized = action.toLowerCase();
		Boolean isTop = null;
		Boolean isFeatured = null;
		String status = null;
		switch (normalized) {
			case "approve":
				status = "normal";
				break;
			case "top":
				isTop = true;
				status = "normal";
				break;
			case "featured":
				isFeatured = true;
				status = "normal";
				break;
			case "reject":
			case "ban":
				status = "archived";
				break;
			default:
				throw new IllegalArgumentException("不支持的审核动作: " + action);
		}

		int updated = postMapper.updateModeration(postId, status, isTop, isFeatured);
		if (updated == 0) {
			throw new IllegalStateException("审核失败，未更新任何记录");
		}
		this.calculateHeatScore(postId);
		log.info("审核帖子{}，action={}, status={}, top={}, featured={}", postId, normalized, status, isTop, isFeatured);
	}

	@Override
	public Result createPost(CreatePostRequestDTO createPostRequestDTO) {
		// 从 SecurityContext 获取当前用户 ID
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return Result.error("未登录");
		}

		Long userId = (Long) auth.getPrincipal();
		log.info("获取用户信息: userId={}", userId);

		createPostRequestDTO.setAuthorId(userId);
		createPostRequestDTO.setStatus("pending"); // 新帖子默认状态为待审核
		createPostRequestDTO.setCreatedAt(LocalDateTime.now());

		postMapper.createPost(createPostRequestDTO);
		return Result.success("帖子创建成功，等待审核");
	}

	@Override
	public PageResponse<PortalPostVO> portalPostFindAll(PortalPostPageQuery query) {
		PageHelper.startPage(query.getPage(), query.getPageSize());
		List<PortalPostVO>postVos = postMapper.portalFindAll(query);
		PageResponse<PortalPostVO> pageResponse = new PageResponse<>();
		pageResponse.setItems(postVos);
		pageResponse.setTotal(postMapper.count(query.getKeyword(),query.getBoardId(),query.getStatus()));
		return pageResponse;
	}


	//计算热度分数
	public void calculateHeatScore(Long postId) {
		Post post = postMapper.findPostById(postId);
		if (post == null) {
			throw new IllegalArgumentException("帖子不存在");
		}

		// 示例热度计算公式
		int heatScore =post.getViewCount()*1+post.getCommentCount()*2;
		if(post.getIsFeatured()!=null&&post.getIsFeatured()) {
			heatScore += 10;
		}
		if (post.getIsTop()!=null&&post.getIsTop()) {
			heatScore += 5;
		}
		postMapper.updateHeatScore(postId, heatScore);
		log.info("更新帖子{}的热度分数为{}", postId, heatScore);
	}

}
