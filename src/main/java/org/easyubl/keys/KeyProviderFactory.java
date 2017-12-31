package org.easyubl.keys;

import org.easyubl.components.ComponentFactory;
import org.easyubl.components.ComponentModel;
import org.easyubl.models.CompanyModel;

public interface KeyProviderFactory<T extends KeyProvider> extends ComponentFactory<T, KeyProvider> {

    T create(CompanyModel company, ComponentModel model);

}
