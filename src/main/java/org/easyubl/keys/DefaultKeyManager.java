package org.easyubl.keys;

import org.easyubl.components.ComponentModel;
import org.easyubl.components.ComponentProvider;
import org.easyubl.components.utils.ComponentProviderLiteral;
import org.easyubl.components.utils.ComponentUtil;
import org.easyubl.components.utils.RsaKeyProviderLiteral;
import org.easyubl.jws.AlgorithmType;
import org.easyubl.keys.qualifiers.RsaKeyType;
import org.easyubl.models.CompanyModel;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Stateless
public class DefaultKeyManager implements KeyManager {

    private static final Logger logger = Logger.getLogger(DefaultKeyManager.class);

    @Inject
    @Any
    private Instance<KeyProviderFactory> keyProviderFactories;

    @Inject
    private ComponentProvider componentProvider;

    @Inject
    private ComponentUtil componentUtil;

    @Inject
    @Any
    private Instance<KeyProviderFactory> getKeyProviderFactories;

    @Override
    public ActiveRsaKey getActiveRsaKey(CompanyModel company) {
        for (KeyProvider p : getProviders(company)) {
            if (p.getType().equals(AlgorithmType.RSA)) {
                RsaKeyProvider r = (RsaKeyProvider) p;
                if (r.getKid() != null && r.getPrivateKey() != null) {
                    if (logger.isTraceEnabled()) {
                        logger.tracev("Active key organization={0} kid={1}", company.getName(), p.getKid());
                    }
                    String kid = p.getKid();
                    return new ActiveRsaKey(kid, r.getPrivateKey(), r.getPublicKey(kid), r.getCertificate(kid));
                }
            }
        }
        throw new RuntimeException("Failed to get RSA keys");
    }

    @Override
    public PublicKey getRsaPublicKey(CompanyModel realm, String kid) {
        if (kid == null) {
            logger.warnv("KID is null, can't find public key", realm.getName(), kid);
            return null;
        }

        for (KeyProvider p : getProviders(realm)) {
            if (p.getType().equals(AlgorithmType.RSA)) {
                RsaKeyProvider r = (RsaKeyProvider) p;
                PublicKey publicKey = r.getPublicKey(kid);
                if (publicKey != null) {
                    if (logger.isTraceEnabled()) {
                        logger.tracev("Found public key realm={0} kid={1}", realm.getName(), kid);
                    }
                    return publicKey;
                }
            }
        }
        if (logger.isTraceEnabled()) {
            logger.tracev("Failed to find public key realm={0} kid={1}", realm.getName(), kid);
        }
        return null;
    }

    @Override
    public Certificate getRsaCertificate(CompanyModel realm, String kid) {
        if (kid == null) {
            logger.warnv("KID is null, can't find public key", realm.getName(), kid);
            return null;
        }

        for (KeyProvider p : getProviders(realm)) {
            if (p.getType().equals(AlgorithmType.RSA)) {
                RsaKeyProvider r = (RsaKeyProvider) p;
                Certificate certificate = r.getCertificate(kid);
                if (certificate != null) {
                    if (logger.isTraceEnabled()) {
                        logger.tracev("Found certificate realm={0} kid={1}", realm.getName(), kid);
                    }
                    return certificate;
                }
            }
        }
        if (logger.isTraceEnabled()) {
            logger.tracev("Failed to find certificate realm={0} kid={1}", realm.getName(), kid);
        }
        return null;
    }

    @Override
    public List<RsaKeyMetadata> getRsaKeys(CompanyModel organization, boolean includeDisabled) {
        List<RsaKeyMetadata> keys = new LinkedList<>();
        for (KeyProvider p : getProviders(organization)) {
            if (p instanceof RsaKeyProvider) {
                if (includeDisabled) {
                    keys.addAll(p.getKeyMetadata());
                } else {
                    List<RsaKeyMetadata> metadata = p.getKeyMetadata();
                    metadata.stream().filter(k -> k.getStatus() != KeyMetadata.Status.DISABLED).forEach(k -> keys.add(k));
                }
            }
        }
        return keys;
    }

    private List<KeyProvider> getProviders(CompanyModel company) {
        List<KeyProvider> providers = new LinkedList<>();

        List<ComponentModel> components = new LinkedList<>(componentProvider.getComponents(company, company.getId(), KeyProvider.class.getName()));
        components.sort(new ProviderComparator());

        boolean activeRsa = false;

        for (ComponentModel c : components) {
            try {

                Optional<RsaKeyType> op = RsaKeyType.findByProviderId(c.getProviderId());
                if (!op.isPresent()) {
                    return null;
                }
                Annotation componentProviderLiteral = new ComponentProviderLiteral(KeyProvider.class);
                Annotation rsaKeyProviderLiteral = new RsaKeyProviderLiteral(op.get());

                KeyProviderFactory factory = getKeyProviderFactories.select(componentProviderLiteral, rsaKeyProviderLiteral).get();
                KeyProvider provider = factory.create(company, c);
                providers.add(provider);
                if (provider.getType().equals(AlgorithmType.RSA)) {
                    RsaKeyProvider r = (RsaKeyProvider) provider;
                    if (r.getKid() != null && r.getPrivateKey() != null) {
                        activeRsa = true;
                    }
                }
            } catch (Throwable t) {
                logger.errorv(t, "Failed to load provider {0}", c.getId());
            }
        }

        if (!activeRsa) {
            providers.add(new FailsafeRsaKeyProvider());
        }

        return providers;
    }

    private class ProviderComparator implements Comparator<ComponentModel> {
        @Override
        public int compare(ComponentModel o1, ComponentModel o2) {
            int i = Long.compare(o2.get("priority", 0l), o1.get("priority", 0l));
            return i != 0 ? i : o1.getId().compareTo(o2.getId());
        }
    }
}
