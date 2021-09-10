package org.javaloong.kongmink.petclinic.customers.internal.service;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.javaloong.kongmink.petclinic.customers.service.PetService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.List;

@Component(service = PetService.class, immediate = true)
public class PetServiceImpl implements PetService {

    private JpaTemplate jpaTemplate;

    @Override
    public void savePet(Pet pet) {
        jpaTemplate.tx(TransactionType.Required, em -> {
            if (pet.getId() == null) {
                em.persist(pet);
            } else {
                em.merge(pet);
                em.flush();
            }
        });
    }

    @Override
    public void deletePet(Pet pet) {
        jpaTemplate.tx(TransactionType.Required, em ->
                em.remove(em.contains(pet) ? pet : em.merge(pet)));
    }

    @Override
    public List<PetType> findPetTypes() {
        return jpaTemplate.txExpr(TransactionType.Supports,
                em -> em.createQuery("SELECT ptype FROM PetType ptype ORDER BY ptype.name",
                        PetType.class).getResultList());
    }

    @Override
    public Pet findPetById(int id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                em -> em.find(Pet.class, id));
    }

    @Override
    public Collection<Pet> findAllPets() {
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            CriteriaQuery<Pet> query = em.getCriteriaBuilder().createQuery(Pet.class);
            return em.createQuery(query.select(query.from(Pet.class))).getResultList();
        });
    }

    @Reference(target = "(osgi.unit.name=customers)")
    public void setJpaTemplate(JpaTemplate jpaTemplate) {
        this.jpaTemplate = jpaTemplate;
    }
}
