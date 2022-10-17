package com.striveh.pushdata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.striveh.pushdata.dto.ConfigDTO;
import com.striveh.pushdata.entity.KafkaConnectTask;
import com.striveh.pushdata.service.ICheckTableExistService;
import com.striveh.pushdata.service.IKafkaConnectTaskService;
import com.striveh.pushdata.vo.PageResult;
import com.striveh.pushdata.vo.Result;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 程序调用kafka connect 服务 http://kafka.apache.org/documentation/#connect
 * 通过 kafka-connect-jdbc 完成数据同步 https://docs.confluent.io/kafka-connect-jdbc/current/source-connector/index.html
 *
 *
 * 该controller 通过调用 kafka connect rest接口，
 * 完成同步任务的配置管理；封装之后暴露必要参数由界面使用者填写，
 * 一些定制型，固定性参数则在程序里完成指定。
 */
@Api(tags = {"同步数据到kafka"})
@RestController
@RequestMapping({"/connectors"})
@RequiredArgsConstructor
@Slf4j
public class KafkaConnectRestController {

    @Value("${kafka.connect.url}")
    private String kafkaConnectUrl;

    @Value("${spring.datasource.dynamic.datasource.biz.url}")
    private String kafkaConnectorConnectionUrl;
    @Value("${spring.datasource.dynamic.datasource.biz.username}")
    private String kafkaConnectorConnectionUser;
    @Value("${spring.datasource.dynamic.datasource.biz.password}")
    private String kafkaConnectorConnectionPassword;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final IKafkaConnectTaskService kafkaConnectTaskService;
    private final ICheckTableExistService checkTableExistService;

    @GetMapping
    @ApiOperation("获取任务列表")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "name",
            value = "任务名"
    ), @ApiImplicitParam(
            name = "createrName",
            value = "创建者"
    ),@ApiImplicitParam(
            name = "current",
            value = "第几页"
    ), @ApiImplicitParam(
            name = "size",
            value = "页大小"
    )})
    public Result<PageResult<KafkaConnectTask>> getList(@RequestParam(required = false) String name, @RequestParam(required = false) String createrName,
                          @RequestParam(defaultValue = "1") Long current, @RequestParam(defaultValue = "10") Long size){

        Page<KafkaConnectTask> page = new Page<>(current,size);

        KafkaConnectTask kafkaConnectTask = new KafkaConnectTask();
        kafkaConnectTask.setName(name);
        kafkaConnectTask.setCreaterName(createrName);
        QueryWrapper<KafkaConnectTask> queryWrapper = new QueryWrapper(kafkaConnectTask);
        queryWrapper.ne("SYS_ISVALID","0");
        page = this.kafkaConnectTaskService.page(page,queryWrapper);

        page.getRecords().forEach(e->{
            String status = restTemplate.getForObject(kafkaConnectUrl+"/"+e.getName()+"/status",String.class);
            log.info("任务={}，status={}",e.getName(),status);
            try {
                e.setStatus(objectMapper.readTree(status).get("connector").get("state").asText());
                this.kafkaConnectTaskService.saveOrUpdate(e);
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        });
        return Result.ok(new PageResult(page.getRecords(),page.getTotal()));
    }

    @GetMapping({"/{name}/status"})
    @ApiOperation("任务状态")
    public Result getStatus(@PathVariable String name){
        String status = restTemplate.getForObject(kafkaConnectUrl+"/"+name+"/status",String.class);
        log.info("任务={}，status={}",name,status);
        return Result.ok(status);
    }

    @PostMapping
    @ApiOperation("创建任务")
    public Result<KafkaConnectTask> create(@RequestBody @Valid ConfigDTO configDTO){
        if (checkTableExistService.checkTableExistWithUserTables(configDTO.getTableName()).equals(0)){
            return Result.error("表不存在呀");
        }
        return Result.ok(this.kafkaConnectTaskService.create(configDTO));
    }

    @PutMapping({"/{name}"})
    @ApiOperation("修改任务")
    public Result<KafkaConnectTask> update(@PathVariable String name, @RequestBody @Valid ConfigDTO configDTO){

        return Result.ok(this.kafkaConnectTaskService.update(name,configDTO));

    }

    @PutMapping({"/updateInitial"})
    @ApiOperation("修改同步初始时间")
    public Result updateInitial(@RequestBody List<String> nameList, @RequestParam LocalDateTime dateTime){

        this.kafkaConnectTaskService.updateInitial(nameList,dateTime);

        return Result.ok();

    }

    @PutMapping({"/pause"})
    @ApiOperation("暂停任务")
    public Result pause(@RequestBody List<String> nameList){

        this.kafkaConnectTaskService.pause(nameList);
        return Result.ok();
    }

    @PutMapping({"/resume"})
    @ApiOperation("恢复任务")
    public Result resume(@RequestBody List<String> nameList){
        this.kafkaConnectTaskService.resume(nameList);
        return Result.ok();
    }

    @DeleteMapping({"/delete"})
    @ApiOperation("删除任务")
    public Result delete(@RequestBody List<String> nameList){

        this.kafkaConnectTaskService.delete(nameList);
        return Result.ok();
    }

    @PostMapping({"/restart"})
    @ApiOperation("重启任务")
    public Result restart(@RequestBody List<String> nameList){
        nameList.forEach(name->{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity httpEntity = new HttpEntity(null,httpHeaders);

            ResponseEntity<String> resultEntity = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/restart", HttpMethod.POST,httpEntity,String.class);
            log.info("重启任务=======>{}",resultEntity.getStatusCode());
            ResponseEntity<String> resultEntity2 = restTemplate.exchange(kafkaConnectUrl+"/"+name+"/tasks/0/restart", HttpMethod.POST,httpEntity,String.class);
            log.info("重启任务=======>{}",resultEntity2.getStatusCode());
        });
        return Result.ok();
    }
}
