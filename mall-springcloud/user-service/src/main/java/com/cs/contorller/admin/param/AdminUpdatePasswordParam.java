package com.cs.contorller.admin.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class AdminUpdatePasswordParam implements Serializable {

    @NotEmpty(message = "旧密码不应为空呢")
    private String originalPassword;

    @NotEmpty(message = "新密码不应为空呢")
    private String newPassword;
}
