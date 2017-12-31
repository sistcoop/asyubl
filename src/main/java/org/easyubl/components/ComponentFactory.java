package org.easyubl.components;

import org.easyubl.models.CompanyModel;
import org.easyubl.provider.ConfiguredProvider;
import org.easyubl.provider.ProviderConfigProperty;
import org.easyubl.provider.ProviderFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ComponentFactory<CreatedType, ProviderType> extends ProviderFactory<ProviderType>, ConfiguredProvider {

    CreatedType create(CompanyModel company, ComponentModel model);

    @Override
    default ProviderType create() {
        return null;
    }

    default void validateConfiguration(CompanyModel company, ComponentModel model) throws ComponentValidationException {
    }

    default void onCreate(CompanyModel company, ComponentModel model) {
    }


    default void onUpdate(CompanyModel company, ComponentModel model) {
    }

    /**
     * These are config properties that are common across all implementation of this component type
     *
     * @return
     */
    default List<ProviderConfigProperty> getCommonProviderConfigProperties() {
        return Collections.EMPTY_LIST;
    }

    /**
     * This is metadata about this component type.  Its really configuration information about the component type and not
     * an individual instance
     *
     * @return
     */
    default Map<String, Object> getTypeMetadata() {
        return Collections.EMPTY_MAP;
    }


}
