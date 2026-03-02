package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.DTO.BoardCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.BoardUpdateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.UpdateModeratorDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.BoardsService;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/boards")
@Slf4j
public class BoardsController {
    @Autowired
    private BoardsService boardsService;

    @GetMapping
    public Result<List<BoardVO>> listBoard(){
        log.info("管理员查询板块列表: {}");
        return Result.success(boardsService.findAll());
    }

    @PostMapping()
    public Result<SuccessResponse> insertBoard(@RequestBody BoardCreateRequestDTO boardCreateRequestDTO){
        log.info("管理员新增板块: {}",boardCreateRequestDTO );
        boardsService.insertBoard(boardCreateRequestDTO);
        return  Result.success();
    }

    @PutMapping("/{boardId}")
    public Result<SuccessResponse> updateBoard(@PathVariable Integer boardId, @RequestBody BoardUpdateRequestDTO boardUpdateRequestDTO){
        log.info("管理员更新板块id: {}  内容:{}",boardId,boardUpdateRequestDTO);
        boardsService.updateBoard(boardId,boardUpdateRequestDTO);
        return Result.success();
    }

    @DeleteMapping("/{boardId}")
    public Result<SuccessResponse> deleteBoard(@PathVariable Integer boardId){
        log.info("管理员删除板块id: {}",boardId );
        boardsService.deleteBoard(boardId);
        return Result.success();
    }

    @GetMapping("/{boardId}")
    public Result<Integer> countPost(@PathVariable Integer boardId){
        log.info("管理员查询该板块下的帖子数量的板块id: {}",boardId );
        return Result.success(boardsService.countPost(boardId));
    }

    @PutMapping("/{boardId}/moderator")
    public Result<SuccessResponse> moderator(@PathVariable Integer boardId, @RequestBody UpdateModeratorDTO updateModeratorDTO){
        log.info("管理员委派版主的板块id: {}  委派版主id:{}",boardId,updateModeratorDTO.getModeratorId() );
        boardsService.updateModerator(boardId,updateModeratorDTO);
        return Result.success();
    }
}
