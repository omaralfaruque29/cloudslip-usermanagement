package com.cloudslip.usermanagement.model;


import com.cloudslip.usermanagement.enums.VpcStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Vpc extends BaseEntity {

    private KubeCluster kubeCluster;

    @NotNull
    private String name;

    private String dashboardUrl;

    private int orderNo;

    private ObjectId companyId;

    private String namespace = "default";

    private int totalCPU;

    private int totalMemory;

    private int totalStorage;

    private int availableCPU;

    private int availableMemory;

    private int availableStorage;

    private VpcStatus vpcStatus;

    private String bandwidth;

    private boolean autoScalingEnabled = false;

    private int extendedCPU;

    private int extendedMemory;

    private int extendedStorage;

    private ArrayList<String> tags;

    private Organization organization;

    private List<Team> teamlist;

    public Vpc() {
    }

    public Vpc(KubeCluster kubeCluster, @NotNull String name, String dashboardUrl, int orderNo, ObjectId companyId, String namespace, int totalCPU, int totalMemory, int totalStorage, int availableCPU, int availableMemory, int availableStorage, VpcStatus vpcStatus, String bandwidth, boolean autoScalingEnabled, int extendedCPU, int extendedMemory, int extendedStorage) {
        this.kubeCluster = kubeCluster;
        this.name = name;
        this.dashboardUrl = dashboardUrl;
        this.orderNo = orderNo;
        this.companyId = companyId;
        this.namespace = namespace;
        this.totalCPU = totalCPU;
        this.totalMemory = totalMemory;
        this.totalStorage = totalStorage;
        this.availableCPU = availableCPU;
        this.availableMemory = availableMemory;
        this.availableStorage = availableStorage;
        this.vpcStatus = vpcStatus;
        this.bandwidth = bandwidth;
        this.autoScalingEnabled = autoScalingEnabled;
        this.extendedCPU = extendedCPU;
        this.extendedMemory = extendedMemory;
        this.extendedStorage = extendedStorage;
    }

    public KubeCluster getKubeCluster() {
        return kubeCluster;
    }

    public void setKubeCluster(KubeCluster kubeCluster) {
        this.kubeCluster = kubeCluster;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    @JsonIgnore
    public ObjectId getCompanyObjectId() {
        return companyId;
    }

    public String getCompanyId() {
        return companyId.toHexString();
    }

    public void setCompanyId(ObjectId companyId) {
        this.companyId = companyId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getTotalCPU() {
        return totalCPU;
    }

    public void setTotalCPU(int totalCPU) {
        this.totalCPU = totalCPU;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(int totalStorage) {
        this.totalStorage = totalStorage;
    }

    public int getAvailableCPU() {
        return availableCPU;
    }

    public void setAvailableCPU(int availableCPU) {
        this.availableCPU = availableCPU;
    }

    public int getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(int availableMemory) {
        this.availableMemory = availableMemory;
    }

    public int getAvailableStorage() {
        return availableStorage;
    }

    public void setAvailableStorage(int availableStorage) {
        this.availableStorage = availableStorage;
    }

    public VpcStatus getVpcStatus() {
        return vpcStatus;
    }

    public void setVpcStatus(VpcStatus vpcStatus) {
        this.vpcStatus = vpcStatus;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public boolean isAutoScalingEnabled() {
        return autoScalingEnabled;
    }

    public void setAutoScalingEnabled(boolean autoScalingEnabled) {
        this.autoScalingEnabled = autoScalingEnabled;
    }

    @JsonIgnore
    public int getExtendedCPU() {
        return extendedCPU;
    }

    public void setExtendedCPU(int extendedCPU) {
        this.extendedCPU = extendedCPU;
    }

    @JsonIgnore
    public int getExtendedMemory() {
        return extendedMemory;
    }

    public void setExtendedMemory(int extendedMemory) {
        this.extendedMemory = extendedMemory;
    }

    @JsonIgnore
    public int getExtendedStorage() {
        return extendedStorage;
    }

    public void setExtendedStorage(int extendedStorage) {
        this.extendedStorage = extendedStorage;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Team> getTeamlist() {
        return teamlist;
    }

    public void setTeamlist(List<Team> teamlist) {
        this.teamlist = teamlist;
    }
}
