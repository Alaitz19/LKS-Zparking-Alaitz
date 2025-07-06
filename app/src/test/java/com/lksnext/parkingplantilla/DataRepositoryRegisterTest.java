package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class DataRepositoryRegisterTest {

    private FirebaseAuth authMock;
    private FirebaseFirestore firestoreMock;
    private FirebaseUser userMock;
    private Task<AuthResult> taskMock;
    private DocumentReference docRefMock;
    private DataRepository repository;

    @Before
    public void setUp() {
        authMock = mock(FirebaseAuth.class);
        firestoreMock = mock(FirebaseFirestore.class);
        userMock = mock(FirebaseUser.class);
        taskMock = mock(Task.class);
        docRefMock = mock(DocumentReference.class);

        repository = new DataRepository(firestoreMock, authMock);
    }

    @Test
    public void testRegister_Successful() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String phone = "123456789";

        when(authMock.createUserWithEmailAndPassword(email, password)).thenReturn(taskMock);
        when(authMock.getCurrentUser()).thenReturn(userMock);
        when(userMock.getUid()).thenReturn("testUid");
        when(firestoreMock.collection(anyString())).thenReturn(mock(com.google.firebase.firestore.CollectionReference.class));
        when(firestoreMock.collection(anyString()).document(anyString())).thenReturn(docRefMock);
        when(docRefMock.set(any())).thenReturn(mock(Task.class));


        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);

        Callback callback = mock(Callback.class);


        repository.register(email, password, phone, callback);


        verify(taskMock).addOnCompleteListener(captor.capture());
        OnCompleteListener<AuthResult> listener = captor.getValue();

        when(taskMock.isSuccessful()).thenReturn(true);
        listener.onComplete(taskMock);

        
        verify(callback, never()).onFailure();

    }

    @Test
    public void testRegister_Failure() {
        String email = "test@example.com";
        String password = "password123";
        String phone = "123456789";

        when(authMock.createUserWithEmailAndPassword(email, password)).thenReturn(taskMock);

        ArgumentCaptor<OnCompleteListener<AuthResult>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);
        Callback callback = mock(Callback.class);

        repository.register(email, password, phone, callback);

        verify(taskMock).addOnCompleteListener(captor.capture());
        OnCompleteListener<AuthResult> listener = captor.getValue();

        when(taskMock.isSuccessful()).thenReturn(false);
        listener.onComplete(taskMock);

        verify(callback).onFailure();
    }
}

