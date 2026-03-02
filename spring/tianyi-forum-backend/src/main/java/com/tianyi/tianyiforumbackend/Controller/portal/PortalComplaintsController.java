package com.tianyi.tianyiforumbackend.Controller.portal;

import com.tianyi.tianyiforumbackend.DTO.CommentPageQuery;
import com.tianyi.tianyiforumbackend.DTO.CreateCommentRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.PortalComplaintDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.ComplaintService;

import com.tianyi.tianyiforumbackend.VO.CommentVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/complaints")
@Slf4j
public class PortalComplaintsController {
    @Autowired
    private ComplaintService complaintService;

    @PostMapping()
    public Result insert(@RequestBody PortalComplaintDTO portalComplaintDTO){

        complaintService.insertPortalComplaint(portalComplaintDTO);
        return Result.success();
    }


}
