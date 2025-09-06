package com.tiktok.demo.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tiktok.demo.dto.request.AuthenticationRequest;
import com.tiktok.demo.dto.request.EmailVerifyRequest;
import com.tiktok.demo.dto.request.IntrospectRequest;
import com.tiktok.demo.dto.request.LogoutRequest;
import com.tiktok.demo.dto.request.RefreshTokenRequest;
import com.tiktok.demo.dto.request.UserCreationRequest;
import com.tiktok.demo.dto.request.UserRegisterRequest;
import com.tiktok.demo.dto.request.UserUpdateRequest;
import com.tiktok.demo.dto.response.AuthenticationResponse;
import com.tiktok.demo.dto.response.EmailVerifyResponse;
import com.tiktok.demo.dto.response.IntrospectResponse;
import com.tiktok.demo.dto.response.LogoutResponse;
import com.tiktok.demo.dto.response.RefreshTokenResponse;
import com.tiktok.demo.dto.response.UserPrivateResponse;
import com.tiktok.demo.dto.response.UserRegisterResponse;
import com.tiktok.demo.entity.InvalidatedToken;
import com.tiktok.demo.entity.User;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;
import com.tiktok.demo.repository.InvalidatedTokenRepository;
import com.tiktok.demo.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class AuthenticationService {

    UserService userService;
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    PasswordEncoder passwordEncoder;

    Random random = new Random();

    EmailService emailService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    public AuthenticationResponse login(AuthenticationRequest request){
        var user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            log.info(request.getUsernameOrEmail());
            log.info(request.getPassword());
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info(authenticated ? "aaa" :"bbb");
        if(!authenticated)
            throw new AppException(ErrorCode.PASSWORD_NOT_TRUE);
        String token = generateToken(user);
        return AuthenticationResponse.builder()
            .auhthenticated(true)
            .token(token)
            .build();
    }

    private String generateToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .issuer("tiktok.com")
            .subject(user.getId())
            .issueTime(new Date())
            .expirationTime(new Date(
                Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()
            ))
            .claim("scope", buildScope(user))
            .build();
        
        
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);

        }
        
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }

    public IntrospectResponse introspect(IntrospectRequest request){
        String token = request.getToken();
        boolean valid = true;
        try {
            verifyToken(token, false);
        } catch (Exception e) {
            valid = false;
        }

        return IntrospectResponse.builder().valid(valid).build();
    }

    SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException{
        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh 
            ? new Date(signedJWT
                    .getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(refreshableDuration, ChronoUnit.SECONDS)
                    .toEpochMilli()
            )
            : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier);

        if(!verified || expiryTime.before(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public LogoutResponse logout(LogoutRequest request) throws ParseException, JOSEException{
        String token = request.getToken();
        SignedJWT signedJWT = verifyToken(token, false);
        String id = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
            .id(id)
            .expiryDate(expiryTime)
            .build();
        invalidatedTokenRepository.save(invalidatedToken);
        return LogoutResponse.builder()
            .result(true)
            .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws ParseException{
        String token = request.getToken();
        SignedJWT signedJWT = null;
        boolean valid = true;
        try {
            signedJWT = verifyToken(token, true);
        } catch (Exception e) {
            valid = false;
        }
        if(!valid) throw new AppException(ErrorCode.UNAUTHENTICATED);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String id = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String newToken = generateToken(user);
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
            .id(id)
            .expiryDate(expiryDate)
            .build();
        invalidatedTokenRepository.save(invalidatedToken);
        return RefreshTokenResponse.builder()
            .token(newToken)
            .build();
        
    }

    public void removeInvalidatedToken(){
        List<InvalidatedToken> invalidatedTokens = invalidatedTokenRepository.findByExpiryDateBefore(new Date());
        invalidatedTokens.forEach(invalidatedToken ->
            invalidatedTokenRepository.deleteById(invalidatedToken.getId())
        );
    }

    String generateUsername(){
        String username;
        do{
            long number = Math.abs(random.nextLong() % 1_000_000_0000L);
            username = "User" + number;
        } while (userRepository.existsByUsername(username));

        return username;
    }

    public UserRegisterResponse register(UserRegisterRequest request){
        var user = userRepository.findByEmail(request.getToEmail());
        String userId;
        if(user.isPresent() ){
            if(user.get().isVerified())
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            else{
                user.get().setPassword(passwordEncoder.encode(request.getPassword()));
                user.get().setDob(request.getDob());
                userId = user.get().getId();
                userRepository.save(user.get());
            }
        } else {
            String username = generateUsername();
            UserPrivateResponse response = userService.createUser(UserCreationRequest.builder()
                .email(request.getToEmail())
                .password(request.getPassword())
                .username(username)
                .name(username)
                .dob(request.getDob())
                .bio("No bio yet.")
                .build()
            , false);
            userId = response.getId();
        }
        emailService.sendVerificationCode(request);

        return UserRegisterResponse.builder()
            .result(true)
            .userId(userId)
            .build();
    }

    public EmailVerifyResponse verifyEmail(EmailVerifyRequest request){
        EmailVerifyResponse response = emailService.verifyEmail(request);
        if(response != null && !response.isValid()){
            throw new AppException(ErrorCode.VERIFY_CODE_NOT_TRUE);
        }
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setVerified(true);
        userRepository.save(user);
        String token = generateToken(user);
        if(response != null) response.setToken(token);
        return response;
    }

    public UserPrivateResponse setUserName(UserUpdateRequest request){
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userService.updateUser(user.getId(), request);
    }
}
