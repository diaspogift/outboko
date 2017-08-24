package test.outbouko.is.cm.testoutboko.model;

/**
 * Created by Ange_Douki on 06/08/2016.
 */
public class Delivery {
    private int id, council_id, division_id, region_id, country_id;
    private String tracking_code, quarter, city,  name_surname, email, phonenumber, description;
    private boolean checked;

    public  Delivery(){}

    public Delivery(int id, int council_id, int division_id, int region_id, int country_id,
                    String tracking_code, String quarter, String city, String name_surname,
                    String email, String phonenumber, String description) {
        this.id = id;
        this.council_id = council_id;
        this.division_id = division_id;
        this.region_id = region_id;
        this.country_id = country_id;
        this.tracking_code = tracking_code;
        this.quarter = quarter;
        this.city = city;
        this.name_surname = name_surname;
        this.email = email;
        this.phonenumber = phonenumber;
        this.description = description;
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCouncil_id() {
        return council_id;
    }

    public void setCouncil_id(int council_id) {
        this.council_id = council_id;
    }

    public int getDivision_id() {
        return division_id;
    }

    public void setDivision_id(int division_id) {
        this.division_id = division_id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getTracking_code() {
        return tracking_code;
    }

    public void setTracking_code(String tracking_code) {
        this.tracking_code = tracking_code;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName_surname() {
        return name_surname;
    }

    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String toString(){
        return "{id: "+ this.id + ", council_id" + council_id+ ", division_id" + division_id+
                ", region_id: " + region_id + ", country_id: "+ country_id + ", tracking_code: "+ tracking_code +
                ", quarter: " + quarter + ", city: "+ city + ", name_surname: " + name_surname+
                ", email: " + email + ", phonenumber: " + phonenumber + ", description: " + description +
                ",checked: " + checked + "}";
    }
}
