package com.striveh.pushdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CheckTableExistMapper extends BaseMapper<String> {
    @Select("select count(*) from user_tables where table_name = #{tableName}")
    Integer checkTableExistWithUserTables(@Param("tableName")String tableName);
}
