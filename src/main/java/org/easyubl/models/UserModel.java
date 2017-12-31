package org.easyubl.models;

import java.util.Set;

public interface UserModel {

    String USERNAME = "username";

    String getId();

    String getIdentityID();

    String getProviderType();

    String getUsername();

    String getFullName();

    void setFullName(String fullName);

    boolean isRegistrationCompleted();

    void setRegistrationCompleted(boolean registrationCompleted);

    String getImageURL();

    void setImageURL(String imageURL);

    String getBio();

    void setBio(String bio);

    String getEmail();

    void setEmail(String email);

    String getUrl();

    void setUrl(String url);

    String getLanguage();

    void setLanguage(String language);

    Set<CompanyModel> getOwnedSpaces();

    Set<CompanyModel> getCollaboratedSpaces();

    Set<CompanyModel> getAllPermittedSpaces();

}
