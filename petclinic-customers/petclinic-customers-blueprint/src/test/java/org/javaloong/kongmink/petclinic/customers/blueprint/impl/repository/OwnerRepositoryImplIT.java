package org.javaloong.kongmink.petclinic.customers.blueprint.impl.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.javaloong.kongmink.petclinic.customers.model.Owner;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(mergeDataSets = true)
@DataSet(value = {"ownerData.xml", "petData.xml"})
public class OwnerRepositoryImplIT extends DatabaseTestSupport {

    private OwnerRepository ownerRepository;

    @Before
    public void setup() {
        ownerRepository = OwnerRepositoryImpl.newInstance(emProvider.getEm());
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("createOwnerDataExpected.xml")
    public void testCreateOwner() {
        Owner owner = new Owner();
        owner.setFirstName("fn3");
        owner.setLastName("ln3");
        owner.setAddress("addr3");
        owner.setCity("city3");
        owner.setTelephone("222222");
        ownerRepository.save(owner);
    }

    @Test
    @DataSet(transactional = true)
    @ExpectedDataSet("updateOwnerDataExpected.xml")
    public void testUpdateOwner() {
        Owner owner = ownerRepository.findById(1);
        owner.setTelephone("222222");
        ownerRepository.save(owner);
    }

    @Test
    public void testFindOwnerById() {
        Owner owner = ownerRepository.findById(1);
        assertThat(owner).isNotNull().matches(
                p -> p.getFirstName().equals("George") && p.getLastName().equals("Franklin"));
    }

    @Test
    public void testFindAllOwners() {
        Collection<Owner> owners = ownerRepository.findAll();
        assertThat(owners).hasSize(2);
    }

    @Test
    public void testFindAllOwnersByLastName() {
        String lastName = "Franklin";
        Collection<Owner> owners = ownerRepository.findAllByLastName(lastName);
        assertThat(owners).hasSize(1);
    }
}
