package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class DataRepositoryCrearPlazasTest {

    private Task<QuerySnapshot> taskMock;
    private QuerySnapshot snapshotMock;
    private WriteBatch batchMock;
    private Task<Void> batchCommitTaskMock;
    private DataRepository repository;

    @Before
    public void setUp() {
        FirebaseFirestore firestoreMock = mock(FirebaseFirestore.class);
        CollectionReference collectionMock = mock(CollectionReference.class);
        Query queryMock = mock(Query.class);
        taskMock = mock(Task.class);
        snapshotMock = mock(QuerySnapshot.class);
        batchMock = mock(WriteBatch.class);
        batchCommitTaskMock = mock(Task.class);


        when(taskMock.addOnSuccessListener(any())).thenReturn(taskMock);
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);
        when(batchCommitTaskMock.addOnSuccessListener(any())).thenReturn(batchCommitTaskMock);
        when(batchCommitTaskMock.addOnFailureListener(any())).thenReturn(batchCommitTaskMock);


        when(firestoreMock.collection(anyString())).thenReturn(collectionMock);
        when(collectionMock.limit(anyLong())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(taskMock);
        when(firestoreMock.batch()).thenReturn(batchMock);
        when(batchMock.commit()).thenReturn(batchCommitTaskMock);


        DocumentReference docRefMock = mock(DocumentReference.class);
        when(collectionMock.document(anyString())).thenReturn(docRefMock);

        repository = new DataRepository(firestoreMock, null);
    }

    @Test
    public void testCrearPlazasIniciales_AlreadyExists() {
        Callback callback = mock(Callback.class);
        when(snapshotMock.isEmpty()).thenReturn(false);

        repository.crearPlazasIniciales(callback);


        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(taskMock).addOnSuccessListener(captor.capture());


        OnSuccessListener<QuerySnapshot> listener = captor.getValue();
        listener.onSuccess(snapshotMock);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void testCrearPlazasIniciales_BatchSuccess() {
        Callback callback = mock(Callback.class);
        when(snapshotMock.isEmpty()).thenReturn(true);

        repository.crearPlazasIniciales(callback);


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
