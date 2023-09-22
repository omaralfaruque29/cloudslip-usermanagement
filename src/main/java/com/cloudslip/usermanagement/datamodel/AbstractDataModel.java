package com.cloudslip.usermanagement.datamodel;

import org.springframework.core.env.Environment;

public abstract class AbstractDataModel implements DataModel {

    public AbstractDataModel() {

    }

    public void execute(DataModelManager dm, int lastDataModelVersion, Environment env) {
        doExecute(dm, lastDataModelVersion, env);
        updateDataModelVersion();
    }

    protected void doExecute(DataModelManager dm, int lastDataModelVersion, Environment env) {

    }

    protected void updateDataModelVersion() {

    }
}
