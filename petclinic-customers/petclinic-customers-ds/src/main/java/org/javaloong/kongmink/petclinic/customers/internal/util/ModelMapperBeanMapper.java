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
package org.javaloong.kongmink.petclinic.customers.internal.util;

import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.javaloong.kongmink.petclinic.customers.service.PetService;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.osgi.service.component.annotations.Component;

import java.util.LinkedHashMap;

@Component(service = BeanMapper.class, immediate = true)
public class ModelMapperBeanMapper implements BeanMapper {

    private final ModelMapper modelMapper;

    public ModelMapperBeanMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        configure(this.modelMapper);
    }

    private void configure(ModelMapper modelMapper) {
        Condition notNull = ctx -> ctx.getSource() != null;
        //Pet Entities
        PropertyMap<LinkedHashMap, Pet> petMap = new PropertyMap<LinkedHashMap, Pet>() {
            @Override
            protected void configure() {
                when(notNull).with(req -> new PetType()).map().setType(source("type"));
                when(notNull).with(req -> new Owner()).map().setOwner(source("owner"));
            }
        };
        modelMapper.createTypeMap(LinkedHashMap.class, Pet.class).addMappings(petMap);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    @Override
    public void map(Object source, Object destination) {
        modelMapper.map(source, destination);
    }
}
