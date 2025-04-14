package com.neutral.newspaper.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {
    DUPLICATED_EMAIL(1000, "이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD_FORMAT(1001, "비밀번호는 8~16자의 영어, 숫자, 특수기호(@$!%*?&)를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER_FORMAT(1002, "휴대폰 번호는 10~11자여야 합니다.", HttpStatus.BAD_REQUEST),
    NOT_REGISTERED_MEMBER(1003, "존재하지 않는 회원입니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_NOT_FOUND(1004, "회원 정보를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    NOT_MATCHED_PASSWORD(1005, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_MATCHED_PHONE_NUMBER(1006, "휴대폰 번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_MATCHED_VERIFYING_CODE(1007, "인증번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_VERIFICATION(1008, "인증이 완료되지 않았습니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
