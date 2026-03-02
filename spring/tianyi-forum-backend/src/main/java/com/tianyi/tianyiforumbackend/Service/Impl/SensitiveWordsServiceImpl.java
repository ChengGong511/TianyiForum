package com.tianyi.tianyiforumbackend.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.tianyi.tianyiforumbackend.DTO.SensitiveWordCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.SensitiveWordPageQuery;
import com.tianyi.tianyiforumbackend.Mapper.SensitiveWordsMapper;
import com.tianyi.tianyiforumbackend.Service.SensitiveWordsService;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SensitiveWordVO;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SensitiveWordsServiceImpl implements SensitiveWordsService {
    @Autowired
    private SensitiveWordsMapper sensitiveWordsMapper;

    @Override
    public PageResponse<SensitiveWordVO> findALl(SensitiveWordPageQuery sensitiveWordPageQuery){
        PageHelper.startPage(sensitiveWordPageQuery.getPage(),sensitiveWordPageQuery.getPageSize());
        List<SensitiveWordVO> sensitiveWordVOList=sensitiveWordsMapper.findAll(sensitiveWordPageQuery);
        PageResponse<SensitiveWordVO> pageResponse=new PageResponse<>();
        pageResponse.setItems(sensitiveWordVOList);
        long t=sensitiveWordsMapper.findCount();
        pageResponse.setTotal(t);
        return pageResponse;

    }

    @Override
    public SuccessResponse insert(SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO){
    sensitiveWordsMapper.insertSensitiveWords(sensitiveWordCreateRequestDTO);
    return null;
    }

    @Override
    public SuccessResponse delete(int wordId){
        sensitiveWordsMapper.delete(wordId);
        return null;
    }

    @Override
    public SuccessResponse update(int wordId,SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO){

        sensitiveWordsMapper.update(wordId,sensitiveWordCreateRequestDTO.getWord(),sensitiveWordCreateRequestDTO.getLevel());
        return null;
    }


}
