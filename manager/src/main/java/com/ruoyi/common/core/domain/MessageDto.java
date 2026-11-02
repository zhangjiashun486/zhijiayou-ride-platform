package com.ruoyi.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class MessageDto {
    private String ids;
    private int operation; // 1添加 2修改 3删除
    private String type; // HITCH_AI_MSG ， HITCH_AI_FILE
}
