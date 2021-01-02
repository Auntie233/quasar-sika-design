package com.quasar.sika.design.server.common.captcha.constant;

import com.sika.code.basic.constant.TypeEnumInf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public enum CaptchaCodeTypeEnum implements TypeEnumInf<Integer> {
    LOGIN(10, "登录验证码", 600L, TimeUnit.SECONDS),
    ;
    private final Integer type;
    private final String desc;
    private final Long timeout;
    private final TimeUnit timeUnit;
}
