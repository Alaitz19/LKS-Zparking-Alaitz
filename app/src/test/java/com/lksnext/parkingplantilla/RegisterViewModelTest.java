package com.lksnext.parkingplantilla;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

import org.junit.Before;
import org.junit.Test;

public class RegisterViewModelTest {

    private DataRepository repo;
    private RegisterViewModel viewModel;

    @Before
    public void setUp() {
        repo = mock(DataRepository.class);
        viewModel = new RegisterViewModel(repo);
    }

    @Test
    public void testRegister_CallsRepository() {
        String email = "test@example.com";
        String password = "123456";
        String username = "TestUser";
        String phone = "123456789";
        Callback callback = mock(Callback.class);

        viewModel.register(email, password, username, phone, callback);


        verify(repo).register(eq(email), eq(password), eq(phone), eq(callback));
    }
}
