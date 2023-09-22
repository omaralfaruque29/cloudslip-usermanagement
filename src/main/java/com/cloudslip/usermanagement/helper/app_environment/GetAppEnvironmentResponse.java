package com.cloudslip.usermanagement.helper.app_environment;

import com.cloudslip.usermanagement.dto.*;
import com.cloudslip.usermanagement.dto.app_environment.*;
import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.Status;
import com.cloudslip.usermanagement.enums.VpcStatus;
import com.cloudslip.usermanagement.exception.model.ApiErrorException;
import com.cloudslip.usermanagement.helper.AbstractHelper;
import com.cloudslip.usermanagement.model.*;
import com.cloudslip.usermanagement.repository.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GetAppEnvironmentResponse extends AbstractHelper {

    @Autowired
    EnvironmentOptionRepository environmentOptionRepository;

    @Autowired
    VpcRepository vpcRepository;

    @Autowired
    CompanyRepository companyRepository;

    private AddAppEnvironmentsDTO input;
    private ResponseDTO output = new ResponseDTO();
    private List<VpcResourceDetails> vpcResourceDetailsList;

    public void init(BaseInput input, Object... extraParams) {
        this.input = (AddAppEnvironmentsDTO) input;
        this.setOutput(output);
        this.vpcResourceDetailsList = new ArrayList<>();
    }


    protected void checkPermission() {
        if (requester == null || requester.hasAuthority(Authority.ANONYMOUS)) {
            output.generateErrorResponse("Unauthorized user!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void checkValidity() {
        if (input.getEnvironmentList() == null) {
            output.generateErrorResponse("Environment list is required!");
            throw new ApiErrorException(this.getClass().getName());
        }
    }


    protected void doPerform() {
        Company company = this.getCompanyFromUser();
        List<EnvironmentDTO> inputEnvironmentList = input.getEnvironmentList();
        List<EnvironmentOption> environmentOptionList = new ArrayList<>();
        Map<ObjectId, List<VpcResourceDetails>> vpcResourceDetailsMap = new HashMap<>();
        for (EnvironmentDTO environmentDTO : inputEnvironmentList) {
            if (environmentDTO.getEnvironmentId() == null) {
                output.generateErrorResponse("Environment list is required!");
                throw new ApiErrorException(this.getClass().getName());
            }
            Optional<EnvironmentOption> environmentOption = environmentOptionRepository.findByIdAndStatus(environmentDTO.getEnvironmentId(), Status.V);
            if(!environmentOption.isPresent()) {
                output.generateErrorResponse("Environment not found!");
                throw new ApiErrorException(this.getClass().getName());
            }

            /*
                check Vpc for each selected selected environment
             */
            this.validatedVpcListAndAddToList(environmentOption.get(), environmentDTO);
            if (!this.containsDuplicateEnv(environmentOptionList, environmentOption.get())) {
                environmentOptionList.add(environmentOption.get());
            }
            vpcResourceDetailsMap.put(environmentOption.get().getObjectId(), vpcResourceDetailsList);
        }

        AddAppEnvironmentsResponseDTO addAppEnvironmentsResponseDTO = new AddAppEnvironmentsResponseDTO();
        addAppEnvironmentsResponseDTO.setEnvironmentOptionList(environmentOptionList);
        addAppEnvironmentsResponseDTO.setVpcResourceDetailsMap(vpcResourceDetailsMap);
        addAppEnvironmentsResponseDTO.setCompany(company);
        output.generateSuccessResponse(addAppEnvironmentsResponseDTO);
    }


    protected void postPerformCheck() {
    }

    protected void doRollback() {

    }

    /*
        Get Company For Any User. If User has super admin authority than user must have to submit companyId
     */
    private Company getCompanyFromUser() {
        Optional<Company> company;
        if (requester.hasAuthority(Authority.ROLE_SUPER_ADMIN)) {
            if (input.getCompanyId() == null || input.getCompanyId().toString().equals("")) {
                output.generateErrorResponse("Company id required!");
                throw new ApiErrorException(this.getClass().getName());
            } else {
                company = companyRepository.findById(input.getCompanyId());
            }
        } else {
            company = companyRepository.findById(requester.getCompanyId());
        }
        if (!company.isPresent()) {
            output.generateErrorResponse("Company not found!");
            throw new ApiErrorException(this.getClass().getName());
        }
        return company.get();
    }

    private void validatedVpcListAndAddToList(EnvironmentOption environmentOption, EnvironmentDTO environmentDTO) {

        // check if input vpc list for any environment is given or not. If the environment is dev than vpc list is not mandatory
        if (environmentDTO.getSelectedVpcList() == null && !environmentOption.getShortName().equalsIgnoreCase("dev")) {
            output.generateErrorResponse(String.format("Vpc list required for '%s' environment", environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }

        List<VpcResourceDetailsDTO> inputVpcResourceDetailsList  = environmentDTO.getSelectedVpcList();

        // if environment is development than its not mandatory to have vpc's
        if (environmentOption.getShortName().equalsIgnoreCase("dev") && inputVpcResourceDetailsList == null) {
            this.vpcResourceDetailsList = new ArrayList<>();
        } else {
            this.vpcResourceDetailsList = new ArrayList<>();
            for (VpcResourceDetailsDTO inputVpcResourceDetails : inputVpcResourceDetailsList) {

                // checking if vpc id is given by the user or not
                if (inputVpcResourceDetails.getVpcId() == null) {
                    output.generateErrorResponse(String.format("Vpc id is missing for '%s' environment", environmentOption.getName()));
                    throw new ApiErrorException(this.getClass().getName());
                }

                // checking the existence of vpc from the provided vpc id by the user
                Optional<Vpc> vpc = vpcRepository.findByIdAndStatus(inputVpcResourceDetails.getVpcId(), Status.V);
                if(!vpc.isPresent()) {
                    output.generateErrorResponse(String.format("No Vpc Found With The Id - %s",
                            inputVpcResourceDetails.getVpcId().toHexString()));
                    throw new ApiErrorException(this.getClass().getName());
                }
                if (vpc.get().getVpcStatus() == null || vpc.get().getVpcStatus() != VpcStatus.INITIALIZED) {
                    output.generateErrorResponse(String.format("Vpc '%s' is not initialized", vpc.get().getName()));
                    throw new ApiErrorException(this.getClass().getName());
                }
                // check if same vpc provided multiple times or not
                if (!this.containsDuplicateVpc(vpcResourceDetailsList, vpc.get())) {
                    if (this.validateResourceDetails(inputVpcResourceDetails, vpc.get(), environmentOption)) { // check if resources are valid or not
                        vpcResourceDetailsList.add(this.getVpcResourceDetailsResponse(inputVpcResourceDetails, vpc.get()));
                    }
                }
            }

            // if environment is not development than user must have to select at least one vpc
            if (!environmentOption.getShortName().equalsIgnoreCase("dev") && this.vpcResourceDetailsList.isEmpty()) {
                output.generateErrorResponse(String.format("User have to select at least one vpc for '%s' environment", environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }


    /*
        Return Vpc Response with Resource Details
     */
    private VpcResourceDetails getVpcResourceDetailsResponse(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc vpc) {
        VpcResourceDetails vpcResourceDetails = new VpcResourceDetails();
        vpcResourceDetails.setVpc(vpc);
        vpcResourceDetails.setAutoScalingEnabled(inputVpcResourceDetails.isAutoScalingEnabled());
        vpcResourceDetails.setCanaryDeploymentEnabled(inputVpcResourceDetails.isCanaryDeploymentEnabled());
        vpcResourceDetails.setMaxMemory(inputVpcResourceDetails.getMaxMemory());
        vpcResourceDetails.setMinMemory(inputVpcResourceDetails.getMinMemory());
        vpcResourceDetails.setMinCpu(inputVpcResourceDetails.getMinCpu());
        vpcResourceDetails.setMaxCpu(inputVpcResourceDetails.getMaxCpu());
        vpcResourceDetails.setMaxStorage(inputVpcResourceDetails.getMaxStorage());
        vpcResourceDetails.setMaxNumOfInstance(inputVpcResourceDetails.getMaxNumOfInstance());
        vpcResourceDetails.setMinNumOfInstance(inputVpcResourceDetails.getMinNumOfInstance());
        vpcResourceDetails.setCpuThreshold(inputVpcResourceDetails.getCpuThreshold());
        vpcResourceDetails.setTransactionPerSecondThreshold(inputVpcResourceDetails.getTransactionPerSecondThreshold());
        vpcResourceDetails.setDesiredNumberOfInstance(inputVpcResourceDetails.getDesiredNumberOfInstance());
        return vpcResourceDetails;
    }

    /*
        check resource details validity
    */
    private boolean validateResourceDetails(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc vpc, EnvironmentOption environmentOption) {

        this.validateMaxCpu(inputVpcResourceDetails, vpc, environmentOption);
        this.validateMinCpu(inputVpcResourceDetails, vpc, environmentOption);
        this.validateMaxMemory(inputVpcResourceDetails, vpc, environmentOption);
        this.validateMinMemory(inputVpcResourceDetails, vpc, environmentOption);
        this.validateAutoScalingDetails(inputVpcResourceDetails, vpc, environmentOption);
        this.validateMaxStorage(inputVpcResourceDetails, vpc, environmentOption);

        return true;
    }

     /*
        Check the input max storage with the available vpc storage
    */
    private void validateMaxStorage(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc selectedVpc, EnvironmentOption environmentOption) {
        if (inputVpcResourceDetails.getMaxStorage() <= 0) {
            output.generateErrorResponse(String.format("Max storage should be greater than zero for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        Check the input max cpu with the available vpc cpu
    */
    private void validateMaxCpu(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc selectedVpc, EnvironmentOption environmentOption) {
        if (inputVpcResourceDetails.getMaxCpu() <= 0) {
            output.generateErrorResponse(String.format("Max CPU should be greater than zero for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        Check the input min cpu with the available vpc cpu
    */
    private void validateMinCpu(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc selectedVpc, EnvironmentOption environmentOption) {
        if (inputVpcResourceDetails.getMinCpu() <= 0) {
            output.generateErrorResponse(String.format("Minimum CPU should be greater than zero for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        } else if (inputVpcResourceDetails.getMinCpu() > inputVpcResourceDetails.getMaxCpu()) {
            output.generateErrorResponse(String.format("Minimum CPU cannot be greater than maximum CPU for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        Check the input max memory with the available vpc memory
     */
    private void validateMaxMemory(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc selectedVpc, EnvironmentOption environmentOption) {
        if (inputVpcResourceDetails.getMaxMemory() <= 0) {
            output.generateErrorResponse(String.format("Max memory should be greater than zero for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
     Check the input min memory
 */
    private void validateMinMemory(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc selectedVpc, EnvironmentOption environmentOption) {
        if (inputVpcResourceDetails.getMinMemory() <= 0) {
            output.generateErrorResponse(String.format("Minimum memory should not be equal or less than zero for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        } else if (inputVpcResourceDetails.getMinMemory() > inputVpcResourceDetails.getMaxMemory()) {
            output.generateErrorResponse(String.format("Minimum memory cannot be greater than max memory for '%s' vpc of '%s' environment!", selectedVpc.getName(), environmentOption.getName()));
            throw new ApiErrorException(this.getClass().getName());
        }
    }

    /*
        Check the input auto scaling details
     */
    private void validateAutoScalingDetails(VpcResourceDetailsDTO inputVpcResourceDetails, Vpc vpc, EnvironmentOption environmentOption) {
        // if auto scaling details enabled than only check min instance, max instance, cpu threshold, transaction per second
        if (inputVpcResourceDetails.isAutoScalingEnabled()) {
            if (inputVpcResourceDetails.getMinNumOfInstance() <= 0) {
                output.generateErrorResponse(String.format("Minimum number of instance should be greater than zero for '%s' vpc of '%s' environment!", vpc.getName(), environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            } else if (inputVpcResourceDetails.getMaxNumOfInstance() <= 0 || inputVpcResourceDetails.getMaxNumOfInstance() < inputVpcResourceDetails.getMinNumOfInstance()) {
                output.generateErrorResponse(String.format("Max number of instance should be greater than zero and greater or equal than minimum number of instance for '%s' vpc of '%s' environment!", vpc.getName(), environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            } else if (inputVpcResourceDetails.getTransactionPerSecondThreshold() <= 0) {
                output.generateErrorResponse(String.format("Transaction per second threshold should be greater than zero for '%s' vpc of '%s' environment!", vpc.getName(), environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            } else if (inputVpcResourceDetails.getCpuThreshold() <= 0) {
                output.generateErrorResponse(String.format("CPU threshold should be greater than zero for '%s' vpc of '%s' environment!", vpc.getName(), environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            }
        } else {
            if (inputVpcResourceDetails.getDesiredNumberOfInstance() <= 0) {
                output.generateErrorResponse(String.format("Desired number of instance should be greater than zero for '%s' vpc of '%s' environment!", vpc.getName(), environmentOption.getName()));
                throw new ApiErrorException(this.getClass().getName());
            }
        }
    }

    /*
        Check if User provided same environment id multiple times
     */
    private boolean containsDuplicateEnv(List<EnvironmentOption> environmentOptionList, EnvironmentOption environmentOption) {
        for (EnvironmentOption environment : environmentOptionList) {
            if (environment.getObjectId().toString().equals(environmentOption.getObjectId().toString())) {
                return true;
            }
        }
        return false;
    }

    /*
        Check if User provided same vpc id multiple times
    */
    private boolean containsDuplicateVpc(List<VpcResourceDetails> vpcResources, Vpc vpc){
        for (VpcResourceDetails cls : vpcResources) {
            if (cls.getVpc().getObjectId().toString().equals(vpc.getObjectId().toString())) {
                return true;
            }
        }
        return false;
    }
}
