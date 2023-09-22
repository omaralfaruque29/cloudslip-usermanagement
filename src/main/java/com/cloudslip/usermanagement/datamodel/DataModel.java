package com.cloudslip.usermanagement.datamodel;

import org.springframework.core.env.Environment;

public interface DataModel {
    public void execute(DataModelManager dm, int lastDataModelVersion, Environment env);
}
