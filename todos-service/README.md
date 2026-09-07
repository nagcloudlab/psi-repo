# todos-service — CI/CD Pipeline Demo

A Spring Boot REST API used to demonstrate a complete enterprise CI/CD pipeline with Jenkins.

---

## Jenkins Plugins Required

| Plugin | Purpose | Install Name |
|--------|---------|--------------|
| **Pipeline** | Declarative pipeline support | `workflow-aggregator` |
| **Git** | SCM checkout | `git` |
| **Credentials Binding** | `withCredentials` for secrets | `credentials-binding` |
| **SonarQube Scanner** | SAST integration + Quality Gate | `sonar` |
| **JUnit** | Test result reporting | `junit` |
| **JaCoCo** | Code coverage reporting | `jacoco` |
| **Coverage** | Code coverage visualization | `coverage` |
| **OWASP Dependency-Check** | SCA report publishing | `dependency-check-jenkins-plugin` |
| **HTML Publisher** | Security report dashboards | `htmlpublisher` |
| **Docker Pipeline** | Docker build/push steps | `docker-workflow` |
| **Pipeline: Stage View** | Visual stage view | `pipeline-stage-view` |
| **Timestamps** | Add timestamps to logs | `timestamper` |
| **Slack Notification** | Slack alerts | `slack` |
| **Email Extension** | Email notifications | `email-ext` |
| **Pipeline Utility Steps** | Utility steps (readJSON, etc.) | `pipeline-utility-steps` |

---

## Jenkins Tools Required

Configure these under **Manage Jenkins → Tools**:

| Tool | Name in Jenkins | Notes |
|------|----------------|-------|
| **JDK** | `JDK-17` | Java 17+ required |
| **Maven** | `Maven-3` | Maven 3.9+ recommended |
| **Docker** | (system) | Docker must be installed on the agent |

---

## Jenkins Credentials Required

Configure these under **Manage Jenkins → Credentials**:

| Credential ID | Type | Purpose |
|---------------|------|---------|
| `sonar-token` | Secret text | SonarQube authentication token |
| `nexus-credentials` | Username/Password | Nexus Repository Manager login |

---

## Jenkins System Configuration

Under **Manage Jenkins → System**:

| Setting | Value |
|---------|-------|
| **SonarQube servers** | Name: `SonarQube`, URL: `http://localhost:8085`, Token: `sonar-token` credential |
| **Slack** | Workspace, channel `#builds`, bot token |

---

## External Services

| Service | Port | Purpose |
|---------|------|---------|
| **SonarQube** | `8085` | SAST — static code analysis |
| **Nexus (Web/Maven)** | `8081` | Artifact repository (JAR) |
| **Nexus (Docker)** | `8083` | Docker image registry |

---

## Pipeline Parameters

The pipeline supports these build parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `DEPLOY_ENV` | Choice | `none` | `none` / `staging` / `production` |
| `SKIP_TESTS` | Boolean | `false` | Skip unit tests |
| `SKIP_SECURITY_SCANS` | Boolean | `false` | Skip all security scans (Gitleaks, SAST, SCA, DAST, Trivy) |

---

## Pipeline Stages (16)

```
 ┌─────────────────────────────────────────────────────────────┐
 │  1. Checkout                                                │
 │  2. Secrets Detection (Gitleaks)          ← skippable       │
 │  3. Build & Test (mvn clean verify)       ← skippable       │
 │  4. SAST & SCA (parallel)                 ← skippable       │
 │     ├── SonarQube (SAST)                                    │
 │     └── OWASP Dependency-Check (SCA)      ← SLOW (~5 min)  │
 │  5. Quality Gate                          ← skippable       │
 │  6. Package JAR & Build Docker Image                        │
 │  7. Vulnerability Scan (Trivy)            ← skippable       │
 │  8. DAST - OWASP ZAP                     ← skippable       │
 │  9. Security Reports Dashboard            ← skippable       │
 │ 10. Publish JAR to Nexus                  ← main only       │
 │ 11. Push Docker Image to Nexus            ← main only       │
 │ 12. Deploy to Staging                     ← parameter       │
 │ 13. Smoke Tests (/health)                 ← parameter       │
 │ 14. Manual Approval                       ← parameter       │
 │ 15. Deploy to Production                  ← parameter       │
 │ 16. Build Summary Dashboard                                 │
 └─────────────────────────────────────────────────────────────┘
```

### Slow Stages (disable with `SKIP_SECURITY_SCANS=true`)

| Stage | Time | Reason |
|-------|------|--------|
| **SCA (OWASP DC)** | ~5-10 min | Downloads NVD database on first run |
| **SAST (SonarQube)** | ~1-2 min | Full code analysis |
| **Docker Build** | ~1-3 min | `--no-cache` forces full rebuild |

> **Tip for demos**: Use `SKIP_SECURITY_SCANS=true` for fast builds during class. Enable for full pipeline demos.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/todos` | List all todos |
| `GET` | `/api/v1/todos/{id}` | Get todo by ID |
| `POST` | `/api/v1/todos` | Create a todo |
| `PUT` | `/api/v1/todos/{id}` | Update a todo |
| `DELETE` | `/api/v1/todos/{id}` | Delete a todo |
| `GET` | `/health` | Health check |

---

## Quick Start (Local)

```bash
cd todos-service
mvn clean verify -B        # Build & test
mvn spring-boot:run        # Run locally on :8080
curl http://localhost:8080/health
curl http://localhost:8080/api/v1/todos
```
