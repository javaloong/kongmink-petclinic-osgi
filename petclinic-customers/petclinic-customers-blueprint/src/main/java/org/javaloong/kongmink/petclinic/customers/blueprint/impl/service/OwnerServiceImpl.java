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
package org.javaloong.kongmink.petclinic.customers.blueprint.impl.service;

import org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository.OwnerRepository;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.service.OwnerService;

import javax.transaction.Transactional;
import java.util.Collection;

@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }

    @Override
    public void deleteOwner(Owner owner) {
        ownerRepository.delete(owner);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Owner findOwnerById(int id) {
        return ownerRepository.findById(id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Collection<Owner> findAllOwners() {
        return ownerRepository.findAll();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    @Override
    public Collection<Owner> findAllOwnersByLastName(String lastName) {
        return ownerRepository.findAllByLastName(lastName);
    }
}
