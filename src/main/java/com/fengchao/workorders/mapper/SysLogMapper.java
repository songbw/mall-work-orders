package com.fengchao.workorders.mapper;;

import com.fengchao.workorders.model.SysLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component(value = "SysLogMapper")
public interface SysLogMapper {

    Long insert(SysLog SysLog);

    void deleteById(@Param("id") Long id);

    void deleteByIdList(@Param("ids") List<Long> ids);

    SysLog select(SysLog SysLog);

    //通过id进行查询
    SysLog selectById(@Param("id") Long id);

    //查询全部
    List<SysLog> selectAll();

    //查询数量
    int selectCounts();

    List<SysLog> selectLog(@Param("sort") String sort, @Param("order") String order, @Param("method") String method, @Param("url") String url, @Param("param") String param, @Param("createTimeStart") Date createTimeStart, @Param("createTimeEnd") Date createTimeEnd, @Param("user") String user);

}
