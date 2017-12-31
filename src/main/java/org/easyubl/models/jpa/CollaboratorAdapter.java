package org.easyubl.models.jpa;

import org.easyubl.models.CollaboratorModel;
import org.easyubl.models.CollaboratorPermissionType;
import org.easyubl.models.CompanyModel;
import org.easyubl.models.UserModel;
import org.easyubl.models.jpa.entity.CollaboratorEntity;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

public class CollaboratorAdapter implements CollaboratorModel {

    private final EntityManager em;
    private final CollaboratorEntity collaborator;

    public CollaboratorAdapter(EntityManager em, CollaboratorEntity collaborator) {
        this.em = em;
        this.collaborator = collaborator;
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, collaborator.getUser());
    }

    @Override
    public CompanyModel getCompany() {
        return new CompanyAdapter(em, collaborator.getCompany());
    }

    @Override
    public Set<CollaboratorPermissionType> getPermissions() {
        return new HashSet<>(collaborator.getPermissions());
    }
}
