package com.tianyi.tianyiforumbackend.Controller.portal;

import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.BoardsService;
import com.tianyi.tianyiforumbackend.Service.PortalBoardsService;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 板块接口
 * 无需登录即可访问
 */
@RestController
@RequestMapping("/portal/boards")
@Slf4j
public class PortalBoardsController {

    @Autowired
    private PortalBoardsService portalBoardsService;

    @GetMapping()
    public Result<List<BoardVO>> findAll(){
        List<BoardVO> boardVOList= portalBoardsService.findAll();
        return Result.success(boardVOList);
    }
}
