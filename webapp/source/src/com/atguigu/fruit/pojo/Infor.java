package com.atguigu.fruit.pojo;

public class Infor {
    private Integer oid;
    private String target_name;   // 字段名必须和数据库列名一致（BaseDAO用反射按列名找字段）
    private String information;
    private String information2;
    private String information3;
    private String information4;
    private String information5;
    private String information6;
    private String information7;
    private String information8;
    private String information9;
    private String information10;
    private String information11;
    private String information12;
    private String information13;
    private String information14;
    private String information15;
    private String information16;

    public Infor(){}

    public Integer getOid() {
        return oid;
    }
    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public String getTargetName() {
        return target_name;
    }
    public void setTargetName(String target_name) {
        this.target_name = target_name;
    }

    public String getInformation() {
        return information;
    }
    public void setInformation(String information) {
        this.information = information;
    }

    public String getInformation2() {
        return information2;
    }
    public void setInformation2(String information2) {
        this.information2 = information2;
    }

    public String getInformation3() {
        return information3;
    }
    public void setInformation3(String information3) {
        this.information3 = information3;
    }

    public String getInformation4() {
        return information4;
    }
    public void setInformation4(String information4) {
        this.information4 = information4;
    }

    public String getInformation5() {
        return information5;
    }
    public void setInformation5(String information5) {
        this.information5 = information5;
    }

    public String getInformation6() {
        return information6;
    }
    public void setInformation6(String information6) {
        this.information6 = information6;
    }

    public String getInformation7() {
        return information7;
    }
    public void setInformation7(String information7) {
        this.information7 = information7;
    }

    public String getInformation8() {
        return information8;
    }
    public void setInformation8(String information8) {
        this.information8 = information8;
    }

    public String getInformation9() {
        return information9;
    }
    public void setInformation9(String information9) {
        this.information9 = information9;
    }

    public String getInformation10() {
        return information10;
    }
    public void setInformation10(String information10) {
        this.information10 = information10;
    }

    public String getInformation11() {
        return information11;
    }
    public void setInformation11(String information11) {
        this.information11 = information11;
    }

    public String getInformation12() {
        return information12;
    }
    public void setInformation12(String information12) {
        this.information12 = information12;
    }

    public String getInformation13() {
        return information13;
    }
    public void setInformation13(String information13) {
        this.information13 = information13;
    }

    public String getInformation14() {
        return information14;
    }
    public void setInformation14(String information14) {
        this.information14 = information14;
    }

    public String getInformation15() {
        return information15;
    }
    public void setInformation15(String information15) {
        this.information15 = information15;
    }

    public String getInformation16() {
        return information16;
    }
    public void setInformation16(String information16) {
        this.information16 = information16;
    }

    @Override
    public String toString() {
        return oid + "\t\t" + target_name + "\t\t" + information + "\t\t" + information2 + "\t\t" + information3
                + "\t\t" + information4 + "\t\t" + information5;
    }
}