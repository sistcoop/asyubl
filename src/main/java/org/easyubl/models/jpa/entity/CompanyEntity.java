package org.easyubl.models.jpa.entity;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Audited
@Entity
@Table(name = "ea_company")
@NamedQueries({
        @NamedQuery(name = "getAllCompanies", query = "select c from CompanyEntity c order by c.name"),
        @NamedQuery(name = "getCompaniesByUserId", query = "select s from CompanyEntity s inner join s.owner o where o.id = :userId")
})
public class CompanyEntity implements Serializable {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = "id", length = 36)
    private String id;

    @NaturalId(mutable = true)
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean enabled;

    @Column(name = "email_theme")
    private String emailTheme;

    @Column(name = "report_theme")
    private String reportTheme;

    @Column(name = "logo_file_id")
    private String logoFileId;

    @Column(name = "logo_file_provider")
    private String logoFileProvider;

    @ElementCollection
    @Column(name = "value")
    @CollectionTable(name = "company_supported_currencies", joinColumns = {@JoinColumn(name = "company_id")})
    private Set<String> supportedCurrencies = new HashSet<>();

    @Column(name = "default_currency")
    private String defaultCurrency;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "company_smtp_config", joinColumns = {@JoinColumn(name = "company_id")})
    private Map<String, String> smtpConfig = new HashMap<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey)
    private UserEntity owner;

    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
    private Set<CollaboratorEntity> collaborators = new HashSet<>();

    @Version
    @Column(name = "version")
    private int version;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmailTheme() {
        return emailTheme;
    }

    public void setEmailTheme(String emailTheme) {
        this.emailTheme = emailTheme;
    }

    public String getReportTheme() {
        return reportTheme;
    }

    public void setReportTheme(String reportTheme) {
        this.reportTheme = reportTheme;
    }

    public String getLogoFileId() {
        return logoFileId;
    }

    public void setLogoFileId(String logoFileId) {
        this.logoFileId = logoFileId;
    }

    public String getLogoFileProvider() {
        return logoFileProvider;
    }

    public void setLogoFileProvider(String logoFileProvider) {
        this.logoFileProvider = logoFileProvider;
    }

    public Set<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public void setSupportedCurrencies(Set<String> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Map<String, String> getSmtpConfig() {
        return smtpConfig;
    }

    public void setSmtpConfig(Map<String, String> smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Set<CollaboratorEntity> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<CollaboratorEntity> collaborators) {
        this.collaborators = collaborators;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}