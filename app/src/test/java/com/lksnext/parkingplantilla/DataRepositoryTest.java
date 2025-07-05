package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;


import com.google.firebase.Timestamp;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import org.junit.Before;
import org.junit.Test;


import java.util.*;

public class DataRepositoryTest {

    private DataRepository repository;

    @Before
    public void setUp() {

        repository = new DataRepository(null, null);
    }

    @Test
    public void testParseTipoPlaza() {
        assertEquals(PlazaType.COCHE, repository.parseTipoPlaza(null));
        assertEquals(PlazaType.COCHE, repository.parseTipoPlaza("coche"));
        assertEquals(PlazaType.MOTO, repository.parseTipoPlaza("moto"));
        assertEquals(PlazaType.ELECTRICO, repository.parseTipoPlaza("electrico"));
        assertEquals(PlazaType.DISCAPACITADO, repository.parseTipoPlaza("discapacitado"));
    }

    @Test
    public void testSafeString() {
        assertEquals("", repository.safeString(null));
        assertEquals("hola", repository.safeString("hola"));
    }

    @Test
    public void testSafeCastHoras() {
        List<Object> input = Arrays.asList("10:00", "11:00", 123, true);
        List<String> result = repository.safeCastHoras(input);
        assertEquals(2, result.size());
        assertTrue(result.contains("10:00"));
    }

    @Test
    public void testParseFecha_String() {
        Timestamp ts = repository.parseFecha("2025-07-05");
        assertNotNull(ts);
    }

    @Test
    public void testParseFecha_Timestamp() {
        Timestamp input = Timestamp.now();
        Timestamp output = repository.parseFecha(input);
        assertEquals(input, output);
    }

    @Test
    public void testIsHoraValidaEnRango() {
        int nowMinutes = repository.getNowMinutes();
        String futureTime = String.format("%02d:%02d",
                (nowMinutes + 5) / 60,
                (nowMinutes + 5) % 60);

        assertTrue(repository.isHoraValidaEnRango(futureTime, nowMinutes));
        assertFalse(repository.isHoraValidaEnRango("00:00", nowMinutes));
    }

    @Test
    public void testContarPlazasLibresPorTipo() {
        Plaza p1 = new Plaza("A", PlazaType.COCHE);
        Plaza p2 = new Plaza("B", PlazaType.MOTO);
        List<Plaza> plazas = Arrays.asList(p1, p2);
        List<String> ocupadas = Collections.singletonList("A");

        Map<String, Integer> result = repository.contarPlazasLibresPorTipo(plazas, ocupadas);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("MOTO"));
        assertEquals(Integer.valueOf(1), result.get("MOTO"));
    }
}
