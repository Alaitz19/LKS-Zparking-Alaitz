package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DataRepositoryResumenPlazasTest {

    private Task<QuerySnapshot> plazasTaskMock;
    private Task<QuerySnapshot> reservasTaskMock;
    private QuerySnapshot plazasSnapshotMock;
    private QuerySnapshot reservasSnapshotMock;
    private QueryDocumentSnapshot plazaDocMock;

    private DataRepository repository;

    @Before
    public void setUp() {
        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
        CollectionReference plazasCollectionMock = mock(CollectionReference.class);
        CollectionReference reservasCollectionMock = mock(CollectionReference.class);
        plazasTaskMock = mock(Task.class);
        reservasTaskMock = mock(Task.class);
        plazasSnapshotMock = mock(QuerySnapshot.class);
        reservasSnapshotMock = mock(QuerySnapshot.class);
        plazaDocMock = mock(QueryDocumentSnapshot.class);
        Query reservasQueryMock = mock(Query.class);


        when(plazasTaskMock.addOnSuccessListener(any())).thenReturn(plazasTaskMock);
        when(plazasTaskMock.addOnFailureListener(any())).thenReturn(plazasTaskMock);
        when(reservasTaskMock.addOnSuccessListener(any())).thenReturn(reservasTaskMock);
        when(reservasTaskMock.addOnFailureListener(any())).thenReturn(reservasTaskMock);

        when(firestoreMock.collection("plazas")).thenReturn(plazasCollectionMock);
        when(firestoreMock.collection("reservas")).thenReturn(reservasCollectionMock);

        when(plazasCollectionMock.get()).thenReturn(plazasTaskMock);

        when(reservasCollectionMock.whereEqualTo(anyString(), anyString())).thenReturn(reservasQueryMock);
        when(reservasQueryMock.get()).thenReturn(reservasTaskMock);

        repository = new DataRepository(firestoreMock, null);
    }

    @Test
    public void testGetResumenPlazasLibresAhora_Success() {
        CallbackWithResult<Map<String, Integer>> callback = mock(CallbackWithResult.class);

        repository.getResumenPlazasLibresAhora(callback);


        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> plazasCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(plazasTaskMock).addOnSuccessListener(plazasCaptor.capture());

        when(plazasSnapshotMock.iterator()).thenReturn(List.of(plazaDocMock).iterator());
        when(plazaDocMock.getId()).thenReturn("ABC123");
        when(plazaDocMock.getString(anyString())).thenReturn("moto");

        plazasCaptor.getValue().onSuccess(plazasSnapshotMock);


        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> reservasCaptor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(reservasTaskMock).addOnSuccessListener(reservasCaptor.capture());

        Iterator<QueryDocumentSnapshot> emptyIterator = List.<QueryDocumentSnapshot>of().iterator();
        when(reservasSnapshotMock.iterator()).thenReturn(emptyIterator);

        reservasCaptor.getValue().onSuccess(reservasSnapshotMock);

        verify(callback).onSuccess(argThat(resumen ->
                resumen.get("MOTO") != null && resumen.get("MOTO") > 0
        ));
    }

}
