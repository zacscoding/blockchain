package demo.basic;

import demo.common.TestHelper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.junit.Test;

/**
 *
 */
public class CertTests {

    /**
     * CA 자가서명 Cert -> Admin enrollment Cert 검증 테스트
     */
    @Test
    public void certCompare() throws Exception {
        String caCert = "-----BEGIN CERTIFICATE-----\n"
            + "MIIB5DCCAYugAwIBAgIUeOX775yG+laZG70/j1Q+X9z1Y0IwCgYIKoZIzj0EAwIw\n"
            + "TzELMAkGA1UEBhMCS1IxFDASBgNVBAoTC0h5cGVybGVkZ2VyMQ8wDQYDVQQLEwZG\n"
            + "YWJyaWMxGTAXBgNVBAMTEGZhYnJpYy1jYS1zZXJ2ZXIwHhcNMTkwNjE5MDUyNzAw\n"
            + "WhcNMzQwNjE1MDUyNzAwWjBPMQswCQYDVQQGEwJLUjEUMBIGA1UEChMLSHlwZXJs\n"
            + "ZWRnZXIxDzANBgNVBAsTBkZhYnJpYzEZMBcGA1UEAxMQZmFicmljLWNhLXNlcnZl\n"
            + "cjBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABCwxGxP1WviV6ClDRQEG8YgsWMhn\n"
            + "Jx5DhL+HQSODzu2hIJvlJg6fOSJt4odVLZunvwEq6RwpJSWioM1j8HJ7aK6jRTBD\n"
            + "MA4GA1UdDwEB/wQEAwIBBjASBgNVHRMBAf8ECDAGAQH/AgEBMB0GA1UdDgQWBBT4\n"
            + "PvWmY7goI9yPcfVArOSyevaywDAKBggqhkjOPQQDAgNHADBEAiA+aweua/fHhY9G\n"
            + "tztCQEy33VYMG0WAqe6VO+3xItnjAQIgU81ToOO8VCDqbHrMWiTNJMzBUucCffCE\n"
            + "Fab6jq9/Qno=\n"
            + "-----END CERTIFICATE-----";

        String adminSignedCert = "-----BEGIN CERTIFICATE-----\n"
            + "MIICcjCCAhmgAwIBAgIUSxd4tFP5v08cdmHaA5KOQhxlH5wwCgYIKoZIzj0EAwIw\n"
            + "TzELMAkGA1UEBhMCS1IxFDASBgNVBAoTC0h5cGVybGVkZ2VyMQ8wDQYDVQQLEwZG\n"
            + "YWJyaWMxGTAXBgNVBAMTEGZhYnJpYy1jYS1zZXJ2ZXIwHhcNMTkwNjE5MDUyODAw\n"
            + "WhcNMjAwNjE4MDUzMzAwWjBIMSMwDQYDVQQLEwZjbGllbnQwEgYDVQQLEwtPcmRl\n"
            + "cmVyT3JnMjEhMB8GA1UEAwwYYWRtaW5PcmRlcmVyQE9yZGVyZXJPcmcyMFkwEwYH\n"
            + "KoZIzj0CAQYIKoZIzj0DAQcDQgAETRkUjbz4btM5MVVkkGDimBdQCWHP8jM5rANM\n"
            + "YNsnxa03MHvSgaAtBtlzRKcK0k4oDJ3OTP+s7KCNx/YipnKXCqOB2TCB1jAOBgNV\n"
            + "HQ8BAf8EBAMCB4AwDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQU6ww+Z8wVfTJQmtaf\n"
            + "bDA3zp2/FY4wHwYDVR0jBBgwFoAU+D71pmO4KCPcj3H1QKzksnr2ssAwdgYIKgME\n"
            + "BQYHCAEEansiYXR0cnMiOnsiaGYuQWZmaWxpYXRpb24iOiJPcmRlcmVyT3JnMiIs\n"
            + "ImhmLkVucm9sbG1lbnRJRCI6ImFkbWluT3JkZXJlckBPcmRlcmVyT3JnMiIsImhm\n"
            + "LlR5cGUiOiJjbGllbnQifX0wCgYIKoZIzj0EAwIDRwAwRAIgdJKYX1yt0PyjuSj6\n"
            + "QwXDd0sU1wHLACxduMo4qzFxGHUCIBsg9rfqheeFkPSSJG0a/NWilmbLd254ahkE\n"
            + "Z3r6UakF\n"
            + "-----END CERTIFICATE-----";

        TestHelper.out("Compare result :: %s", isParent(caCert, adminSignedCert));
        TestHelper.out("Verify password %s --> %s", "rootcaadmin", isValidPassword(adminSignedCert, "rootcaadmin"));
        TestHelper.out("Verify password %s --> %s", "rootcaadmin22", isValidPassword(adminSignedCert, "rootcaadmin22"));
    }

    private boolean isValidPassword(String certString, String password) throws Exception {
        X509Certificate cert = getCert(certString.getBytes());
        X500Principal principal = cert.getSubjectX500Principal();
        //principal.getName()
        return false;
    }

    /**
     * Cert parent 체크
     */
    private boolean isParent(String parentCertString, String certString) throws Exception {
        X509Certificate parentCert = getCert(parentCertString.getBytes());
        X509Certificate cert = getCert(certString.getBytes());
        try {
            cert.verify(parentCert.getPublicKey());
            return true;
        } catch (Exception e) {
            TestHelper.out("Failed to verify :: %s", e.getMessage());
            return false;
        }
    }

    private X509Certificate getCert(byte[] certBytes) throws CertificateException {
        BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(certBytes));
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);
        return certificate;
    }
}
