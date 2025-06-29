package com.server.ShareDoo.util;

import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.enums.Role;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SecurityUtil {




    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${mathcha_edu.jwt.base64-secret}")
    private String jwtKey;

    @Value("${mathcha_edu.jwt.token-validity-in-seconds}")
    private long jwtExpiration;


    public String createToken(User user) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + 1000*60*60*24*1))
                .claim("scope", "ROLE_"+user.getRole())
                .claim("userId", user.getUserId())
                .build();
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try{
            jwsObject.sign(new MACSigner(jwtKey.getBytes()));
            return jwsObject.serialize();
        }catch(JOSEException e){
            throw new RuntimeException(e);
        }
    }
}
//        public String createToken(Authentication authentication) {
//        Instant now = Instant.now();
//        Instant validity = now.plus(this.jwtExpiration, ChronoUnit.SECONDS);
//
//        // @formatter:off
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuedAt(now)
//                .expiresAt(validity)
//                .subject(authentication.getName())
//                .claim("mathcha_edu", authentication)
//                .build();
//        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
//        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
//    }
//    public String createTokenStudent(Student student) {
//
//        JWTClaimsSet claims = new JWTClaimsSet.Builder()
//                .subject(student.getUsername())
//                .issueTime(new Date(System.currentTimeMillis()))
//                .expirationTime(new Date(System.currentTimeMillis() + 1000*60*60*24*1))
//                .claim("scope", "ROLE_"+Role.STUDENT)
//                .build();
//        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
//        Payload payload = new Payload(claims.toJSONObject());
//        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
//        try{
//            jwsObject.sign(new MACSigner(jwtKey.getBytes()));
//            return jwsObject.serialize();
//        }catch(JOSEException e){
//            throw new RuntimeException(e);
//        }
//    }