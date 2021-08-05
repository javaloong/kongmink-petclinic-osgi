package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.List;

public class PetRepositoryImpl implements PetRepository {

    public static PetRepositoryImpl newInstance(EntityManager em) {
        PetRepositoryImpl instance = new PetRepositoryImpl();
        instance.em = em;
        return instance;
    }

    @PersistenceContext(unitName = "customers")
    private EntityManager em;

    @Override
    public void save(Pet pet) {
        em.persist(pet);
        em.flush();
    }

    @Override
    public void delete(Pet pet) {
        em.remove(pet);
    }

    @Override
    public List<PetType> findPetTypes() {
        return em.createQuery("SELECT ptype FROM PetType ptype ORDER BY ptype.name",
                PetType.class).getResultList();
    }

    @Override
    public Pet findById(int id) {
        return em.find(Pet.class, id);
    }

    @Override
    public Collection<Pet> findAll() {
        CriteriaQuery<Pet> query = em.getCriteriaBuilder().createQuery(Pet.class);
        return em.createQuery(query.select(query.from(Pet.class))).getResultList();
    }
}
