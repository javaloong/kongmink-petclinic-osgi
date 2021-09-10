/*
 * Copyright 2012-2019 the original author or authors.
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
package org.javaloong.kongmink.petclinic.customers.internal.service;

import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.service.OwnerService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;

@Component(service = OwnerService.class, immediate = true)
public class OwnerServiceImpl implements OwnerService {

    private JpaTemplate jpaTemplate;

    @Override
    public void saveOwner(Owner owner) {
        jpaTemplate.tx(TransactionType.Required, em -> {
            if (owner.getId() == null) {
                em.persist(owner);
            } else {
                em.merge(owner);
                em.flush();
            }
        });
    }

    @Override
    public void deleteOwner(Owner owner) {
        jpaTemplate.tx(TransactionType.Required, em ->
                em.remove(em.contains(owner) ? owner : em.merge(owner)));
    }

    @Override
    public Owner findOwnerById(int id) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                em -> em.find(Owner.class, id));
    }

    @Override
    public Collection<Owner> findAllOwners() {
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            CriteriaQuery<Owner> query = em.getCriteriaBuilder().createQuery(Owner.class);
            return em.createQuery(query.select(query.from(Owner.class))).getResultList();
        });
    }

    @Override
    public Collection<Owner> findAllOwnersByLastName(String lastName) {
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Owner> query = em.getCriteriaBuilder().createQuery(Owner.class);
            Root<Owner> root = query.from(Owner.class);
            return em.createQuery(query.select(root)
                    .where(cb.like(root.get("lastName"), lastName + "%"))).getResultList();
        });
    }

    @Reference(target = "(osgi.unit.name=customers)")
    public void setJpaTemplate(JpaTemplate jpaTemplate) {
        this.jpaTemplate = jpaTemplate;
    }
}
