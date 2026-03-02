package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.SensitiveWordCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.SensitiveWordPageQuery;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SensitiveWordVO;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;

import java.util.List;

public interface SensitiveWordsService {
    PageResponse<SensitiveWordVO> findALl(SensitiveWordPageQuery sensitiveWordPageQuery);
    SuccessResponse insert(SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO);
    SuccessResponse delete(int wordId);
    SuccessResponse update(int wordId,SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO);
}
