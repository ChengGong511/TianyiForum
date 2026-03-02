package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.BoardCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.BoardUpdateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UpdateModeratorDTO;
import com.tianyi.tianyiforumbackend.VO.BoardVO;

import java.util.List;

public interface BoardsService {
    List<BoardVO> findAll();
    void insertBoard(BoardCreateRequestDTO boardCreateRequestDTO);
    void updateBoard(Integer boardId, BoardUpdateRequestDTO boardUpdateRequestDTO);
    void deleteBoard(Integer boardId);
    Integer countPost(Integer boardId);
    void updateModerator(Integer boardId, UpdateModeratorDTO updateModeratorDTO);
}
