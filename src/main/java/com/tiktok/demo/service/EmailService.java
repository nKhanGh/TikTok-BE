package com.tiktok.demo.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tiktok.demo.dto.request.EmailRequest;
import com.tiktok.demo.dto.request.UserRegisterRequest;
import com.tiktok.demo.dto.request.EmailVerifyRequest;
import com.tiktok.demo.dto.response.EmailVerifyResponse;
import com.tiktok.demo.exception.AppException;
import com.tiktok.demo.exception.ErrorCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
@Slf4j
public class EmailService {
    JavaMailSender javaMailSender;
    StringRedisTemplate redisTemplate;
    Random random = new Random();


    String generate6Digit(String email){
        int number = random.nextInt(1000000);
        String code = "%06d".formatted(number);
        redisTemplate.opsForValue().set("OTP:" + email, code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("OTP_ATTEMPS:" + email, "0", 5, TimeUnit.MINUTES);
        return code;
    }

    public void sendEmail(EmailRequest request){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("khang.nguyenkhanghuu14@hcmut.edu.vn");
            helper.setTo(request.getToEmail());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody());
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendVerificationCode(UserRegisterRequest request){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("khang.nguyenkhanghuu14@hcmut.edu.vn");
            helper.setTo(request.getToEmail());
            helper.setSubject(generate6Digit(request.getToEmail()) + " is your verification code");
            helper.setText("");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    public EmailVerifyResponse verifyEmail(EmailVerifyRequest request){
        String key = "OTP:" + request.getEmail();
        String attempsKey = "OTP_ATTEMPS:" + request.getEmail();
        Long attemps = redisTemplate.opsForValue().increment(attempsKey);
        
       
        if(attemps != null && attemps > 5){
            throw new AppException(ErrorCode.TOO_MANY_ATTEMPS);
        }
        String savedCode = redisTemplate.opsForValue().get(key);
        boolean result = false;
        if(savedCode != null && savedCode.equals(request.getVerifyCode())){
            redisTemplate.delete(key);
            redisTemplate.delete(attempsKey);
            result = true;
        }
        return EmailVerifyResponse.builder().valid(result).build();
    }
}
