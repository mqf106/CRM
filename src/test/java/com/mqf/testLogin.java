package com.mqf;

import com.mqf.crm.utils.DateTimeUtil;
import org.junit.Test;

public class testLogin {
    @Test
    public void testExpireTime(){
        String expireTime = "2020-12-12 12:12:12";
        String nowDate = DateTimeUtil.getSysTime();
        System.out.println(nowDate);
        int count = expireTime.compareTo(nowDate);
        System.out.println(count);
    }
    @Test
    public void testLock(){
        String lockState = "0";
        if ("0".equals(lockState)){
            System.out.println("用户已经锁定");
        }else {
            System.out.println("用户已启用");
        }
    }
    @Test
    public void testIp(){

    }
}
