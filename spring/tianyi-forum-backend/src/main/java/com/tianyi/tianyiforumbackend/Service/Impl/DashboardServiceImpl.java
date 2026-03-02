package com.tianyi.tianyiforumbackend.Service.Impl;

import com.tianyi.tianyiforumbackend.DTO.ComplaintPageQuery;
import com.tianyi.tianyiforumbackend.Mapper.ComplaintMapper;
import com.tianyi.tianyiforumbackend.Mapper.PostMapper;
import com.tianyi.tianyiforumbackend.Mapper.UserMapper;
import com.tianyi.tianyiforumbackend.Service.DashboardService;
import com.tianyi.tianyiforumbackend.VO.DashboardStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PostMapper postMapper;

	@Autowired
	private ComplaintMapper complaintMapper;

	@Override
	public DashboardStatsVO getStats() {
		DashboardStatsVO stats = new DashboardStatsVO();

		// 获取总用户数
		stats.setTotalUsers(userMapper.findCount());

		// 获取总帖子数
		stats.setTotalPosts(postMapper.count(null, null, null));

		// 获取今日新帖数
		stats.setTodayPosts(postMapper.getTodayPostsCount());

		// 获取待处理投诉数
		ComplaintPageQuery query = new ComplaintPageQuery();
		query.setStatus("pending");
		stats.setPendingComplaints(complaintMapper.count(query));

		// 获取总浏览量
		stats.setTotalViews(postMapper.getTotalViews());

		log.info("获取仪表板统计数据: {}", stats);
		return stats;
	}
}
