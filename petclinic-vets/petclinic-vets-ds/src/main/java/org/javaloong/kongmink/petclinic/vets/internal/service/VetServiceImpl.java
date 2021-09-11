/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
