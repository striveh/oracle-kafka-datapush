package com.striveh.pushdata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

public interface ICheckTableExistService extends IService<String> {
    Integer checkTableExistWithUserTables(String tableName);

}
