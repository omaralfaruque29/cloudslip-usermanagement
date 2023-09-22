package com.cloudslip.usermanagement.core;

import com.cloudslip.usermanagement.datamodel.DataModelManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class OnApplicationStart {

    private Logger log = LogManager.getLogger(OnApplicationStart.class);

    @Autowired
    private DataModelManager dm;

    @Autowired
    private CreateAndSubscribeTopicForClusterAgentResponse createAndSubscribeTopicForClusterAgentResponse;

    @Autowired
    private Environment env;

    @PostConstruct
    public void init(){
        dm.runUpdate(env);
        createAndSubscribeTopicForClusterAgentResponse.execute(env);
    }
}