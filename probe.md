### Liveness, Readiness, and Startup Probes API Implementation
```bash
package com.bankapp.probes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbeController {

    private boolean isAppReady = false;
    private boolean isAppStarted = false;

    @GetMapping("/liveness")
    public ResponseEntity<String> livenessProbe() {
        return ResponseEntity.ok("Application is alive!");
    }

    @GetMapping("/readiness")
    public ResponseEntity<String> readinessProbe() {
        if (isAppReady) {
            return ResponseEntity.ok("Application is ready!");
        }
        return ResponseEntity.status(503).body("Application is not ready yet!");
    }

    @GetMapping("/startup")
    public ResponseEntity<String> startupProbe() {
        if (isAppStarted) {
            return ResponseEntity.ok("Application has started successfully!");
        }
        return ResponseEntity.status(503).body("Application is still starting!");
    }

    // Simulate readiness and startup changes (to be removed in production)
    public void markAppAsReady() {
        isAppReady = true;
    }

    public void markAppAsStarted() {
        isAppStarted = true;
    }
}
```
**Where to add this code**
Create the file ProbeController.java in the directory src/main/java/com/bankapp/probes.
