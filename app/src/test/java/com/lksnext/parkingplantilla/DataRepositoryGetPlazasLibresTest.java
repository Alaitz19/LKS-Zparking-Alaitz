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
import com.lksnext.parkingplantilla.domain.Plaza;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class DataRepositoryGetPlazasLibresTest {

    private FirebaseFirestore firestoreMock;
    private CollectionReference collectionMock;
    private Query queryMock;
    private Task<QuerySnapshot> taskMock;
    private QuerySnapshot snapshotMock;
    private QueryDocumentSnapshot docMock;
    private DataRepository repository;

    @Before
    public void setUp() {
        firestoreMock = mock(FirebaseFirestore.class);
        collectionMock = mock(CollectionReference.class);
        queryMock = mock(Query.class);
        taskMock = mock(Task.class);
        snapshotMock = mock(QuerySnapshot.class);
        docMock = mock(QueryDocumentSnapshot.class);

        when(taskMock.addOnSuccessListener(any())).thenReturn(taskMock);
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        when(firestoreMock.collection(anyString())).thenReturn(collectionMock);
        when(collectionMock.whereEqualTo(anyString(), any())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(taskMock);

        repository = new DataRepository(firestoreMock, null);
    }

    @Test
    public void testGetPlazasLibres_Success() {
        CallbackWithResult<List<Plaza>> callback = mock(CallbackWithResult.class);

        repository.getPlazasLibres(callback);

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(taskMock).addOnSuccessListener(captor.capture());


        when(snapshotMock.iterator()).thenReturn(List.of(docMock).iterator());
        when(docMock.getId()).thenReturn("ABC123");
        when(docMock.getString(anyString())).thenReturn("moto");

        OnSuccessListener<QuerySnapshot> listener = captor.getValue();
        listener.onSuccess(snapshotMock);


        verify(callback).onSuccess(argThat(plazas ->
                plazas.size() == 1 && plazas.get(0).getCodigo().equals("ABC123")
        ));
    }
}
