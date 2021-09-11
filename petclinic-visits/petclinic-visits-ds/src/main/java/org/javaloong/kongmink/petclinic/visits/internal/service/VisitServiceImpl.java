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
