package test.repository;

import model.Bill;
import repository.impl.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BillRepositoryTest {

    private BillRepository repository;

    @BeforeEach
    void setUp() {
        repository = new BillRepository();
    }

    @Test
    void testSaveAndFindById() {
        Bill bill = new Bill(1L, "ELEC", 100.0, LocalDate.now(), "EVN");
        repository.save(bill);

        Bill found = repository.findById(1L);
        assertNotNull(found);
        assertEquals("ELEC", found.getType());
    }

    @Test
    void testSave_UpdatesExistingBill() {
        Bill bill1 = new Bill(1L, "ELEC", 100.0, LocalDate.now(), "EVN");
        repository.save(bill1);

        Bill billUpdate = new Bill(1L, "WATER", 200.0, LocalDate.now(), "SAVACO");
        repository.save(billUpdate);

        assertEquals(1, repository.findAll().size());
        assertEquals("WATER", repository.findById(1L).getType());
    }

    @Test
    void testFindAll_ReturnsCopy() {
        repository.save(new Bill(1L, "ELEC", 100.0, LocalDate.now(), "EVN"));
        repository.save(new Bill(2L, "WATER", 50.0, LocalDate.now(), "SAVACO"));

        List<Bill> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testDelete() {
        repository.save(new Bill(1L, "ELEC", 100.0, LocalDate.now(), "EVN"));
        assertNotNull(repository.findById(1L));

        repository.delete(1L);
        assertNull(repository.findById(1L));
    }

    @Test
    void testFindById_NonExistent_ReturnsNull() {
        assertNull(repository.findById(999L));
    }
}