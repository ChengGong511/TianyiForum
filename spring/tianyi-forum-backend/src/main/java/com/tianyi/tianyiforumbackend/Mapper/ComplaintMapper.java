package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.ComplaintPageQuery;
import com.tianyi.tianyiforumbackend.DTO.PortalComplaintDTO;
import com.tianyi.tianyiforumbackend.VO.ComplaintVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ComplaintMapper {
	List<ComplaintVO> findAll(ComplaintPageQuery query);

	long count(ComplaintPageQuery query);

	ComplaintVO findById(Long id);

	int resolveComplaint(@Param("id") Long id,
			@Param("handleNote") String handleNote,
			@Param("handlerId") Long handlerId);
	int insertPortalComplaint(ComplaintVO complaintVO);
}
