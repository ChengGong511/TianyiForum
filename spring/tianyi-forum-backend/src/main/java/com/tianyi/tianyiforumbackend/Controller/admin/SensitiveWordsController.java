package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.DTO.SensitiveWordCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.SensitiveWordPageQuery;
import com.tianyi.tianyiforumbackend.DTO.UserPageQuery;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.SensitiveWordsService;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SensitiveWordVO;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import com.tianyi.tianyiforumbackend.VO.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.spi.DirStateFactory;

@RestController
@RequestMapping("/admin/sensitive-words")
@Slf4j
public class SensitiveWordsController {
    @Autowired
    private SensitiveWordsService sensitiveWordsService;

    @GetMapping()
    public Result<PageResponse<SensitiveWordVO>> listUsers( SensitiveWordPageQuery sensitiveWordPageQuery){
        log.info("管理员查询敏感词列表: {}", sensitiveWordPageQuery);
        PageResponse<SensitiveWordVO> PagResp=sensitiveWordsService.findALl(sensitiveWordPageQuery);
        return Result.success(PagResp);
    }

    @PostMapping()
    public Result insert(@RequestBody SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO){
        sensitiveWordsService.insert(sensitiveWordCreateRequestDTO);
        return Result.success();
    }

    @DeleteMapping("/{wordId}")
    public Result delete(@PathVariable int wordId){
        sensitiveWordsService.delete(wordId);
        return Result.success();
    }

    @PutMapping("/{wordId}")
    public Result<PageResponse<SensitiveWordVO>> update(@PathVariable int wordId,@RequestBody SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO){
        sensitiveWordsService.update(wordId,sensitiveWordCreateRequestDTO);
        return Result.success();
    }

}
