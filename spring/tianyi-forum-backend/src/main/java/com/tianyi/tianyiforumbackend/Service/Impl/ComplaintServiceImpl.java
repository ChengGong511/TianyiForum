package com.tianyi.tianyiforumbackend.Service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tianyi.tianyiforumbackend.DTO.ComplaintHandleRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.ComplaintPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PortalComplaintDTO;
import com.tianyi.tianyiforumbackend.Mapper.ComplaintMapper;
import com.tianyi.tianyiforumbackend.Mapper.PostMapper;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.ComplaintService;
import com.tianyi.tianyiforumbackend.VO.ComplaintVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComplaintServiceImpl implements ComplaintService {

	@Autowired
	private ComplaintMapper complaintMapper;

	@Autowired
	private PostMapper postMapper;

	@Override
	public PageResponse<ComplaintVO> list(ComplaintPageQuery query) {
		int pageNo = query.getPage() == null ? 1 : query.getPage();
		int pageSize = query.getPageSize() == null ? 10 : query.getPageSize();
		Page<ComplaintVO> page = PageHelper.startPage(pageNo, pageSize)
				.doSelectPage(() -> complaintMapper.findAll(query));
		PageResponse<ComplaintVO> response = new PageResponse<>();
		response.setItems(page.getResult());
		response.setTotal(page.getTotal());
		return response;
	}

	@Override
	public ComplaintVO detail(Long id) {
		return complaintMapper.findById(id);
	}

	@Override
	public void resolve(Long id, ComplaintHandleRequestDTO requestDTO) {
		ComplaintVO complaint = complaintMapper.findById(id);
		if (complaint == null) {
			throw new IllegalArgumentException("投诉不存在");
		}
		if (requestDTO.getAction() == null) {
			throw new IllegalArgumentException("处理动作不能为空");
		}

		String action = requestDTO.getAction().toLowerCase();
		switch (action) {
			case "dismiss":
				break;
			case "takedown":
			case "reject_post":
				if ("post".equalsIgnoreCase(complaint.getTargetType())) {
					postMapper.updateModeration(complaint.getTargetId(), "archived", null, null);
				}
				break;
			default:
				throw new IllegalArgumentException("不支持的投诉处理动作: " + requestDTO.getAction());
		}

		int updated = complaintMapper.resolveComplaint(id, requestDTO.getHandleNote(), requestDTO.getHandlerId());
		if (updated == 0) {
			throw new IllegalStateException("投诉处理失败");
		}
		log.info("处理投诉{} 动作={} note={}", id, action, requestDTO.getHandleNote());
	}

	@Override
	public Result insertPortalComplaint(PortalComplaintDTO portalComplaintDTO){
		ComplaintVO complaintVO=new ComplaintVO();
		complaintVO.setReason(portalComplaintDTO.getReason());
		complaintVO.setTargetType(portalComplaintDTO.getTargetType());
		complaintVO.setTargetId(portalComplaintDTO.getTargetId());
		complaintVO.setEvidence(portalComplaintDTO.getEvidence());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null) {
			return Result.error("未登录");
		}

		Long userId = (Long) auth.getPrincipal();
		complaintVO.setReporterId(userId);
		log.info("获取用户信息: userId={}", userId);
		complaintMapper.insertPortalComplaint(complaintVO);
		return null;
	}
}
