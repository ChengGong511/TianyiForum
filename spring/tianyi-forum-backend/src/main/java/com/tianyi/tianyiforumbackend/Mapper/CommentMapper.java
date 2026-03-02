package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.CreateCommentRequestDTO;
import com.tianyi.tianyiforumbackend.VO.CommentVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("select * from comments where post_id = #{postId}")
    List<CommentVO> findCommentsByPostId(Long postId);

    @Select("select count(*) from comments where post_id = #{postId}")
    Long countCommentsByPostId(Long postId);

    @Insert("INSERT INTO comments (post_id, author_id, content, parent_id) SELECT #{postId},p.author_id,#{content},#{parentId} FROM posts p  WHERE p.id = #{postId};")
    void createComment(CreateCommentRequestDTO createCommentRequestDTO);
}
