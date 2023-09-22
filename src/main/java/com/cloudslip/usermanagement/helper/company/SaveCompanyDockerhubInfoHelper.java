package com.cloudslip.usermanagement.helper.company;

import com.cloudslip.usermanagement.constant.ApplicationProperties;
import com.cloudslip.usermanagement.dto.BaseInput;
import com.cloudslip.usermanagement.dto.ResponseDTO;
import com.cloudslip.usermanagement.dto.SaveCompanyDockerHubInfoInputDTO;
import com.cloudslip.usermanagement.dto.kubeconfig.SecretConfig;
import com.cloudslip.usermanagement.enums.AgentCommand;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.DockerRegistryType;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.kafka.KafkaPublisher;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessage;
import com.cloudslip.usermanagement.kafka.dto.KafkaMessageHeader;
import com.cloudslip.usermanagement.model.Company;
import com.cloudslip.usermanagement.model.Vpc;
import com.cloudslip.usermanagement.model.dummy.DockerHubInfo;
import com.cloudslip.usermanagement.repository.CompanyRepository;
import com.cloudslip.usermanagement.repository.VpcRepository;
import com.cloudslip.usermanagement.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


@Service
public class SaveCompanyDockerhubInfoHelper extends AbstractHelper {

    private final Logger log = LoggerFactory.getLogger(SaveCompanyDockerhubInfoHelper.class);

    private SaveCompanyDockerHubInfoInputDTO input;
    private ResponseDTO output = new ResponseDTO();
    private Company company;
    Optional<Company> companyOptional;


    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;


    public void init(BaseInput input, Object... extraParams) {
        this.input = (SaveCompanyDockerHubInfoInputDTO) input;
        this.setOutput(output);
    }


    protected void checkPermission() {
        if (requester == null || (!requester.hasAuthority(Authority.ROLE_SUPER_ADMIN) && !requester.hasAuthority(Authority.ROLE_ADMIN))) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if(requester.getCompanyId() == null) {
            output.generateErrorResponse("Invalid User");
            throw new ApiErrorException(output.getMessage(), this.getClass().getName());
        }
        companyOptional = companyRepository.findById(requester.getCompanyId());
        if(!(companyOptional.isPresent())) {
            output.generateErrorResponse(String.format("No Company found with the id - %s", requester.getCompanyIdAsString()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        if(input.getDockerRegistryType() == DockerRegistryType.DOCKER_HUB) {
            input.setDockerRegistryServer(ApplicationProperties.DOCKER_HUB_REGISTRY_SERVER);
        }
        company = companyOptional.get();
        DockerHubInfo oldDockerHubInfo = company.getDockerHubInfo();
        if(oldDockerHubInfo == null || !oldDockerHubInfo.getDockerRegistryType().equals(input.getDockerRegistryType())
                || !oldDockerHubInfo.getDockerhubId().equals(input.getDockerhubId())
                || (oldDockerHubInfo.getDockerhubEmail() == null || !oldDockerHubInfo.getDockerhubEmail().equals(input.getDockerhubEmail()))
                || !oldDockerHubInfo.getDockerRegistryServer().equals(input.getDockerRegistryServer())
                || (!Utils.getBase64DecodedSrtring(oldDockerHubInfo.getDockerhubPassword()).equals(input.getDockerhubPassword())) && !oldDockerHubInfo.getDockerhubPassword().equals(input.getDockerhubPassword())) {

            String password = Utils.getBase64EncodedString(input.getDockerhubPassword());
            if(oldDockerHubInfo != null && (!Utils.getBase64DecodedSrtring(oldDockerHubInfo.getDockerhubPassword()).equals(input.getDockerhubPassword())) && !oldDockerHubInfo.getDockerhubPassword().equals(input.getDockerhubPassword())) {
                password = Utils.getBase64EncodedString(input.getDockerhubPassword());
            } else if(oldDockerHubInfo != null){
                password = oldDockerHubInfo.getDockerhubPassword();
            }
            DockerHubInfo newDockerHubInfo = new DockerHubInfo(input.getDockerRegistryType(), input.getDockerRegistryServer(), input.getDockerhubId(), password, input.getDockerhubEmail());
            company.setDockerHubInfo(newDockerHubInfo);
            company = companyRepository.save(company);

            this.generateSecreteConfigAndPublishKafkaMessageForNewDockerHubSecret(oldDockerHubInfo, newDockerHubInfo);

            output.generateSuccessResponse(null, "Docker Registry Information is updated");
        } else {
            output.generateSuccessResponse(null,"No change detected");
        }
    }


    private void generateSecreteConfigAndPublishKafkaMessageForNewDockerHubSecret(DockerHubInfo oldDockerHubInfo, @NotNull DockerHubInfo newDockerHubInfo) {
        List<Vpc> companyVpcList = vpcRepository.findAllByCompanyIdAndStatus(company.getObjectId(), Status.V);
        for(Vpc vpc : companyVpcList) {
            String secretName = "regcred";
            SecretConfig secretConfig = new SecretConfig();
            secretConfig.generateForDockerhubCredentials(secretName, vpc.getNamespace(), newDockerHubInfo.getDockerConfigData());
            publishKafkaMessageForNewDockerHubSecret(vpc.getId(), vpc.getKubeCluster().getKafkaTopic(), secretConfig, AgentCommand.CREATE_SECRET);
        }
    }

    private void publishKafkaMessageForNewDockerHubSecret(String vpcId, String targetVpcKafkaTopic, SecretConfig secretConfig, AgentCommand agentCommand) {
        KafkaMessageHeader header = new KafkaMessageHeader(secretConfig.getNamespace(), agentCommand.toString());
        header.setCompanyId(company.getId());
        header.addToExtra("vpcId", vpcId);
        KafkaMessage message = new KafkaMessage(header, secretConfig);
        kafkaPublisher.publishMessage(targetVpcKafkaTopic, message);
    }

    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }
}
