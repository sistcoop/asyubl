package org.easyubl.components;

import org.easyubl.common.exceptions.ModelException;
import org.easyubl.models.CompanyModel;

import java.util.List;

public interface ComponentProvider {

	/**
     * Adds component model. Will call onCreate() method of ComponentFactory
     *
     * @param model
     * @return
     */
    ComponentModel addComponentModel(CompanyModel organization, ComponentModel model) throws ModelException;

    /**
     * Adds component model. Will NOT call onCreate() method of ComponentFactory
     *
     * @param model
     * @return
     */
    ComponentModel importComponentModel(CompanyModel organization, ComponentModel model) throws ModelException;

    void updateComponent(CompanyModel organization, ComponentModel component) throws ModelException;

    void removeComponent(CompanyModel organization, ComponentModel component);

    void removeComponents(CompanyModel organization, String parentId);

    List<ComponentModel> getComponents(CompanyModel organization, String parentId, String providerType);

    List<ComponentModel> getComponents(CompanyModel organization, String parentId);

    List<ComponentModel> getComponents(CompanyModel organization);

    ComponentModel getComponent(CompanyModel organization, String id);
    
}
