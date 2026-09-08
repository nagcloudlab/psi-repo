# todos-service — CI/CD Pipeline Demo

A Spring Boot REST API used to demonstrate a complete enterprise CI/CD pipeline with Jenkins.

---

## Jenkins Plugins Required

| Plugin | Purpose | Install Name |
|--------|---------|--------------|
| **Pipeline** | Declarative pipeline support | `workflow-aggregator` |
| **Git** | SCM checkout | `git` |
| **Credentials Binding** | `withCredentials` for secrets | `credentials-binding` |
| **JUnit** | Test result reporting | `junit` |
| **JaCoCo** | Code coverage reporting | `jacoco` |
| **Coverage** | Code coverage visualization | `coverage` |
| **Docker Pipeline** | Docker build/push steps | `docker-workflow` |
| **Pipeline: Stage View** | Visual stage view | `pipeline-stage-view` |
| **Timestamps** | Add timestamps to logs | `timestamper` |
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
| `nexus-credentials` | Username/Password | Nexus Repository Manager login |

---

## External Services

| Service | Port | Purpose |
|---------|------|---------|
| **Nexus (Web/Maven)** | `8081` | Artifact repository (JAR) |
| **Nexus (Docker)** | `8083` | Docker image registry |

---

## Nexus Repository Setup

Nexus stores build artifacts (JAR files) and Docker images in a private registry.

### 1. Run Nexus Container

```bash
docker run -d --name nexus \
    -p 8081:8081 \
    -p 8083:8083 \
    -v nexus_data:/nexus-data \
    sonatype/nexus3
```

- **Web UI**: `http://localhost:8081`
- **Docker Registry**: port `8083`
- Data persists in Docker volume

### 2. Get Initial Admin Password

```bash
# Wait ~2 minutes for Nexus to start, then:
docker exec nexus cat /nexus-data/admin.password
```

### 3. Initial Setup

1. Open `http://localhost:8081`
2. Click **Sign In** → username: `admin`, password: from step 2
3. Follow the setup wizard:
   - Set new admin password (e.g., `admin123`)
   - Enable anonymous access: **Yes** (for pulling artifacts)

### 4. Create Maven Repository

1. Go to **Settings (gear icon) → Repositories → Create Repository**
2. Select **maven2 (hosted)**
3. Name: `maven-snapshots`
4. Version policy: **Snapshot**
5. Deployment policy: **Allow redeploy**
6. Click **Create Repository**

### 5. Create Docker Repository

1. Go to **Settings → Repositories → Create Repository**
2. Select **docker (hosted)**
3. Name: `psi-docker`
4. Check **HTTP** connector, port: `8083`
5. Enable **Docker V1 API** (optional)
6. Click **Create Repository**

### 6. Enable Docker Realm

1. Go to **Settings → Security → Realms**
2. Move **Docker Bearer Token Realm** to the **Active** column
3. Click **Save**

### 7. Configure Docker Insecure Registry

On the **Jenkins/Nexus host**, add insecure registry:

```bash
sudo tee /etc/docker/daemon.json <<EOF
{
  "insecure-registries": ["localhost:8083", "<YOUR_SERVER_IP>:8083"]
}
EOF
sudo systemctl restart docker
```

On **Mac (Docker Desktop)**: Settings → Docker Engine → add `"insecure-registries": ["<IP>:8083"]`

### 8. Configure Jenkins

**Manage Jenkins → Credentials:**
- Kind: **Username with password**
- Username: `admin`
- Password: your Nexus admin password
- ID: `nexus-credentials`

### 9. Verify

```bash
# Test Maven repo
curl -u admin:admin123 http://localhost:8081/repository/maven-snapshots/

# Test Docker login
echo "admin123" | docker login -u admin --password-stdin localhost:8083

# Test Docker push
docker pull hello-world
docker tag hello-world localhost:8083/hello-world:test
docker push localhost:8083/hello-world:test
```

---

## Pipeline Parameters

The pipeline supports these build parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `DEPLOY_ENV` | Choice | `none` | `none` / `staging` / `production` |
| `SKIP_TESTS` | Boolean | `false` | Skip unit tests |

---

## Pipeline Stages (10)

```
 ┌─────────────────────────────────────────────────────────────┐
 │  1. Checkout                                                │
 │  2. Build & Test (mvn clean verify)       ← skippable       │
 │  3. Package JAR & Build Docker Image                        │
 │  4. Publish JAR to Nexus                                    │
 │  5. Push Docker Image to Nexus                              │
 │  6. Deploy to Staging                                       │
 │  7. Smoke Tests (/health)                                   │
 │  8. Manual Approval                                         │
 │  9. Deploy to Production + Git Tag                          │
 │ 10. Rollback (placeholder)                                  │
 └─────────────────────────────────────────────────────────────┘
```

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
