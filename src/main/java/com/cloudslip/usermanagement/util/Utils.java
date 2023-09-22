package com.cloudslip.usermanagement.util;

import com.cloudslip.usermanagement.enums.Authority;
import com.cloudslip.usermanagement.enums.UserType;
import com.cloudslip.usermanagement.model.User;
import com.cloudslip.usermanagement.model.UserInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.*;

public class Utils {

    private static JsonParser jsonParser = new JsonParser();

    public static UserInfo getCurrentUser() {
        return new UserInfo();
    }

    public static UserInfo getUser() {
        return new UserInfo();
    }

    public static User generateUserFromJsonStr(String userStr) {
        if(userStr == "" || userStr.equals("")) {
            User requester = new User();
            requester.setUsername("anonymous");
            List<Authority> authorities = new ArrayList<>();
            authorities.add(Authority.ANONYMOUS);
            requester.setAuthorities(authorities);
            return requester;
        }
        JsonObject currentUserJO = jsonParser.parse(userStr).getAsJsonObject();
        User currentUser = new User();
        try {
            currentUser.setId(currentUserJO.has("id") ? new ObjectId(currentUserJO.get("id").getAsString()) : null);
            currentUser.setUsername(currentUserJO.has("username") ? currentUserJO.get("username").getAsString() : null);
            currentUser.setEnabled(currentUserJO.has("isEnabled") ? currentUserJO.get("isEnabled").getAsBoolean() : false);
            String authoritiesStr = currentUserJO.has("authorities") ? currentUserJO.get("authorities").getAsString() : "";
            List<String> authorityStrList = Arrays.asList(authoritiesStr.split(","));
            List<Authority> authorities = new ArrayList<>();
            for (String authorityStr : authorityStrList) {
                if(authorityStr.equals(Authority.ROLE_SUPER_ADMIN.getAuthority())) {
                    authorities.add(Authority.ROLE_SUPER_ADMIN);
                } else if(authorityStr.equals(Authority.ROLE_ADMIN.getAuthority())) {
                    authorities.add(Authority.ROLE_ADMIN);
                } else if(authorityStr.equals(Authority.ROLE_DEV.getAuthority())) {
                    authorities.add(Authority.ROLE_DEV);
                } else if(authorityStr.equals(Authority.ROLE_OPS.getAuthority())) {
                    authorities.add(Authority.ROLE_OPS);
                } else if (authorityStr.equals(Authority.ROLE_AGENT_SERVICE.getAuthority())) {
                    authorities.add(Authority.ROLE_AGENT_SERVICE);
                } else if (authorityStr.equals(Authority.ROLE_GIT_AGENT.getAuthority())) {
                    authorities.add(Authority.ROLE_GIT_AGENT);
                }
            }
            currentUser.setAuthorities(authorities);
            currentUser.setUserType((currentUserJO.has("userType") && currentUserJO.get("userType").getAsString() != "") ? UserType.valueOf(currentUserJO.get("userType").getAsString()) : null);
            currentUser.setCompanyId((currentUserJO.has("companyId") && currentUserJO.get("companyId").getAsString() != "") ? new ObjectId(currentUserJO.get("companyId").getAsString()) : null);
            currentUser.setOrganizationId((currentUserJO.has("organizationId") && currentUserJO.get("organizationId").getAsString() != null && (!isStringEquals(currentUserJO.get("organizationId").getAsString(), ""))) ? new ObjectId(currentUserJO.get("organizationId").getAsString()) : null);

            String teamIdsStr = currentUserJO.has("teamIds") ? currentUserJO.get("teamIds").getAsString() : "";
            List<String> teamIdStrList = Arrays.asList(teamIdsStr.split(","));
            List<ObjectId> teamIdList = new ArrayList<>();
            for (String teamId : teamIdStrList) {
                teamIdList.add(new ObjectId(teamId));
            }
            currentUser.setTeamIdList(teamIdList);

        } catch (Exception ex) {
            return  currentUser;
        }
        return currentUser;
    }

    public static String removeAllSpaceWithUnderScore(String input) {
        return input.replaceAll("\\s+","_");
    }

    public static String removeAllSpaceWithDash(String input) {
        return input.replaceAll("\\s+","-");
    }

    public static boolean isStringEquals(String val1, String val2) {
        if(val1 == val2 || val1.equals(val2)) {
            return true;
        }
        return false;
    }

    public static String getBase64EncodedString(String originalString){
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        return encodedString;
    }

    public static String getBase64DecodedSrtring(String encodedString){
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    public static HttpHeaders generateHttpHeaders(String userStr) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("current-user", userStr);
        return headers;
    }
}
