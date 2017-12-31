package org.easyubl.keys;

import org.easyubl.components.ComponentModel;
import org.easyubl.models.CompanyModel;
import org.keycloak.common.util.KeyUtils;
import org.keycloak.common.util.PemUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class ImportedRsaKeyProvider extends AbstractRsaKeyProvider {

    public ImportedRsaKeyProvider(CompanyModel company, ComponentModel model) {
        super(company, model);
    }

    @Override
    public Keys loadKeys(CompanyModel company, ComponentModel model) {
        String privateRsaKeyPem = model.getConfig().getFirst(Attributes.PRIVATE_KEY_KEY);
        String certificatePem = model.getConfig().getFirst(Attributes.CERTIFICATE_KEY);

        PrivateKey privateKey = PemUtils.decodePrivateKey(privateRsaKeyPem);
        PublicKey publicKey = KeyUtils.extractPublicKey(privateKey);

        KeyPair keyPair = new KeyPair(publicKey, privateKey);
        X509Certificate certificate = PemUtils.decodeCertificate(certificatePem);

        String kid = KeyUtils.createKeyId(keyPair.getPublic());

        return new Keys(kid, keyPair, certificate);
    }

}
