package org.easyubl.models;

import java.util.Set;

public interface CollaboratorModel {

    UserModel getUser();

    CompanyModel getCompany();

    Set<CollaboratorPermissionType> getPermissions();
}
