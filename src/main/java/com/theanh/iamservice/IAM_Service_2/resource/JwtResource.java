package com.theanh.iamservice.IAM_Service_2.resource;

import com.theanh.iamservice.IAM_Service_2.Jwts.RSAKeysUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtResource {

    private final RSAKeysUtil rsaKeysUtil;

    @GetMapping("/api/certificate/.well-known/jwks.json")
    public Map<String, Object> publicKey() throws Exception {
        return this.rsaKeysUtil.jwkSet().toJSONObject();
    }
}
