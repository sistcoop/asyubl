package org.easyubl.models.jpa;

import org.easyubl.models.CompanyModel;
import org.easyubl.models.CompanyProvider;
import org.easyubl.models.UserModel;
import org.easyubl.models.jpa.entity.CompanyEntity;
import org.easyubl.models.jpa.entity.UserEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class JpaCompanyProvider implements CompanyProvider {

    private final static String[] SEARCH_FIELDS = {"assignedId", "name"};

    private EntityManager em;

    @Inject
    public JpaCompanyProvider(EntityManager em) {
        this.em = em;
    }

    @Override
    public CompanyModel addCompany(String name, UserModel owner) {
        UserEntity userEntity = UserAdapter.toEntity(owner, em);

        CompanyEntity entity = new CompanyEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setName(name);
        entity.setOwner(userEntity);
        em.persist(entity);
        em.flush();

        // Cache
        userEntity.getOwnedCompanies().add(entity);

        return new CompanyAdapter(em, entity);
    }

    @Override
    public CompanyModel getCompany(String id) {
        CompanyEntity entity = em.find(CompanyEntity.class, id);
        if (entity == null) return null;
        return new CompanyAdapter(em, entity);
    }

    @Override
    public boolean removeCompany(CompanyModel company) {
        CompanyEntity entity = em.find(CompanyEntity.class, company.getId());
        if (entity == null) return false;
        em.remove(entity);
        em.flush();
        return true;
    }

    @Override
    public List<CompanyModel> getCompanies(UserModel user) {
        return getCompanies(user, -1, -1);
    }

    @Override
    public List<CompanyModel> getCompanies(UserModel user, int offset, int limit) {
        TypedQuery<CompanyEntity> query = em.createNamedQuery("getCompaniesByUserId", CompanyEntity.class);
        query.setParameter("userId", user.getId());

        if (offset != -1) query.setFirstResult(offset);
        if (limit != -1) query.setMaxResults(limit);

        return query.getResultList().stream()
                .map(f -> new CompanyAdapter(em, f))
                .collect(Collectors.toList());
    }
}
