package org.easyubl.models.jpa;

import org.easyubl.common.jpa.JpaModel;
import org.easyubl.models.CompanyModel;
import org.easyubl.models.UserModel;
import org.easyubl.models.jpa.entity.CollaboratorEntity;
import org.easyubl.models.jpa.entity.CompanyEntity;
import org.easyubl.models.jpa.entity.UserEntity;

import javax.persistence.EntityManager;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserAdapter implements UserModel, JpaModel<UserEntity> {

    private final EntityManager em;
    private final UserEntity user;

    public UserAdapter(EntityManager em, UserEntity user) {
        this.em = em;
        this.user = user;
    }

    public static UserEntity toEntity(UserModel model, EntityManager em) {
        if (model instanceof UserAdapter) {
            return ((UserAdapter) model).getEntity();
        }
        return em.getReference(UserEntity.class, model.getId());
    }

    @Override
    public UserEntity getEntity() {
        return user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getIdentityID() {
        return user.getIdentityID();
    }

    @Override
    public String getProviderType() {
        return user.getProviderType();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getImageURL() {
        return user.getImageURL();
    }

    @Override
    public void setImageURL(String imageURL) {
        user.setImageURL(imageURL);
    }

    @Override
    public String getBio() {
        return user.getBio();
    }

    @Override
    public void setBio(String bio) {
        user.setBio(bio);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    public String getUrl() {
        return user.getUrl();
    }

    @Override
    public void setUrl(String url) {
        user.setUrl(url);
    }

    @Override
    public String getLanguage() {
        return user.getLanguage();
    }

    @Override
    public void setLanguage(String language) {
        user.setLanguage(language);
    }

    @Override
    public boolean isRegistrationCompleted() {
        return user.isRegistrationCompleted();
    }

    @Override
    public void setRegistrationCompleted(boolean registrationCompleted) {
        user.setRegistrationCompleted(registrationCompleted);
    }

    @Override
    public String getFullName() {
        return user.getFullName();
    }

    @Override
    public void setFullName(String fullName) {
        user.setFullName(fullName);
    }

    @Override
    public Set<CompanyModel> getOwnedSpaces() {
        return user.getOwnedCompanies().stream()
                .map(f -> new CompanyAdapter(em, f))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CompanyModel> getCollaboratedSpaces() {
        return user.getCollaboratedCopanies().stream()
                .map(f -> new CompanyAdapter(em, f.getCompany()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<CompanyModel> getAllPermittedSpaces() {
        Set<CompanyEntity> ownedSpaces = user.getOwnedCompanies();
        Set<CollaboratorEntity> collaboratedSpaces = user.getCollaboratedCopanies();
        return Stream.concat(ownedSpaces.stream(), collaboratedSpaces.stream().map(CollaboratorEntity::getCompany))
                .map(f -> new CompanyAdapter(em, f))
                .collect(Collectors.toSet());
    }

}
