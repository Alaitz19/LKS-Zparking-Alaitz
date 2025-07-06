package com.lksnext.parkingplantilla;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DataRepositoryTotalTest {

    @Mock FirebaseFirestore firestoreMock;
    @Mock FirebaseAuth authMock;
    @Mock FirebaseUser userMock;
    @Mock CollectionReference collectionMock;
    @Mock DocumentReference docRefMock;
    @Mock Query queryMock;
    @Mock QuerySnapshot querySnapshotMock;
    @Mock WriteBatch batchMock;

    private DataRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new DataRepository(firestoreMock, authMock);
    }
    @Test
    public void testCrearPlazasIniciales_AlreadyExists() {
        Task<QuerySnapshot> taskMock = mock(Task.class);
        Callback callback = mock(Callback.class);

        when(taskMock.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            QuerySnapshot snapshot = mock(QuerySnapshot.class);
            when(snapshot.isEmpty()).thenReturn(false);
            listener.onSuccess(snapshot);
            return taskMock;
        });
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        // Si realmente necesitas firestoreMock:
        when(firestoreMock.collection(any())).thenReturn(collectionMock);
        when(collectionMock.limit(anyLong())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(taskMock);

        repository.crearPlazasIniciales(callback);

        verify(callback).onSuccess();
    }


    @Test
    public void testCrearPlazasIniciales_CreatesBatch() {
        Task<QuerySnapshot> taskMock = mock(Task.class);
        Task<Void> commitTaskMock = mock(Task.class);

        when(firestoreMock.collection(any())).thenReturn(collectionMock);
        when(collectionMock.limit(anyLong())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(taskMock);

        when(firestoreMock.batch()).thenReturn(batchMock);
        when(batchMock.commit()).thenReturn(commitTaskMock);


        when(taskMock.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            QuerySnapshot snapshot = mock(QuerySnapshot.class);
            when(snapshot.isEmpty()).thenReturn(true);
            listener.onSuccess(snapshot);
            return taskMock; // encadena bien
        });
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);


        when(commitTaskMock.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return commitTaskMock;
        });

        when(commitTaskMock.addOnFailureListener(any())).thenReturn(commitTaskMock);

        Callback callback = mock(Callback.class);
        repository.crearPlazasIniciales(callback);

        verify(batchMock, atLeastOnce()).set(any(), any());
        verify(callback).onSuccess();
    }

    @Test
    public void testLoginSuccess() {
        Task taskMock = mock(Task.class);

        when(authMock.signInWithEmailAndPassword(any(), any())).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        when(taskMock.addOnCompleteListener(any())).thenAnswer(invocation -> {
            invocation.<OnCompleteListener>getArgument(0).onComplete(taskMock);
            return taskMock;
        });

        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        Callback callback = mock(Callback.class);
        repository.login("email", "pass", callback);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void testLogout() {
        repository.logout();
        verify(authMock).signOut();
    }
    @Test
    public void testUploadProfileImage_NullUri() {
        CallbackWithResult<String> callback = mock(CallbackWithResult.class);
        repository.uploadProfileImage("uid", null, callback);
        verify(callback).onFailure(any(IllegalArgumentException.class));
    }


    @Test
    public void testLoginWithCredential_Failure() {
        Task taskMock = mock(Task.class);
        when(authMock.signInWithCredential(any())).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);
        when(taskMock.addOnCompleteListener(any())).thenAnswer(invocation -> {
            invocation.<OnCompleteListener>getArgument(0).onComplete(taskMock);
            return taskMock;
        });
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        Callback callback = mock(Callback.class);
        repository.loginWithCredential(mock(com.google.firebase.auth.AuthCredential.class), callback);

        verify(callback).onFailure();
    }



}
