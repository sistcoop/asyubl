package org.easyubl.models.jpa.entity;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "ea_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
}, indexes = {
        @Index(columnList = "username", unique = true)
})
@NamedQueries({
        @NamedQuery(name = "getAllUsers", query = "select u from UserEntity u order by u.username"),
        @NamedQuery(name = "getUserByUsername", query = "select u from UserEntity u where u.username = :username"),
        @NamedQuery(name = "getUserByIdentityID", query = "select u from UserEntity u where u.identityID = :identityID"),
})
public class UserEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @Column(name = "identity_id")
    private String identityID;

    @NotNull
    @Column(name = "provider_type")
    private String providerType;

    @NotNull
    @Column(name = "username")
    private String username;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageURL;

    @Size(max = 255)
    @Column(name = "bio")
    private String bio;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "url")
    private String url;

    @Column(name = "language")
    private String language;

    @NotNull
    @Type(type = "org.hibernate.type.TrueFalseType")
    @Column(name = "registration_complete")
    private boolean registrationCompleted;

    @NotNull
    @Column(name = "max_number_of_companies")
    private int maxNumberOfCompanies;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<CompanyEntity> ownedCompanies = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CollaboratorEntity> collaboratedCopanies = new HashSet<>();

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "favorite_companies", joinColumns = {@JoinColumn(name = "user_id")})
    private Set<String> favoriteCompanies = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isRegistrationCompleted() {
        return registrationCompleted;
    }

    public void setRegistrationCompleted(boolean registrationCompleted) {
        this.registrationCompleted = registrationCompleted;
    }

    public int getMaxNumberOfCompanies() {
        return maxNumberOfCompanies;
    }

    public void setMaxNumberOfCompanies(int maxNumberOfCompanies) {
        this.maxNumberOfCompanies = maxNumberOfCompanies;
    }

    public Set<CompanyEntity> getOwnedCompanies() {
        return ownedCompanies;
    }

    public void setOwnedCompanies(Set<CompanyEntity> ownedCompanies) {
        this.ownedCompanies = ownedCompanies;
    }

    public Set<CollaboratorEntity> getCollaboratedCopanies() {
        return collaboratedCopanies;
    }

    public void setCollaboratedCopanies(Set<CollaboratorEntity> collaboratedCopanies) {
        this.collaboratedCopanies = collaboratedCopanies;
    }

    public Set<String> getFavoriteCompanies() {
        return favoriteCompanies;
    }

    public void setFavoriteCompanies(Set<String> favoriteCompanies) {
        this.favoriteCompanies = favoriteCompanies;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
