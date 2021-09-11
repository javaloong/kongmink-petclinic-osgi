package org.javaloong.kongmink.petclinic.visits.internal.service;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.javaloong.kongmink.petclinic.visits.model.Visit;
import org.javaloong.kongmink.petclinic.visits.service.VisitService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;

@Component(service = VisitService.class, immediate = true)
public class VisitServiceImpl implements VisitService {

    private JpaTemplate jpaTemplate;

    @Override
    public void saveVisit(Visit visit) {
        jpaTemplate.tx(TransactionType.Required, em -> {
            if (visit.getId() == null) {
                em.persist(visit);
            } else {
                em.merge(visit);
                em.flush();
            }
        });
    }

    @Override
    public void deleteVisit(Visit visit) {
        jpaTemplate.tx(TransactionType.Required, em ->
                em.remove(em.contains(visit) ? visit : em.merge(visit)));
    }

    @Override
    public Visit findVisitById(int id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                em -> em.find(Visit.class, id));
    }

    @Override
    public Collection<Visit> findAllVisits() {
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            CriteriaQuery<Visit> query = em.getCriteriaBuilder().createQuery(Visit.class);
            return em.createQuery(query.select(query.from(Visit.class))).getResultList();
        });
    }

    @Reference(target = "(osgi.unit.name=visits)")
    public void setJpaTemplate(JpaTemplate jpaTemplate) {
        this.jpaTemplate = jpaTemplate;
    }
}
