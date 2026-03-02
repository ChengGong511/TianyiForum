package com.tianyi.tianyiforumbackend.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO {
	private Long totalUsers;
	private Long totalPosts;
	private Long todayPosts;
	private Long pendingComplaints;
	private Long totalViews;
}
