package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class DataRepositoryLoginWithCredentialTest {

    private FirebaseAuth authMock;
    private FirebaseFirestore firestoreMock;
    private Task<AuthResult> taskMock;
    private FirebaseUser userMock;
    private DocumentReference docRefMock;
    private DataRepository repository;

    @Before
    public void setUp() {
        authMock = mock(FirebaseAuth.class);
        firestoreMock = mock(FirebaseFirestore.class);
        taskMock = mock(Task.class);
        userMock = mock(FirebaseUser.class);
        docRefMock = mock(DocumentReference.class);

        when(taskMock.addOnCompleteListener(any())).thenReturn(taskMock);
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        repository = new DataRepository(firestoreMock, authMock);
    }

    @Test
    public void testLoginWithCredential_Success() {
        AuthCredential credential = mock(AuthCredential.class);
        Callback callback = mock(Callback.class);

        when(authMock.signInWithCredential(credential)).thenReturn(taskMock);
        when(authMock.getCurrentUser()).thenReturn(userMock);
        when(userMock.getUid()).thenReturn("testUid");
        when(firestoreMock.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(firestoreMock.collection(anyString()).document(anyString())).thenReturn(docRefMock);
        when(docRefMock.get()).thenReturn(mock(Task.class));
        when(docRefMock.set(any())).thenReturn(mock(Task.class));

        repository.loginWithCredential(credential, callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> completeCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(taskMock).addOnCompleteListener(completeCaptor.capture());
        OnCompleteListener<AuthResult> listener = completeCaptor.getValue();

        when(taskMock.isSuccessful()).thenReturn(true);
        listener.onComplete(taskMock);

        verify(callback).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void testLoginWithCredential_Failure() {
        AuthCredential credential = mock(AuthCredential.class);
        Callback callback = mock(Callback.class);

        when(authMock.signInWithCredential(credential)).thenReturn(taskMock);

        repository.loginWithCredential(credential, callback);

        ArgumentCaptor<OnCompleteListener<AuthResult>> completeCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(taskMock).addOnCompleteListener(completeCaptor.capture());
        OnCompleteListener<AuthResult> listener = completeCaptor.getValue();

        when(taskMock.isSuccessful()).thenReturn(false);
        listener.onComplete(taskMock);

        verify(callback).onFailure();

        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);
        verify(taskMock).addOnFailureListener(failureCaptor.capture());
        OnFailureListener onFailureListener = failureCaptor.getValue();

        Exception fakeEx = new Exception("Fake error");
        onFailureListener.onFailure(fakeEx);

        verify(callback, atLeastOnce()).onFailure();
    }
}
