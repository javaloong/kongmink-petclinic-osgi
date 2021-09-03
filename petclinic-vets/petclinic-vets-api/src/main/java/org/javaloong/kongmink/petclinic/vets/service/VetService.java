package org.javaloong.kongmink.petclinic.vets.service;

import org.javaloong.kongmink.petclinic.vets.model.Vet;

import java.util.Collection;

public interface VetService {

    void saveVet(Vet vet);

    void deleteVet(Vet vet);

    Vet findVetById(int id);

    Collection<Vet> findAllVets();
}
