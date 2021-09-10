package org.javaloong.kongmink.petclinic.vets.service;

import org.javaloong.kongmink.petclinic.vets.model.Specialty;

import java.util.Collection;

public interface SpecialtyService {

    void saveSpecialty(Specialty specialty);

    void deleteSpecialty(Specialty specialty);

    Specialty findSpecialtyById(int id);

    Collection<Specialty> findAllSpecialties();
}
