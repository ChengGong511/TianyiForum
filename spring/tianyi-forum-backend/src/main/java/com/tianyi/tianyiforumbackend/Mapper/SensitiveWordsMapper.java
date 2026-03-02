package com.tianyi.tianyiforumbackend.Mapper;

import com.tianyi.tianyiforumbackend.DTO.SensitiveWordCreateRequestDTO;
import com.tianyi.tianyiforumbackend.DTO.SensitiveWordPageQuery;
import com.tianyi.tianyiforumbackend.Eneity.SensitiveWord;
import com.tianyi.tianyiforumbackend.VO.SensitiveWordVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SensitiveWordsMapper {
    List<SensitiveWordVO> findAll(SensitiveWordPageQuery sensitiveWordPageQuery);
    @Select("SELECT COUNT(*) AS total_sensitive_words FROM sensitive_words")
    long findCount();

    @Insert("insert into sensitive_words (word, level) values (#{word},#{level})")
    int insertSensitiveWords(SensitiveWordCreateRequestDTO sensitiveWordCreateRequestDTO);

    @Delete("delete from sensitive_words where id=#{wordId}")
    int delete(int wordId);
    
    @Update("update sensitive_words set word = #{word},level=#{level} where id=#{wordId} ;")
    int update(int wordId,String word,String level);
}
