package org.easyubl.keys;

import org.easyubl.components.ComponentModel;
import org.easyubl.components.ComponentValidationException;
import org.easyubl.models.CompanyModel;
import org.easyubl.provider.ConfigurationValidationHelper;
import org.easyubl.provider.ProviderConfigurationBuilder;

public abstract class AbstractRsaKeyProviderFactory {

    public final static ProviderConfigurationBuilder configurationBuilder() {
        return ProviderConfigurationBuilder.create()
                .property(Attributes.PRIORITY_PROPERTY)
                .property(Attributes.ENABLED_PROPERTY)
                .property(Attributes.ACTIVE_PROPERTY);
    }

    public void validateConfiguration(CompanyModel company, ComponentModel model) throws ComponentValidationException {
        ConfigurationValidationHelper.check(model)
                .checkLong(Attributes.PRIORITY_PROPERTY, false)
                .checkBoolean(Attributes.ENABLED_PROPERTY, false)
                .checkBoolean(Attributes.ACTIVE_PROPERTY, false);
    }
}
