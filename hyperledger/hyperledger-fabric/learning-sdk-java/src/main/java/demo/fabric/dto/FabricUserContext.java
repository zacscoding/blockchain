package demo.fabric.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

/**
 * Fabric User impl
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricUserContext implements User {

    public static final String DEFAULT_USER_NAME = "FabricUser";

    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private String mspId;
    private String password;
    private FabricOrgType orgType;

    // register 요청 후 Set 해야 함
    private String enrollmentSecret;
    // enrollment 요청 후 Set
    private Enrollment enrollment;
    // admin 여부
    private boolean isAdmin = false;

    /**
     * 기본 이름 + enrollment 용 User 생성
     */
    public static FabricUserContext newInstance(Enrollment enrollment) {
        return newInstance(DEFAULT_USER_NAME, enrollment);
    }

    /**
     * 이름 + enrollment를 가진 User 인스턴스 생성
     */
    public static FabricUserContext newInstance(String name, Enrollment enrollment) {
        return builder()
                .name(name)
                .enrollment(enrollment)
                .build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }
}
