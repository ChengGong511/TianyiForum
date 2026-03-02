package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.AuditPostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.CreatePostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.PortalPostPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PostPageQuery;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.PortalPostVO;
import com.tianyi.tianyiforumbackend.VO.PostVo;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;

import java.util.List;

public interface PostService {
	PageResponse<PostVo> findAll(PostPageQuery postPageQuery);

	PostVo findDetail(Long postId);

	void reviewPost(Long postId, AuditPostRequestDTO auditPostRequestDTO);

	Result createPost(CreatePostRequestDTO createPostRequestDTO);

	PageResponse<PortalPostVO> portalPostFindAll(PortalPostPageQuery query);

	public void calculateHeatScore(Long postId);
}
