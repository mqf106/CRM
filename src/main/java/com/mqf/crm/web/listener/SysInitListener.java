package com.mqf.crm.web.listener;

import com.mqf.crm.settings.domain.DicValue;
import com.mqf.crm.settings.service.DicService;
import com.mqf.crm.settings.service.impl.DicServiceImpl;
import com.mqf.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    /*
     该方法是用来监听上下文域对象的方法，当服务器启动，对象创建完毕后，执行该方法
     该参数能够取得监听的对象（这里监听的是上下文域对象所以能通过参数获得上下文域对象）
    */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("创建了全局作用域对象，或者叫上下文域对象");
        ServletContext application = sce.getServletContext();
        //从业务层要到数据字典按type分配的几组数据
        DicService dicService = (DicService) ServiceFactory.getService(new DicServiceImpl());
        //打包成map集合("类型1",多条数据数组)，("类型2",多条数据数组)....
        Map<String, List<DicValue>> map = dicService.getAll();
        //将获取的map拆解取数据字典，放到上下文域中，application.setAttribute()
        Set<String> set = map.keySet();
        for (String key:set) {
            application.setAttribute(key,map.get(key));
        }

        //处理完数据字典后，需要将自动分析可能性的文件的键值对加载，处理成java可用的键值对关系（Stage2Possibility.properties）->(Map)
        //Map<String(stage),String(可能性)>
        Map<String,String> pMap = new HashMap<>();
        //解析properties文件
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = rb.getKeys();
        while (e.hasMoreElements()){
            //阶段
            String key = e.nextElement();
            //可能性
            String value = rb.getString(key);
            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap);
    }
}
