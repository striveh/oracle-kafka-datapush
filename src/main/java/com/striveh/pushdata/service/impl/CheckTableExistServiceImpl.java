package com.striveh.pushdata.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.striveh.pushdata.mapper.CheckTableExistMapper;
import com.striveh.pushdata.service.ICheckTableExistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@DS("biz")
public class CheckTableExistServiceImpl extends ServiceImpl<CheckTableExistMapper,String> implements ICheckTableExistService {

    @Override
    public Integer checkTableExistWithUserTables(String tableName) {
        return baseMapper.checkTableExistWithUserTables(tableName);
    }
}
