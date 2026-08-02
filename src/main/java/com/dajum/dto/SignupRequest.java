package com.dajum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 190, message = "이메일은 190자 이하여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(
            min = 8,
            max = 50,
            message = "비밀번호는 8자 이상 50자 이하여야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Size(
            min = 2,
            max = 20,
            message = "닉네임은 2자 이상 20자 이하여야 합니다."
    )
    private String nickname;

    public SignupRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}