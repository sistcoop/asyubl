package org.easyubl.keys;

import org.easyubl.models.CompanyModel;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

public interface KeyManager {

    ActiveRsaKey getActiveRsaKey(CompanyModel company);

    PublicKey getRsaPublicKey(CompanyModel company, String kid);

    Certificate getRsaCertificate(CompanyModel company, String kid);

    List<RsaKeyMetadata> getRsaKeys(CompanyModel company, boolean includeDisabled);

    class ActiveRsaKey {
        private final String kid;
        private final PrivateKey privateKey;
        private final PublicKey publicKey;
        private final X509Certificate certificate;

        public ActiveRsaKey(String kid, PrivateKey privateKey, PublicKey publicKey, X509Certificate certificate) {
            this.kid = kid;
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.certificate = certificate;
        }

        public String getKid() {
            return kid;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }
    }

}
