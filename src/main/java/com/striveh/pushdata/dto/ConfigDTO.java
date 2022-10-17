package com.striveh.pushdata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@ApiModel(
        value = "任务配置"
)
@Data
public class ConfigDTO {
    @NotBlank
    @ApiModelProperty(
            value = "kafka topic前缀，与表名称一起组成topic",
            required = true
    )
    private String topicPrefix;
    @NotBlank
    @ApiModelProperty(
            value = "表名称",
            required = true
    )
    private String tableName;

    @ApiModelProperty(
            value = "初始同步时间，默认全部同步",
            required = true
    )
    private LocalDateTime timestampInitial;

    @ApiModelProperty(
            value = "同步频率，单位秒，默认每5秒执行一次",
            required = true
    )
    private Long pollIntervalMs;

    @ApiModelProperty(
            value = "当前登录用户",
            required = true
    )
    private String createrName;
}
