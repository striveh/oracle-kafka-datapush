package com.striveh.pushdata.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author striveh
 * @since 2021-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("KAFKA_CONNECT_TASK")
@KeySequence(value = "T_KAFKA_CONNECT_TASK_SEQ",clazz = Long.class)
@ApiModel(
        value = "任务信息"
)
public class KafkaConnectTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(
            value = "任务名称",
            required = true
    )
    @TableField("NAME")
    private String name;

    @ApiModelProperty(
            value = "kafka topic前缀，与表名称一起组成topic",
            required = true
    )
    @TableField("TOPIC_PREFIX")
    private String topicPrefix;

    @ApiModelProperty(
            value = "表名称",
            required = true
    )
    @TableField("TABLE_NAME")
    private String tableName;

    @ApiModelProperty(
            value = "初始同步时间，默认全部同步",
            required = false
    )
    @TableField("TIMESTAMP_INITIAL")
    private LocalDateTime timestampInitial;

    @ApiModelProperty(
            value = "同步频率，单位秒，默认每5秒执行一次",
            required = false
    )
    @TableField("POLL_INTERVAL_MS")
    private Long pollIntervalMs;

    @ApiModelProperty(
            value = "kafka topic",
            required = false
    )
    @TableField("TOPIC")
    private String topic;

    @ApiModelProperty(
            value = "状态",
            required = false
    )
    @TableField("STATUS")
    private String status;

    @TableField("SYS_ISVALID")
    private String sysIsvalid;

    @TableField("CREATER_NAME")
    private String createrName;

    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;


}
