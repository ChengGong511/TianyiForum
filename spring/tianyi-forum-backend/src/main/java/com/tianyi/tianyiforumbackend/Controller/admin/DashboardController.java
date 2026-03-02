package com.tianyi.tianyiforumbackend.Controller.admin;

import com.tianyi.tianyiforumbackend.Result.Result;
import com.tianyi.tianyiforumbackend.Service.DashboardService;
import com.tianyi.tianyiforumbackend.VO.DashboardStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Slf4j
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/stats")
	public Result<DashboardStatsVO> getStats() {
		log.info("获取仪表板统计数据");
		return Result.success(dashboardService.getStats());
	}
}
