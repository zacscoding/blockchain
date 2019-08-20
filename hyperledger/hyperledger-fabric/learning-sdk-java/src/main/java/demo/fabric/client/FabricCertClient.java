package demo.fabric.client;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation.HFCAAffiliationResp;
import org.hyperledger.fabric_ca.sdk.HFCACertificateRequest;
import org.hyperledger.fabric_ca.sdk.HFCACertificateResponse;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCACredential;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.HFCAX509Certificate;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.AffiliationException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.IdentityException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

import demo.fabric.dto.FabricUserContext;
import demo.fabric.util.FabricCertParser;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class FabricCertClient {

    /**
     * affiliation 생성
     */
    public boolean createNewAffiliation(HFCAClient caClient, Enrollment enrollment, String name)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        // 이미 존재하는 지 체크
        Optional<HFCAAffiliation> affOptional = getAffiliationByName(caClient, enrollment, name);

        if (affOptional.isPresent()) {
            logger.trace("Skip new affiliation because already exist {}", name);
            return true;
        }

        HFCAAffiliation hfcaAffiliation = caClient.newHFCAAffiliation(name);
        HFCAAffiliationResp response = hfcaAffiliation.create(FabricUserContext.newInstance(enrollment));

        return is2xxSuccessful(response);
    }

    /**
     * 해당 enrollment의 affiliation tree 조회
     */
    public Optional<HFCAAffiliation> getAffiliations(HFCAClient caClient, Enrollment enrollment)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        HFCAAffiliation affiliation = caClient.getHFCAAffiliations(FabricUserContext.newInstance(enrollment));

        return Optional.ofNullable(affiliation);
    }

    /**
     * 해당 enrollment의 affiliation + name 조회
     */
    public Optional<HFCAAffiliation> getAffiliationByName(HFCAClient caClient, Enrollment enrollment,
                                                          String name)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        HFCAAffiliation affiliation = caClient.getHFCAAffiliations(FabricUserContext.newInstance(enrollment));

        return Optional.ofNullable(searchAffiliationWithRecursively(affiliation, name));
    }

    /**
     * Affiliation 삭제
     */
    public void deleteAffiliation(HFCAClient caClient, Enrollment enrollment, String name)
            throws AffiliationException, InvalidArgumentException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        Optional<HFCAAffiliation> affOptional = getAffiliationByName(caClient, enrollment, name);

        if (!affOptional.isPresent()) {
            return;
        }

        HFCAAffiliation affiliation = affOptional.get();
        HFCAAffiliationResp response = affiliation.delete(FabricUserContext.newInstance(enrollment));
        if (!is2xxSuccessful(response)) {
            logger.warn("Failed to delete affiliation. status code :  {}", response.getStatusCode());
        }
    }

    /**
     * new identity 등록
     */
    public boolean registerNewIdentity(HFCAClient caClient, Enrollment enrollment, String type, String name,
                                       String password, String affiliation, List<Attribute> attributes)
            throws Exception {

        return registerNewIdentity(caClient, enrollment, type, name, password, affiliation, attributes, -1);
    }

    public boolean registerNewIdentity(HFCAClient caClient, Enrollment enrollment, String type, String name,
                                       String password, String affiliation, List<Attribute> attributes,
                                       int maxEnrollments) throws Exception {

        requireNonNull(caClient, "caClient");
        requireNonNull(enrollment, "enrollment");
        requireNonNull(password, "password");

        RegistrationRequest rr = new RegistrationRequest(name, affiliation);

        rr.setType(type);
        rr.setSecret(password);
        rr.setMaxEnrollments(maxEnrollments);

        if (attributes != null && !attributes.isEmpty()) {
            for (Attribute attribute : attributes) {
                rr.addAttribute(attribute);
            }
        }

        String secret = caClient.register(rr, FabricUserContext.newInstance(enrollment));
        return password.equals(secret);
    }

    public Optional<HFCAIdentity> getIdentityByName(HFCAClient caClient, Enrollment enrollment, String name)
            throws InvalidArgumentException, IdentityException {

        requireNonNull(caClient, "caClient");

        Collection<HFCAIdentity>
                identities = caClient.getHFCAIdentities(FabricUserContext.newInstance(enrollment));

        for (HFCAIdentity identity : identities) {
            if (identity.getEnrollmentId().equals(name)) {
                return Optional.of(identity);
            }
        }

        return Optional.empty();
    }

    public Enrollment enroll(HFCAClient caClient, String enrollmentId, String enrollmentSecret)
            throws EnrollmentException, InvalidArgumentException {

        return enroll(caClient, enrollmentId, enrollmentSecret, new EnrollmentRequest());
    }

    public Enrollment enroll(HFCAClient caClient, String enrollmentId, String enrollmentSecret,
                             EnrollmentRequest request) throws EnrollmentException, InvalidArgumentException {

        requireNonNull(caClient, "caClient");
        requireNonNull(request, "request");

        return caClient.enroll(enrollmentId, enrollmentSecret, request);
    }

    public List<HFCAX509Certificate> getCertificates(HFCAClient caClient, Enrollment enrollment,
                                                     HFCACertificateRequest requestFilter) throws Exception {

        requireNonNull(caClient, "caClient");

        if (requestFilter == null) {
            requestFilter = caClient.newHFCACertificateRequest();
        }

        HFCACertificateResponse response = caClient.getHFCACertificates(
                FabricUserContext.newInstance(enrollment), requestFilter);

        Collection<HFCACredential> certs = response.getCerts();
        if (certs == null || certs.isEmpty()) {
            return Collections.emptyList();
        }

        List<HFCAX509Certificate> x509Certificates = new ArrayList<>(certs.size());
        for (HFCACredential cert : certs) {
            x509Certificates.add((HFCAX509Certificate) cert);
        }

        return x509Certificates;
    }

    public List<HFCAX509Certificate> getCertificatesByCn(HFCAClient caClient, Enrollment enrollment, String cn)
            throws Exception {

        requireNonNull(caClient, "caClient");

        List<HFCAX509Certificate> certificates = getCertificates(caClient, enrollment, null);
        if (certificates.isEmpty()) {
            return Collections.emptyList();
        }

        List<HFCAX509Certificate> certs = new ArrayList<>();

        for (int i = 0; i < certificates.size(); i++) {
            HFCAX509Certificate certificate = certificates.get(i);
            String cnValue = FabricCertParser.getCnValue(certificate.getX509());

            if (cn.equals(cnValue)) {
                certs.add(certificate);
            }
        }

        return certs;
    }

    /**
     * HFCAAffiliationResp 성공 여부 반환
     *
     * @return true : 200 <= status code < 300 인 경우, false : 그 외
     */
    protected boolean is2xxSuccessful(HFCAAffiliationResp response) {
        if (response == null) {
            return false;
        }

        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }

    /**
     * HFCAAffiliation의 child를 순회하면서 name 값이 같은 Affiliation 인스턴스 조회
     *
     * @param affiliation : root affiliation
     * @param name        : 찾을 affiliation name
     * @return affiliation이 name 인 HFCAAffiliation or null
     */
    protected HFCAAffiliation searchAffiliationWithRecursively(HFCAAffiliation affiliation, String name)
            throws AffiliationException {
        if (name == null || name.length() == 0 || affiliation == null) {
            return null;
        }

        if (name.equals(affiliation.getName())) {
            return affiliation;
        }

        for (HFCAAffiliation child : affiliation.getChildren()) {
            HFCAAffiliation found = searchAffiliationWithRecursively(child, name);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    protected EnrollmentRequest createDefaultEnrollmentRequest() {
        EnrollmentRequest request = new EnrollmentRequest();
        return request;
    }
}
