package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DataRepository repository;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(DataRepository.class);
        viewModel = new LoginViewModel(repository);
    }

    @Test
    public void testLoginUser_Success() {

        String email = "test@example.com";
        String password = "password";


        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(2);
            callback.onSuccess();
            return null;
        }).when(repository).login(eq(email), eq(password), any(Callback.class));


        viewModel.loginUser(email, password);

        assertTrue(viewModel.isLogged().getValue());
    }

    @Test
    public void testLoginUser_Failure() {
        String email = "test@example.com";
        String password = "wrongpass";

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(2);
            callback.onFailure();
            return null;
        }).when(repository).login(eq(email), eq(password), any(Callback.class));

        viewModel.loginUser(email, password);

        assertFalse(viewModel.isLogged().getValue());
    }
}
