package org.javaloong.kongmink.petclinic.customers.blueprint.impl.util;

import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.javaloong.kongmink.petclinic.customers.model.Pet;
import org.javaloong.kongmink.petclinic.customers.model.PetType;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

import java.util.LinkedHashMap;

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
