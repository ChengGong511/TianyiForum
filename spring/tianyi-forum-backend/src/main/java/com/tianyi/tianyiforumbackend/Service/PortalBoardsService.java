package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.VO.BoardVO;

import java.util.List;

public interface PortalBoardsService{
    List<BoardVO> findAll();
}
