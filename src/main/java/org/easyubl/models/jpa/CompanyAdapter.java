package org.easyubl.models.jpa;

import org.easyubl.common.jpa.JpaModel;
import org.easyubl.models.CollaboratorModel;
import org.easyubl.models.CollaboratorPermissionType;
import org.easyubl.models.CompanyModel;
import org.easyubl.models.UserModel;
import org.easyubl.models.jpa.entity.CollaboratorEntity;
import org.easyubl.models.jpa.entity.CompanyEntity;
import org.easyubl.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompanyAdapter implements CompanyModel, JpaModel<CompanyEntity> {

    private final EntityManager em;
    private final CompanyEntity company;

    public CompanyAdapter(EntityManager em, CompanyEntity company) {
        this.em = em;
        this.company = company;
    }

    public static CompanyEntity toEntity(CompanyModel model, EntityManager em) {
        if (model instanceof CompanyAdapter) {
            return ((CompanyAdapter) model).getEntity();
        }
        return em.getReference(CompanyEntity.class, model.getId());
    }

    @Override
    public CompanyEntity getEntity() {
        return company;
    }

    @Override
    public String getId() {
        return company.getId();
    }

    @Override
    public String getName() {
        return company.getName();
    }

    @Override
    public void setName(String name) {
        company.setName(name);
    }

    @Override
    public String getDescription() {
        return company.getDescription();
    }

    @Override
    public void setDescription(String description) {
        company.setDescription(description);
    }

    @Override
    public UserModel getOwner() {
        UserEntity owner = company.getOwner();
        return owner != null ? new UserAdapter(em, owner) : null;
    }

    @Override
    public void setOwner(UserModel user) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);
        company.setOwner(userEntity);
    }

    @Override
    public List<UserModel> getCollaborators() {
        return company.getCollaborators().stream()
                .map(CollaboratorEntity::getUser)
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getCollaborators(int offset, int limit) {
        TypedQuery<CollaboratorEntity> query = em.createNamedQuery("getCollaboratorsBySpaceId", CollaboratorEntity.class);
        query.setParameter("spaceId", company.getId());

        if (offset != -1) query.setFirstResult(offset);
        if (limit != -1) query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(CollaboratorEntity::getUser)
                .map(f -> new UserAdapter(em, f))
                .collect(Collectors.toList());
    }

    @Override
    public CollaboratorModel addCollaborators(UserModel user, Set<CollaboratorPermissionType> permissions) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        CollaboratorEntity collaboratorEntity = new CollaboratorEntity(userEntity, company);
        em.persist(collaboratorEntity);

        // Cache
        company.getCollaborators().add(collaboratorEntity);

        return new CollaboratorAdapter(em, collaboratorEntity);
    }

    @Override
    public boolean removeCollaborator(UserModel user) {
        UserEntity userEntity = UserAdapter.toEntity(user, em);

        CollaboratorEntity entity = em.find(CollaboratorEntity.class, new CollaboratorEntity.Key(userEntity, company));
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

}
