package com.studyolle.studyolle.modules.zone;

import lombok.Data;

@Data
public class ZoneDto {

    private String path;

    private String zoneName;

    private String city;

    private String localNameOfCity;

    private String province;

    // TODO "%s(%s)/%s", city, localNameOfCity, province 단위별로 잘라내기

    public String getCity(){
        int i = zoneName.indexOf('(');
        if(i == -1) return "";
        city = zoneName.substring(0, i);
        return city;
    }
    public String getLocalNameOfCity(){
        int i = zoneName.indexOf('(');
        int i1 = zoneName.indexOf(')');
        if(i == -1 || i1 == -1) return "";
        localNameOfCity = zoneName.substring(i + 1, i1);
        return localNameOfCity;
    }
    public String getProvince(){
        int i = zoneName.indexOf('/');
        int length = zoneName.length();
        if(i == -1) return "";
        province = zoneName.substring(i + 1, length);
        return province;
    }
}
