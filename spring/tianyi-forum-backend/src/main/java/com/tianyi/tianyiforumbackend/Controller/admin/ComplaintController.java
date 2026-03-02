package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.DTO.ComplaintHandleRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.ComplaintPageQuery;
import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.ComplaintService;
import com.tianyi.tianyiforumbackend.VO.ComplaintVO;
import com.tianyi.tianyiforumbackend.VO.PageResponse;
import com.tianyi.tianyiforumbackend.VO.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/complaints")
@Slf4j
public class ComplaintController {

	@Autowired
	private ComplaintService complaintService;

	@GetMapping
	public Result<PageResponse<ComplaintVO>> list(ComplaintPageQuery query) {
		log.info("管理员查询投诉列表: {}", query);
		return Result.success(complaintService.list(query));
	}

	@GetMapping("/{id}")
	public Result<ComplaintVO> detail(@PathVariable Long id) {
		log.info("管理员查看投诉详情: {}", id);
		return Result.success(complaintService.detail(id));
	}

	@PostMapping("/{id}/resolve")
	public Result<SuccessResponse> resolve(@PathVariable Long id, @RequestBody ComplaintHandleRequestDTO requestDTO) {
		log.info("管理员处理投诉: {}, payload: {}", id, requestDTO);
		complaintService.resolve(id, requestDTO);
		return Result.success();
	}
}
