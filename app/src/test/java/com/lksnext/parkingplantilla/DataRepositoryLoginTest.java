package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class DataRepositoryLoginTest {

    private FirebaseAuth authMock;
    private Task<AuthResult> taskMock;
    private DataRepository repository;

    @Before
    public void setUp() {
        authMock = mock(FirebaseAuth.class);
        taskMock = mock(Task.class);

        // Configura encadenamiento para evitar NullPointerException
        when(taskMock.addOnCompleteListener(any())).thenReturn(taskMock);
        when(taskMock.addOnFailureListener(any())).thenReturn(taskMock);

        repository = new DataRepository(null, authMock);
    }

    @Test
    public void testLogin_EmailPassword_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        Callback callback = mock(Callback.class);

        when(authMock.signInWithEmailAndPassword(email, password)).thenReturn(taskMock);


        repository.login(email, password, callback);


        ArgumentCaptor<OnCompleteListener<AuthResult>> completeCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(taskMock).addOnCompleteListener(completeCaptor.capture());

        OnCompleteListener<AuthResult> onCompleteListener = completeCaptor.getValue();

        when(taskMock.isSuccessful()).thenReturn(true);
        onCompleteListener.onComplete(taskMock);

        // Assert
        verify(callback).onSuccess();
        verify(callback, never()).onFailure();
    }

    @Test
    public void testLogin_EmailPassword_Failure() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        Callback callback = mock(Callback.class);

        when(authMock.signInWithEmailAndPassword(email, password)).thenReturn(taskMock);

        repository.login(email, password, callback);


        ArgumentCaptor<OnCompleteListener<AuthResult>> completeCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(taskMock).addOnCompleteListener(completeCaptor.capture());

        OnCompleteListener<AuthResult> onCompleteListener = completeCaptor.getValue();


        when(taskMock.isSuccessful()).thenReturn(false);
        onCompleteListener.onComplete(taskMock);


        verify(callback).onFailure();


        ArgumentCaptor<OnFailureListener> failureCaptor = ArgumentCaptor.forClass(OnFailureListener.class);
        verify(taskMock).addOnFailureListener(failureCaptor.capture());

        OnFailureListener onFailureListener = failureCaptor.getValue();

        Exception fakeException = new Exception("Fake error");
        onFailureListener.onFailure(fakeException);

        verify(callback, atLeastOnce()).onFailure();
    }
}
