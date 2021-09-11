package org.javaloong.kongmink.petclinic.vets.internal.service;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.javaloong.kongmink.petclinic.vets.model.Vet;
import org.javaloong.kongmink.petclinic.vets.service.VetService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;

@Component(service = VetService.class, immediate = true)
public class VetServiceImpl implements VetService {

    private JpaTemplate jpaTemplate;

    @Override
    public void saveVet(Vet vet) {
        jpaTemplate.tx(TransactionType.Required, em -> {
            if (vet.getId() == null) {
                em.persist(vet);
            } else {
                em.merge(vet);
                em.flush();
            }
        });
    }

    @Override
    public void deleteVet(Vet vet) {
        jpaTemplate.tx(TransactionType.Required, em ->
                em.remove(em.contains(vet) ? vet : em.merge(vet)));
    }

    @Override
    public Vet findVetById(int id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                em -> em.find(Vet.class, id));
    }

    @Override
    public Collection<Vet> findAllVets() {
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            CriteriaQuery<Vet> query = em.getCriteriaBuilder().createQuery(Vet.class);
            return em.createQuery(query.select(query.from(Vet.class))).getResultList();
        });
    }

    @Reference(target = "(osgi.unit.name=vets)")
    public void setJpaTemplate(JpaTemplate jpaTemplate) {
        this.jpaTemplate = jpaTemplate;
    }
}
