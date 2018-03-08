package io.github.boapps.eSzivacs.Datas;

/**
 * Created by boa on 29/09/17.
 */

public class School {
    private int id;
    private String name;
    private String code;
    private String url;
    private String city;

    public School(int id, String name, String code, String url, String city) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.url = url;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
