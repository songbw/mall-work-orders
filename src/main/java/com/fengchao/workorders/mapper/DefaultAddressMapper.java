package com.fengchao.workorders.mapper;

import com.fengchao.workorders.model.DefaultAddress;
import com.fengchao.workorders.model.DefaultAddressExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "DefaultAddressMapper")
public interface DefaultAddressMapper {
    long countByExample(DefaultAddressExample example);

    int deleteByExample(DefaultAddressExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DefaultAddress record);

    int insertSelective(DefaultAddress record);

    List<DefaultAddress> selectByExample(DefaultAddressExample example);

    DefaultAddress selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DefaultAddress record, @Param("example") DefaultAddressExample example);

    int updateByExample(@Param("record") DefaultAddress record, @Param("example") DefaultAddressExample example);

    int updateByPrimaryKeySelective(DefaultAddress record);

    int updateByPrimaryKey(DefaultAddress record);
}
