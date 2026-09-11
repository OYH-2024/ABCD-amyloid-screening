package com.atguigu.fruit.dao;

import com.atguigu.fruit.pojo.Infor;

import java.util.List;

public interface InforDAO {

    List<Infor> getInforList(String keyword, Integer pageNo);

    Infor getInforByOid(Integer oid);

    int getInforCount(String keyword);

    // 按靶蛋白 + 关键字 组合过滤
    List<Infor> getInforListByTargetAndKeyword(String targetName, String keyword, Integer pageNo);

    int getInforCountByTargetAndKeyword(String targetName, String keyword);
}