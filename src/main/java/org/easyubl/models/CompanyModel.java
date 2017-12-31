package org.easyubl.models;

import java.util.List;
import java.util.Set;

public interface CompanyModel {

    String NAME = "name";

    String getId();

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    UserModel getOwner();

    void setOwner(UserModel user);

    List<UserModel> getCollaborators();

    List<UserModel> getCollaborators(int offset, int limit);

    CollaboratorModel addCollaborators(UserModel user, Set<CollaboratorPermissionType> permissions);

    boolean removeCollaborator(UserModel user);
}
