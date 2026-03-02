package com.tianyi.tianyiforumbackend.Service;

import com.tianyi.tianyiforumbackend.DTO.LoginRequestDTO;
import com.tianyi.tianyiforumbackend.VO.LoginResponseVO;
import org.springframework.beans.factory.annotation.Autowired;

public interface AdminSerivce {

	LoginResponseVO login(LoginRequestDTO loginRequestDTO);
}
