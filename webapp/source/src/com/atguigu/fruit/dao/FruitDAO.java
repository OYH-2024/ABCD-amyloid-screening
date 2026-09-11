package com.atguigu.fruit.dao;

import com.atguigu.fruit.pojo.Fruit;

import java.util.List;

public interface FruitDAO {
    //获取指定页码所有的库存列表信息
    List<Fruit> getFruitList(String keyword,Integer pageNo);
    List<Fruit> getFruitList2(String keyword,Integer pageNo);

    //根据tid获取特定的小分子库存信息
    Fruit getFruitByFid(Integer tid);

    int getFruitCount(String keyword);
    int getFruitCount2(String keyword);

    // 新增：按疾病 + 关键字 组合过滤
    List<Fruit> getFruitListByDeseaseAndKeyword(String desease, String keyword, Integer pageNo);
    int getFruitCountByDeseaseAndKeyword(String desease, String keyword);

    // 新增：显示全部数据，不按 desease 过滤
    List<Fruit> getFruitListAll(String keyword, Integer pageNo);
    int getFruitCountAll(String keyword);


}

