package com.studyolle.studyolle.zone;

import com.studyolle.studyolle.domain.Zone;
import lombok.Data;

import javax.persistence.Column;

@Data
public class ZoneDto {

    private String zoneName;

    private String city;

    private String localNameOfCity;

    private String province;

    // TODO "%s(%s)/%s", city, localNameOfCity, province 단위별로 잘라내기

    public String getCity(){
        int i = zoneName.indexOf('(');
        city = zoneName.substring(0, i);
        return city;
    }
    public String getLocalNameOfCity(){
        int i = zoneName.indexOf('(');
        int i1 = zoneName.indexOf(')');
        localNameOfCity = zoneName.substring(i + 1, i1);
        return localNameOfCity;
    }
    public String getProvince(){
        int i = zoneName.indexOf('/');
        int length = zoneName.length();
        province = zoneName.substring(i + 1, length);
        return province;
    }
}
