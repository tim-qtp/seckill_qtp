package com.seckill.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author http://www.itheima.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemCertificate {

    private String clientId;
    private String base64Secret;
    private String name;
    private int expiresSecond;

}
