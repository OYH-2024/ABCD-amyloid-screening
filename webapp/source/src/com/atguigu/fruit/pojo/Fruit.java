package com.atguigu.fruit.pojo;


public class Fruit {
    private Integer tid;

    private String desease;
    private String condition;
    private String targetname;

    private String target;

    private String tname;

    private String energy;

    private String solution;

    private String pdb;
    private String protein;

    private String plothtml;

    private String name;

    private String sid;

    private String jizhun;
    private String information2;

    public Fruit(){}

    public Fruit(Integer tid,String energy,String solution,String tname,String target) {
        this.tid= tid;
        this.energy = energy;
        this.solution = solution;
        this.tname=tname;
        this.target=target;
        this.desease=desease;
        this.condition=condition;
        this.targetname=targetname;
        this.pdb=pdb;
        this.protein=protein;
        this.plothtml=plothtml;
        this.name=name;
        this.sid=sid;
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy=energy;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution=solution;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname=tname;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target=target;
    }

    public String getDesease() {
        return desease;
    }

    public void setDesease(String desease) {
        this.desease=desease;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition=condition;
    }

    public String getTargetname() {
        return targetname;
    }

    public void setTargetname(String targetname) {
        this.targetname=targetname;
    }

    public String getPdb() {
        return pdb;
    }

    public void setPdb(String pdb) {
        this.pdb=pdb;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein=protein;
    }

    public String getPlothtml() {
        return plothtml;
    }

    public void setPlothtml(String plothtml) {
        this.plothtml=plothtml;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid=sid;
    }

    public String getJizhun() {
        return jizhun;
    }

    public void setJizhun(String jizhun) {
        this.jizhun=jizhun;
    }

    public String getInformation2() {
        return information2;
    }

    public void setInformation2(String information2) {
        this.information2=information2;
    }

    @Override
    public String toString() {
        return tid + "\t\t" + energy + "\t\t" + solution+"\t\t"+tname+"\t\t"+target+"\t\t"+desease+"\t\t"+condition+"\t\t"+targetname+"\t\t"+pdb+"\t\t"+protein+"\t\t"+plothtml+"\t\t"+name+"\t\t"+sid+"\t\t"+jizhun+"\t\t"+information2;
    }
}