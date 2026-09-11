package com.atguigu.fruit.dao.impl;

import com.atguigu.fruit.dao.FruitDAO;
import com.atguigu.fruit.pojo.Fruit;
import com.atguigu.myssm.basedao.BaseDAO;

import java.util.List;

public class FruitDAOImpl extends BaseDAO<Fruit> implements FruitDAO {

    @Override
    public List<Fruit> getFruitList(String keyword, Integer pageNo) {
        return super.executeQuery("select * from dbtest where `condition`  = ? and (target like ? or tname like ? or solution like ?) limit ?, 15",
                "monomer", "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%", (pageNo-1)*15 );
    }

    @Override
    public List<Fruit> getFruitList2(String keyword, Integer pageNo) {
        return super.executeQuery("select * from dbtest where `condition`  = ? and (target like ? or tname like ? or solution like ?) limit ?, 15",
                "fibril", "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%", (pageNo-1)*15 );
    }

    @Override
    public Fruit getFruitByFid(Integer tid) {
        return super.load("select * from dbtest where tid = ? ", tid);
    }

    @Override
    public int getFruitCount(String keyword) {
        return ((Long)super.executeComplexQuery("select count(*) from dbtest where `condition`  = ? and (target like ? or tname like ? or solution like ?)",
                "monomer", "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%")[0]).intValue();
    }

    @Override
    public int getFruitCount2(String keyword) {
        return ((Long)super.executeComplexQuery("select count(*) from dbtest where `condition`  = ? and (target like ? or tname like ? or solution like ?)",
                "fibril", "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%")[0]).intValue();
    }

    //###############0725新增
    @Override
    public List<Fruit> getFruitListByDeseaseAndKeyword(String desease, String keyword, Integer pageNo) {
        List<Fruit> result = super.executeQuery(
                "select * from dbtest where desease = ? and (target like ? or tname like ? or solution like ?) limit ?, 15",
                desease, "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%", (pageNo-1)*15
        );
        System.out.println("【调试-index】desease=[" + desease + "] 查询结果条数 = " + (result == null ? "null" : result.size()));
        return result;
    }

    @Override
    public int getFruitCountByDeseaseAndKeyword(String desease, String keyword) {
        return ((Long) super.executeComplexQuery(
                "select count(*) from dbtest where desease = ? and (target like ? or tname like ? or solution like ?)",
                desease, "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%"
        )[0]).intValue();
    }
    //###############0725新增



    //###############0725新增
    @Override
    public List<Fruit> getFruitListAll(String keyword, Integer pageNo) {
        System.out.println("【调试】keyword=[" + keyword + "], pageNo=" + pageNo);
        try {
            List<Fruit> result = super.executeQuery(
                    "select * from dbtest where (target like ? or tname like ? or solution like ?) limit ?, 15",
                    "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%", (pageNo-1)*15
            );
            System.out.println("【调试】查询结果条数 = " + (result == null ? "null" : result.size()));
            return result;
        } catch (Exception e) {
            System.out.println("【调试】executeQuery抛出异常：");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int getFruitCountAll(String keyword) {
        return ((Long) super.executeComplexQuery(
                "select count(*) from dbtest where (target like ? or tname like ? or solution like ?)",
                "%"+keyword+"%", "%"+keyword+"%", "%"+keyword+"%"
        )[0]).intValue();
    }

//###############0725新增
}