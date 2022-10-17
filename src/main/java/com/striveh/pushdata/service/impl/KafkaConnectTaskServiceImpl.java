package com.striveh.pushdata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.striveh.pushdata.dto.ConfigDTO;
import com.striveh.pushdata.entity.KafkaConnectTask;
import com.striveh.pushdata.mapper.KafkaConnectTaskMapper;
import com.striveh.pushdata.service.IKafkaConnectTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author striveh
 * @since 2021-05-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KafkaConnectTaskServiceImpl extends ServiceImpl<KafkaConnectTaskMapper, KafkaConnectTask> implements IKafkaConnectTaskService {
    @Value("${kafka.connect.url}")
    private String kafkaConnectUrl;

    @Value("${spring.datasource.dynamic.datasource.biz.url}")
    private String kafkaConnectorConnectionUrl;
    @Value("${spring.datasource.dynamic.datasource.biz.username}")
    private String kafkaConnectorConnectionUser;
    @Value("${spring.datasource.dynamic.datasource.biz.password}")
    private String kafkaConnectorConnectionPassword;

    private final RestTemplate restTemplate;

    @Override
    public KafkaConnectTask create(ConfigDTO configDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map connector = new HashMap<String,Object>();
        connector.put("name",configDTO.getTopicPrefix()+configDTO.getTableName());

        Map config = new HashMap<String,String>();
        config.put("connector.class","io.confluent.connect.jdbc.JdbcSourceConnector");
        config.put("connection.url",kafkaConnectorConnectionUrl);
        config.put("connection.user",kafkaConnectorConnectionUser);
        config.put("connection.password",kafkaConnectorConnectionPassword);
        config.put("topic.prefix",configDTO.getTopicPrefix());
        config.put("table.whitelist",configDTO.getTableName());
        config.put("timestamp.initial",configDTO.getTimestampInitial().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        config.put("poll.interval.ms",configDTO.getPollIntervalMs()*1000);
        config.put("mode","timestamp");
        config.put("timestamp.column.name","SYS_UPDATE_TIME");
        config.put("db.timezone","Asia/Shanghai");
        config.put("validate.non.null","false");
        config.put("numeric.mapping","best_fit");
//        config.put("value.converter","org.apache.kafka.connect.storage.StringConverter");

        connector.put("config",config);


        HttpEntity httpEntity = new HttpEntity(connector,httpHeaders);
        String result = restTemplate.postForObject(kafkaConnectUrl,httpEntity,String.class);
        log.info("创建任务=======>{}",result);

        KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
        kafkaConnectTask.setCreateTime(LocalDateTime.now());
        kafkaConnectTask.setUpdateTime(LocalDateTime.now());
        kafkaConnectTask.setCreaterName(configDTO.getCreaterName());
        kafkaConnectTask.setName(configDTO.getTopicPrefix()+configDTO.getTableName());
        kafkaConnectTask.setTopic(configDTO.getTopicPrefix()+configDTO.getTableName());
        kafkaConnectTask.setTableName(configDTO.getTableName());
        kafkaConnectTask.setTopicPrefix(configDTO.getTopicPrefix());
        kafkaConnectTask.setPollIntervalMs(configDTO.getPollIntervalMs());
        kafkaConnectTask.setTimestampInitial(configDTO.getTimestampInitial());
        kafkaConnectTask.setSysIsvalid("1");
        kafkaConnectTask.setStatus("RUNNING");
        saveOrUpdate(kafkaConnectTask);
        return kafkaConnectTask;
    }

    @Override
    public KafkaConnectTask update(String name, ConfigDTO configDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map config = new HashMap<String,String>();
        config.put("connector.class","io.confluent.connect.jdbc.JdbcSourceConnector");
        config.put("connection.url",kafkaConnectorConnectionUrl);
        config.put("connection.user",kafkaConnectorConnectionUser);
        config.put("connection.password",kafkaConnectorConnectionPassword);
        config.put("topic.prefix",configDTO.getTopicPrefix());
        config.put("table.whitelist",configDTO.getTableName());
        config.put("timestamp.initial",configDTO.getTimestampInitial().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        config.put("poll.interval.ms",configDTO.getPollIntervalMs()*1000);
        config.put("mode","timestamp");
        config.put("timestamp.column.name","SYS_UPDATE_TIME");
        config.put("db.timezone","Asia/Shanghai");
        config.put("validate.non.null","false");
        config.put("numeric.mapping","best_fit");
        config.put("value.converter.schemas.enable","false");
//        config.put("value.converter","org.apache.kafka.connect.storage.StringConverter");


        HttpEntity httpEntity = new HttpEntity(config,httpHeaders);
        ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/config", HttpMethod.PUT,httpEntity,String.class);
        log.info("修改任务=======>{}",resultEntity.getBody());


        KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
        kafkaConnectTask.setName(name);
        QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
        queryWrapper.ne("SYS_ISVALID","0");
        kafkaConnectTask = this.getOne(queryWrapper);
        kafkaConnectTask.setUpdateTime(LocalDateTime.now());
        kafkaConnectTask.setPollIntervalMs(configDTO.getPollIntervalMs());
        kafkaConnectTask.setTimestampInitial(configDTO.getTimestampInitial());
        saveOrUpdate(kafkaConnectTask);

        return kafkaConnectTask;
    }

    @Override
    public void updateInitial(List<String> nameList, LocalDateTime dateTime) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        nameList.forEach(name->{
            KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
            kafkaConnectTask.setName(name);
            QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
            queryWrapper.ne("SYS_ISVALID","0");
            kafkaConnectTask = getOne(queryWrapper);
            kafkaConnectTask.setUpdateTime(LocalDateTime.now());
            kafkaConnectTask.setTimestampInitial(dateTime);
            saveOrUpdate(kafkaConnectTask);

            Map config = new HashMap<String,String>();
            config.put("connector.class","io.confluent.connect.jdbc.JdbcSourceConnector");
            config.put("connection.url",kafkaConnectorConnectionUrl);
            config.put("connection.user",kafkaConnectorConnectionUser);
            config.put("connection.password",kafkaConnectorConnectionPassword);
            config.put("topic.prefix",kafkaConnectTask.getTopicPrefix());
            config.put("table.whitelist",kafkaConnectTask.getTableName());
            config.put("timestamp.initial",kafkaConnectTask.getTimestampInitial().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            config.put("poll.interval.ms",kafkaConnectTask.getPollIntervalMs()*1000);
            config.put("mode","timestamp");
            config.put("timestamp.column.name","SYS_UPDATE_TIME");
            config.put("db.timezone","Asia/Shanghai");
            config.put("validate.non.null","false");
            config.put("numeric.mapping","best_fit");
            config.put("value.converter.schemas.enable","false");
//        config.put("value.converter","org.apache.kafka.connect.storage.StringConverter");

            HttpEntity httpEntity = new HttpEntity(config,httpHeaders);
            ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/config", HttpMethod.PUT,httpEntity,String.class);
            log.info("修改同步初始时间=======>{}",resultEntity.getBody());
        });
    }

    @Override
    public void pause(List<String> nameList) {
        nameList.forEach(name->{
            ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/pause", HttpMethod.PUT,null,String.class);
            log.info("暂停任务=======>{}",resultEntity.getStatusCode());

            KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
            kafkaConnectTask.setName(name);
            QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
            queryWrapper.ne("SYS_ISVALID","0");

            kafkaConnectTask = getOne(queryWrapper);
            kafkaConnectTask.setUpdateTime(LocalDateTime.now());
            kafkaConnectTask.setStatus("PAUSED");
            saveOrUpdate(kafkaConnectTask);
        });
    }

    @Override
    public void resume(List<String> nameList) {
        nameList.forEach(name->{
            ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/resume", HttpMethod.PUT,null,String.class);
            log.info("恢复任务=======>{}",resultEntity.getStatusCode());

            KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
            kafkaConnectTask.setName(name);
            QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
            queryWrapper.ne("SYS_ISVALID","0");

            kafkaConnectTask = getOne(queryWrapper);
            kafkaConnectTask.setUpdateTime(LocalDateTime.now());
            kafkaConnectTask.setStatus("RUNNING");
            saveOrUpdate(kafkaConnectTask);
        });
    }

    @Override
    public void delete(List<String> nameList) {
        nameList.forEach(name->{
            ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name,HttpMethod.DELETE,null,String.class);
            log.info("删除任务=======>{}",resultEntity.getStatusCode());

            KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
            kafkaConnectTask.setName(name);
            QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
            queryWrapper.ne("SYS_ISVALID","0");

            kafkaConnectTask = getOne(queryWrapper);
            kafkaConnectTask.setUpdateTime(LocalDateTime.now());
            kafkaConnectTask.setSysIsvalid("0");
            saveOrUpdate(kafkaConnectTask);
        });
    }
}
