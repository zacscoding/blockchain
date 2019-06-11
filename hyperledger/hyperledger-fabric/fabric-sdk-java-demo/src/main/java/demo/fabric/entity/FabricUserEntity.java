package demo.fabric.entity;

import demo.fabric.repository.converter.ObjectSerializeConverter;
import demo.fabric.repository.converter.SetAttributeConverter;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hyperledger.fabric.sdk.Enrollment;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "FABRIC_USER")
public class FabricUserEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Convert(converter = SetAttributeConverter.class)
    @Column(name = "ROLES")
    private Set<String> roles = new HashSet<>();

    @Column(name = "ACCOUNT")
    private String account;

    @Column(name = "AFFILIATION")
    private String affiliation;

    @Column(name = "ORGANIZATION")
    private String organization;

    @Column(name = "ENROLLMENT_SECRET")
    private String enrollmentSecret;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ENROLLMENT")
    @Convert(converter = ObjectSerializeConverter.class)
    private Enrollment enrollment;

    /**
     * register 여부 체크
     */
    public boolean isRegistered() {
        return StringUtils.hasText(enrollmentSecret);
    }

    /**
     * enrollment 여부 체크
     */
    public boolean isEnrolled() {
        return enrollment != null;
    }
}
