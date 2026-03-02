package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.Eneity.Board;
import com.tianyi.tianyiforumbackend.VO.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.swing.border.Border;
import java.util.List;

@Mapper
public interface PortalBoardsMapper{
    List<BoardVO> findAll();
}
