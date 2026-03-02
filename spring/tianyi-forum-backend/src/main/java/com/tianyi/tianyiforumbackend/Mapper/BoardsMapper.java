package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.BoardCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.BoardUpdateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UpdateModeratorDTO;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardsMapper {

    List<BoardVO> findAll();
    int insert(BoardCreateRequestDTO boardCreateRequestDTO);
    int update(Integer boardId, BoardUpdateRequestDTO boardUpdateRequestDTO);
    int delete(Integer boardId);
    Long getModeratorIdByBoardId(Integer boardId);
    Integer count(Integer boardId);
    int updateModeratorId(UpdateModeratorDTO updateModeratorDTO);
}
