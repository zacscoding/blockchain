package demo.fabric.util;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;

/**
 *
 */
public class FabricCertParser {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Pem private key bytes -> PrivateKey 인스턴스 변환
     */
    public static PrivateKey getPrivateKeyFromPemBytes(byte[] pemKey) throws CryptoException {
        PrivateKey pk = null;

        try {
            PemReader pr = new PemReader(new StringReader(new String(pemKey)));
            PemObject po = pr.readPemObject();
            PEMParser pem = new PEMParser(new StringReader(new String(pemKey)));

            if (po.getType().equals("PRIVATE KEY")) {
                pk = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) pem.readObject());
            } else {
                PEMKeyPair kp = (PEMKeyPair) pem.readObject();
                pk = new JcaPEMKeyConverter().getPrivateKey(kp.getPrivateKeyInfo());
            }
        } catch (Exception e) {
            throw new CryptoException("Failed to convert private key bytes", e);
        }

        return pk;
    }

    /**
     * private key pem bytes + cert bytes -> Enrollment 인스턴스 반환
     */
    public static Enrollment x509EnrollmentOf(byte[] privateKeyPem, byte[] cert) throws CryptoException {
        return new X509Enrollment(
            getPrivateKeyFromPemBytes(privateKeyPem),
            new String(cert)
        );
    }

    private FabricCertParser() {
    }
}
