package com.tianyi.tianyiforumbackend.Service.Impl;

import com.tianyi.tianyiforumbackend.Mapper.PortalBoardsMapper;
import com.tianyi.tianyiforumbackend.Service.PortalBoardsService;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortalBoardsServiceImpl implements PortalBoardsService {
    @Autowired
    private PortalBoardsMapper portalBoardsMapper;

    @Override
    public List<BoardVO> findAll(){
        List<BoardVO> Boardslist= portalBoardsMapper.findAll();
        return Boardslist;
    }
}
