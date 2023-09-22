package com.cloudslip.usermanagement.dto;

public class SaveCompanyDTO extends BaseInputDTO {
    private String name;
    private String businessEmail;
    private String adminEmail;
    private String website;
    private String address;
    private String phoneNo;

    public SaveCompanyDTO() {
    }

    public SaveCompanyDTO(String name, String businessEmail, String adminEmail, String website, String address, String phoneNo) {
        this.name = name;
        this.businessEmail = businessEmail;
        this.adminEmail = adminEmail;
        this.website = website;
        this.address = address;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

}
