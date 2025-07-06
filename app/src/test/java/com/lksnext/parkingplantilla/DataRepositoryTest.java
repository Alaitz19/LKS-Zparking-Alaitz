package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


import java.util.*;

public class DataRepositoryTest {

    private DataRepository repository;
    private Task<QuerySnapshot> taskMock;
    private QuerySnapshot snapshotMock;
    private WriteBatch batchMock;
    private Task<Void> batchCommitTaskMock;
   

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
    @Test
    public void testCrearPlazasIniciales_BatchSuccess_Spy() {

        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
        CollectionReference collectionMock = mock(CollectionReference.class);
        Query queryMock = mock(Query.class);
        DocumentReference docRefMock = mock(DocumentReference.class);


        taskMock = mock(Task.class);
        snapshotMock = mock(QuerySnapshot.class);
        batchMock = mock(WriteBatch.class);
        batchCommitTaskMock = mock(Task.class);

        when(firestoreMock.collection(any())).thenReturn(collectionMock);
        when(collectionMock.limit(anyLong())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(taskMock);
        when(collectionMock.document(any())).thenReturn(docRefMock);

        when(firestoreMock.batch()).thenReturn(batchMock);
        when(batchMock.commit()).thenReturn(batchCommitTaskMock);

        when(taskMock.addOnSuccessListener(any())).thenReturn(taskMock);
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);
        when(batchCommitTaskMock.addOnSuccessListener(any())).thenReturn(batchCommitTaskMock);
        when(batchCommitTaskMock.addOnFailureListener(any())).thenReturn(batchCommitTaskMock);


        DataRepository repoSpy = spy(new DataRepository(firestoreMock, null));


        Callback callback = mock(Callback.class);
        when(snapshotMock.isEmpty()).thenReturn(true);


        repoSpy.crearPlazasIniciales(callback);


        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(taskMock).addOnSuccessListener(captor.capture());
        OnSuccessListener<QuerySnapshot> listener = captor.getValue();
        listener.onSuccess(snapshotMock);


        verify(batchMock, atLeastOnce()).set(any(), any());


        ArgumentCaptor<OnSuccessListener<Void>> commitCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(batchCommitTaskMock).addOnSuccessListener(commitCaptor.capture());
        OnSuccessListener<Void> commitListener = commitCaptor.getValue();
        commitListener.onSuccess(null);


        verify(callback).onSuccess();
        verify(callback, never()).onFailure();
    }
}
