package org.easyubl.models.jpa.entity;

import org.easyubl.models.CollaboratorPermissionType;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "ea_collaborator")
@IdClass(CollaboratorEntity.Key.class)
@NamedQueries({
        @NamedQuery(name = "getCollaboratorsBySpaceId", query = "select c from CollaboratorEntity c inner join c.space s where s.id = :spaceId")
})
public class CollaboratorEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @NotNull
    @ElementCollection(targetClass = CollaboratorPermissionType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "value")
    @CollectionTable(name = "permissions", joinColumns = {@JoinColumn(name = "user_id"), @JoinColumn(name = "company_id")})
    private Set<CollaboratorPermissionType> permissions = new HashSet<>();

    public CollaboratorEntity() {
    }

    public CollaboratorEntity(UserEntity user, CompanyEntity company) {
        this.user = user;
        this.company = company;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public void setCompany(CompanyEntity company) {
        this.company = company;
    }

    public Set<CollaboratorPermissionType> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<CollaboratorPermissionType> permissions) {
        this.permissions = permissions;
    }

    public static class Key implements Serializable {

        private UserEntity user;
        private CompanyEntity company;

        public Key() {
        }

        public Key(UserEntity user, CompanyEntity company) {
            this.user = user;
            this.company = company;
        }

        public UserEntity getUser() {
            return user;
        }

        public CompanyEntity getCompany() {
            return company;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            if (!getUser().equals(key.getUser())) return false;
            return getCompany().equals(key.getCompany());
        }

        @Override
        public int hashCode() {
            int result = getUser().hashCode();
            result = 31 * result + getCompany().hashCode();
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollaboratorEntity that = (CollaboratorEntity) o;
        if (!getUser().equals(that.getUser())) return false;
        return getCompany().equals(that.getCompany());
    }

    @Override
    public int hashCode() {
        int result = getUser().hashCode();
        result = 31 * result + getCompany().hashCode();
        return result;
    }

}
