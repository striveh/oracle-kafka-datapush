package com.striveh.pushdata.service;

import com.striveh.pushdata.dto.ConfigDTO;
import com.striveh.pushdata.entity.KafkaConnectTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author striveh
 * @since 2021-05-11
 */
public interface IKafkaConnectTaskService extends IService<KafkaConnectTask> {

    KafkaConnectTask create(ConfigDTO configDTO);

    KafkaConnectTask update(String name,ConfigDTO configDTO);

    void updateInitial(List<String> nameList, LocalDateTime dateTime);

    void pause(List<String> nameList);

    void resume(List<String> nameList);

    void delete(List<String> nameList);

}
