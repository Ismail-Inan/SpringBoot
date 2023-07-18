package com.meineAngebote.registration;

import com.meineAngebote.security.auth.AuthenticationResponse;
import com.meineAngebote.security.auth.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;

  //or return String
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest request) {
    return ResponseEntity.ok(registrationService.register(request));
  }

  @GetMapping(path = "confirm")
  public String confirm(@RequestParam("token") String token) {
    return registrationService.confirmToken(token);
  }

}
