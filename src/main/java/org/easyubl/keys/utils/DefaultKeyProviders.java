package org.easyubl.keys.utils;

import org.easyubl.common.exceptions.ModelException;
import org.easyubl.common.util.MultivaluedHashMap;
import org.easyubl.components.ComponentModel;
import org.easyubl.components.ComponentProvider;
import org.easyubl.keys.KeyProvider;
import org.easyubl.models.CompanyModel;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DefaultKeyProviders {

    @Inject
    private ComponentProvider componentProvider;

    public void createProviders(CompanyModel organization) throws ModelException {
        ComponentModel generated = new ComponentModel();
        generated.setName("rsa-generated");
        generated.setParentId(organization.getId());
        generated.setProviderId("rsa-generated");
        generated.setProviderType(KeyProvider.class.getName());

        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.putSingle("priority", "100");
        generated.setConfig(config);

        componentProvider.addComponentModel(organization, generated);
    }

    public void createProviders(CompanyModel organization, String privateKeyPem, String certificatePem) throws ModelException {
        ComponentModel rsa = new ComponentModel();
        rsa.setName("rsa");
        rsa.setParentId(organization.getId());
        rsa.setProviderId("rsa");
        rsa.setProviderType(KeyProvider.class.getName());

        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.putSingle("priority", "100");
        config.putSingle("privateKey", privateKeyPem);
        if (certificatePem != null) {
            config.putSingle("certificate", certificatePem);
        }
        rsa.setConfig(config);

        componentProvider.addComponentModel(organization, rsa);
    }

}
