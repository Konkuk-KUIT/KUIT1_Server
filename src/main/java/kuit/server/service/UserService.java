package kuit.server.service;

import kuit.server.common.exception.UserException;
import kuit.server.dao.UserDao;
import kuit.server.dto.PostUserRequest;
import kuit.server.dto.PostUserResponse;
import kuit.server.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static kuit.server.common.response.status.BaseExceptionResponseStatus.DUPLICATE_EMAIL;
import static kuit.server.common.response.status.BaseExceptionResponseStatus.DUPLICATE_NICKNAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public PostUserResponse createUser(PostUserRequest postUserRequest) {
        log.info("[UserService.createUser]");

        // TODO: 1. validation (중복 검사)
        if (userDao.hasDuplicateEmail(postUserRequest.getEmail())) {
            throw new UserException(DUPLICATE_EMAIL);
        }
        if (userDao.hasDuplicateNickName(postUserRequest.getNickname())) {
            throw new UserException(DUPLICATE_NICKNAME);
        }

        // TODO: 2. password 암호화
        String encodedPassword = passwordEncoder.encode(postUserRequest.getPassword());
        postUserRequest.resetPassword(encodedPassword);

        // TODO: 3. DB insert & userId 반환
        long userId = userDao.createUser(postUserRequest);

        // TODO: 4. JWT 토큰 생성
        String jwt = jwtTokenProvider.createToken(postUserRequest.getEmail(), userId);

        return new PostUserResponse(userId, jwt);
    }

}
