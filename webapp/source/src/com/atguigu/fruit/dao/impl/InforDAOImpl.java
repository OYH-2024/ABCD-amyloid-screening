package com.atguigu.fruit.dao.impl;

import com.atguigu.fruit.dao.InforDAO;
import com.atguigu.fruit.pojo.Infor;
import com.atguigu.myssm.basedao.BaseDAO;

import java.util.List;

public class InforDAOImpl extends BaseDAO<Infor> implements InforDAO {

    @Override
    public List<Infor> getInforList(String keyword, Integer pageNo) {
        return super.executeQuery(
                "select * from dbinfor where oid like ? or information like ? or information2 like ? limit ? , 15",
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", (pageNo - 1) * 15
        );
    }

    @Override
    public Infor getInforByOid(Integer oid) {
        return super.load("select * from dbinfor where oid = ? ", oid);
    }

    @Override
    public int getInforCount(String keyword) {
        return ((Long) super.executeComplexQuery(
                "select count(*) from dbinfor where oid like ? or information like ? or information2 like ?",
                "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"
        )[0]).intValue();
    }

    @Override
    public List<Infor> getInforListByTargetAndKeyword(String targetName, String keyword, Integer pageNo) {
        return super.executeQuery(
                "select * from dbinfor where target_name = ? and (information like ? or information2 like ?) limit ? , 15",
                targetName, "%" + keyword + "%", "%" + keyword + "%", (pageNo - 1) * 15
        );
    }

    @Override
    public int getInforCountByTargetAndKeyword(String targetName, String keyword) {
        return ((Long) super.executeComplexQuery(
                "select count(*) from dbinfor where target_name = ? and (information like ? or information2 like ?)",
                targetName, "%" + keyword + "%", "%" + keyword + "%"
        )[0]).intValue();
    }
}