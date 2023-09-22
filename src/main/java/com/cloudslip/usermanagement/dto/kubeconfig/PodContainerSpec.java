package com.cloudslip.usermanagement.dto.kubeconfig;


import com.cloudslip.usermanagement.dto.NameValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PodContainerSpec {
    private String name;
    private String imagePullPolicy = "Always";
    private String image;
    private PodResourceSpec resourceSpec;
    private List<NameValue> environmentVariableList;
    private List<Integer> portList;


    public PodContainerSpec() {
        this.environmentVariableList = new ArrayList<>();
    }

    public PodContainerSpec(String name, String imagePullPolicy, String image, PodResourceSpec resourceSpec, List<NameValue> environmentVariableList, List<Integer> portList) {
        this.name = name;
        this.imagePullPolicy = imagePullPolicy;
        this.image = image;
        this.resourceSpec = resourceSpec;
        this.environmentVariableList = environmentVariableList;
        this.portList = portList;
    }

    public PodContainerSpec(String name, String dockerRepoName, String gitCommitId, int maxCpu, int maxMemory, Integer... ports) {
        this.name = name;
        this.image = dockerRepoName + ":" + gitCommitId;
        this.resourceSpec = new PodResourceSpec(maxCpu, maxMemory);
        this.environmentVariableList = new ArrayList<>(Arrays.asList(new NameValue("GIT_COMMIT", gitCommitId)));
        this.portList = new ArrayList<>(Arrays.asList(ports));
    }

    public PodContainerSpec(String name, String dockerRepoName, String gitCommitId, int maxCpu, int maxMemory, String imagePullPolicy, Integer... ports) {
        this(name, dockerRepoName, gitCommitId, maxCpu, maxMemory, ports);
        this.imagePullPolicy = imagePullPolicy;
    }

    public void addEnvironmentVariable(NameValue envNameValue) {
        this.environmentVariableList.add(envNameValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePullPolicy() {
        return imagePullPolicy;
    }

    public void setImagePullPolicy(String imagePullPolicy) {
        this.imagePullPolicy = imagePullPolicy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PodResourceSpec getResourceSpec() {
        return resourceSpec;
    }

    public void setResourceSpec(PodResourceSpec resourceSpec) {
        this.resourceSpec = resourceSpec;
    }

    public List<NameValue> getEnvironmentVariableList() {
        return environmentVariableList;
    }

    public void setEnvironmentVariableList(List<NameValue> environmentVariableList) {
        this.environmentVariableList = environmentVariableList;
    }

    public List<Integer> getPortList() {
        return portList;
    }

    public void setPortList(List<Integer> portList) {
        this.portList = portList;
    }
}
