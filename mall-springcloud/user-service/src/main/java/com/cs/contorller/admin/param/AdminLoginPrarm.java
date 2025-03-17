package com.cs.contorller.admin.param;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class AdminLoginPrarm implements Serializable {

    @NotEmpty(message = "登录名不能为空")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    private String passwordMd5;
}
