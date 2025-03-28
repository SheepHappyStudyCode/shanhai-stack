package edu.neuq.techhub.domain.vo;

import lombok.Data;

@Data
public class CaptchaVO {

    private String uuid;

    /**
     * 验证码图片
     */
    private String img;

}