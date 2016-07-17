package com.buyhatke.chat.registration;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface RegistrationView {

    void startBHService(boolean isRegister);

    void showError();

    void goToDash();

    void showUserAlreadyExistError();
}
