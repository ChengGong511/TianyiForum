package com.tianyi.tianyiforumbackend.Service.Impl;

import com.tianyi.tianyiforumbackend.DTO.BoardCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.BoardUpdateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UpdateModeratorDTO;
import com.tianyi.tianyiforumbackend.Mapper.BoardsMapper;
import com.tianyi.tianyiforumbackend.Service.BoardsService;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BoardsServiceImpI implements BoardsService {
    @Autowired
    private BoardsMapper boardsMapper;
    @Override
    public List<BoardVO> findAll(){
        List<BoardVO> BoardList = boardsMapper.findAll();

        return BoardList;
    }
    @Override
    public void insertBoard(BoardCreateRequestDTO boardCreateRequestDTO){
        boardsMapper.insert(boardCreateRequestDTO);
        log.info("增加的版块:{}",boardCreateRequestDTO);
    }

    @Override
    public void updateBoard(Integer boardId, BoardUpdateRequestDTO boardUpdateRequestDTO){
        log.info("更新的版块Id:{} 更新的内容:{}",boardId,boardUpdateRequestDTO);
        boardUpdateRequestDTO.setModeratorId(boardsMapper.getModeratorIdByBoardId(boardId));
        boardsMapper.update(boardId,boardUpdateRequestDTO);

    }

    @Override
    public void deleteBoard(Integer boardId){
        boardsMapper.delete(boardId);
        log.info("删除的版块id:{}",boardId);
    }

    @Override
    public Integer countPost(Integer boardId){
        Integer count = boardsMapper.count(boardId);
        log.info("查询该板块下的帖子数量的版块id:{}",boardId);
        return count;

    }

    @Override
    public void updateModerator(Integer boardId, UpdateModeratorDTO updateModeratorDTO){

        updateModeratorDTO.setBoardId(boardId);
        boardsMapper.updateModeratorId(updateModeratorDTO);
    }
}
