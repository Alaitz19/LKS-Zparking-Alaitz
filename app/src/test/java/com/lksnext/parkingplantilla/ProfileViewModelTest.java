package com.lksnext.parkingplantilla;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import android.app.Application;
import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private DataRepository repo;
    private ProfileViewModel viewModel;

    @Before
    public void setUp() {
        repo = mock(DataRepository.class);
        Application app = mock(Application.class);
        viewModel = new ProfileViewModel(app, repo);
    }

    @Test
    public void loadUserData_shouldPostValue_whenUserExists() {
        // Arrange
        FirebaseUser fakeUser = mock(FirebaseUser.class);
        when(fakeUser.getUid()).thenReturn("123");
        when(repo.getCurrentUser()).thenReturn(fakeUser);

        doAnswer(invocation -> {
            CallbackWithResult<Map<String, Object>> callback = invocation.getArgument(1);
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", "Alaitz");
            callback.onSuccess(data);
            return null;
        }).when(repo).getCurrentUserData(eq("123"), any());

        // Act
        viewModel.loadUserData();

        // Assert
        Map<String, Object> result = viewModel.getUserData().getValue();
        assertThat(result, is(notNullValue()));
        assertThat(result.get("nombre"), is("Alaitz"));
    }

    @Test
    public void loadUserData_shouldPostNull_whenNoUser() {
        // Arrange
        when(repo.getCurrentUser()).thenReturn(null);

        // Act
        viewModel.loadUserData();

        // Assert
        assertThat(viewModel.getUserData().getValue(), is(nullValue()));
    }

    @Test
    public void logout_shouldCallRepositoryLogout() {
        // Act
        viewModel.logout();

        // Assert
        verify(repo).logout();
    }

    @Test
    public void updateUserProfile_shouldCallRepository_whenUserExists() {
        // Arrange
        FirebaseUser fakeUser = mock(FirebaseUser.class);
        when(fakeUser.getUid()).thenReturn("123");
        when(repo.getCurrentUser()).thenReturn(fakeUser);

        @SuppressWarnings("unchecked")
        CallbackWithResult<Boolean> callback = mock(CallbackWithResult.class);

        // Act
        viewModel.updateUserProfile("Alaitz", "alaitz@example.com", "12345", callback);

        // Assert
        verify(repo).updateUserProfile(eq("123"), eq("Alaitz"), eq("alaitz@example.com"), eq("12345"), same(callback));

    }

    @Test
    public void updateUserProfile_shouldFail_whenNoUser() {
        // Arrange
        when(repo.getCurrentUser()).thenReturn(null);

        @SuppressWarnings("unchecked")
        CallbackWithResult<Boolean> callback = mock(CallbackWithResult.class);

        // Act
        viewModel.updateUserProfile("Alaitz", "alaitz@example.com", "12345", callback);

        // Assert
        verify(callback).onFailure(any(Exception.class));
    }

    @Test
    public void uploadProfileImage_shouldCallRepository_whenUserExists() {
        // Arrange
        FirebaseUser fakeUser = mock(FirebaseUser.class);
        when(fakeUser.getUid()).thenReturn("123");
        when(repo.getCurrentUser()).thenReturn(fakeUser);

        Uri fakeUri = mock(Uri.class);
        @SuppressWarnings("unchecked")
        CallbackWithResult<String> callback = mock(CallbackWithResult.class);

        // Act
        viewModel.uploadProfileImage(fakeUri, callback);

        // Assert
        verify(repo).uploadProfileImage(eq("123"), eq(fakeUri), same(callback));
    }

    @Test
    public void uploadProfileImage_shouldFail_whenNoUser() {
        // Arrange
        when(repo.getCurrentUser()).thenReturn(null);

        Uri fakeUri = mock(Uri.class);
        @SuppressWarnings("unchecked")
        CallbackWithResult<String> callback = mock(CallbackWithResult.class);

        // Act
        viewModel.uploadProfileImage(fakeUri, callback);

        // Assert
        verify(callback).onFailure(any(Exception.class));
    }
}
