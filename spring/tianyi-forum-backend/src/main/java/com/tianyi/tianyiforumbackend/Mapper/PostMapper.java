package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.CreatePostRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.PortalPostPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PostPageQuery;
import com.tianyi.tianyiforumbackend.Eneity.Post;
import com.tianyi.tianyiforumbackend.VO.PortalPostVO;
import com.tianyi.tianyiforumbackend.VO.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper {

	List<PostVo> findAll(PostPageQuery postPageQuery);

	PostVo findById(Long postId);

	int updateModeration(@Param("id") Long postId,
			@Param("status") String status,
			@Param("isTop") Boolean isTop,
			@Param("isFeatured") Boolean isFeatured);

	Long count(String keyword, Long boardId, String status);

	void createPost(CreatePostRequestDTO createPostRequestDTO);

	List<PortalPostVO> portalFindAll(PortalPostPageQuery query);

	@Select("select * from posts where id = #{postId}")
	Post findPostById(Long postId);

	@Update("update posts set hot_score = #{heatScore} where id = #{postId}")
	void updateHeatScore(Long postId, int heatScore);

	@Update("update posts set view_count = view_count + 1 where id = #{postId}")
	void addViewCount(Long postId);

	@Update("update posts set comment_count = comment_count + 1 where id = #{postId}")
	void addCommentCount(Long postId);

	@Select("SELECT COALESCE(SUM(view_count), 0) FROM posts")
	Long getTotalViews();

	@Select("SELECT COUNT(*) FROM posts WHERE DATE(created_at) = CURDATE()")
	Long getTodayPostsCount();
}
