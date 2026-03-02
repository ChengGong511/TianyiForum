package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.ComplaintHandleRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.ComplaintPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PortalComplaintDTO;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.VO.ComplaintVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;

public interface ComplaintService {
	PageResponse<ComplaintVO> list(ComplaintPageQuery query);

	ComplaintVO detail(Long id);

	void resolve(Long id, ComplaintHandleRequestDTO requestDTO);

	Result insertPortalComplaint(PortalComplaintDTO portalComplaintDTO);
}
