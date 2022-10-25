package group.rxcloud.vrml.spi;


import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class SPITest {

    @Test
    public void loadSpiImpl() {
        Optional<TestSPIInterface> spiImpl = SPI.loadSpiImpl(TestSPIInterface.class);
        assertEquals(TestSPIImpl1.class, spiImpl.get().getClass());
    }

    @Test
    public void loadSpiImpls() {
        Optional<List<TestSPIInterface>> spiImpls = SPI.loadSpiImpls(TestSPIInterface.class);
        assertEquals(TestSPIImpl1.class, spiImpls.get().get(0).getClass());
    }

    @Test
    public void testLoadSpiImpl() {
        TestSPIInterface spiImpl = SPI.loadSpiImpl(TestSPIInterface.class, TestSPIImpl2::new);
        assertEquals(TestSPIImpl1.class, spiImpl.getClass());
    }
}