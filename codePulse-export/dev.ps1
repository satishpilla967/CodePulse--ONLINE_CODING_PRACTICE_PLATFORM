# One-command local dev runner: infra (MySQL/Redis/Judge0) + backend + frontend.
#
# Usage (from PowerShell): .\dev.ps1
# Stop everything with Ctrl+C (containers keep running in the background — run
# `docker compose down` if you want to fully stop them too).

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# --- Resolve docker compose command ---
$composeCmd = $null
try {
    docker compose version | Out-Null
    $composeCmd = "docker compose"
} catch {
    try {
        docker-compose version | Out-Null
        $composeCmd = "docker-compose"
    } catch {
        Write-Error "Neither 'docker compose' nor 'docker-compose' is available. Install Docker Desktop and try again."
        exit 1
    }
}

# --- Ensure .env exists ---
if (-not (Test-Path ".env")) {
    Write-Host "No .env found - creating one from example.env with local-dev defaults."
    Copy-Item "example.env" ".env"
}

# --- Load .env into this process's environment ---
$envValues = @{}
Get-Content ".env" | ForEach-Object {
    if ($_ -match '^\s*#' -or $_.Trim() -eq "") { return }
    if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)\s*$') {
        $name = $matches[1]
        $value = $matches[2]
        [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        $envValues[$name] = $value
    }
}
$dbPassword = if ($envValues["DATABASE_PASSWORD"]) { $envValues["DATABASE_PASSWORD"] } else { "mysql" }

Write-Host "==> Starting MySQL, Redis, and Judge0 (docker compose)..."
Invoke-Expression "$composeCmd up -d"

Write-Host "==> Waiting for MySQL to accept connections..."
$tries = 0
$ready = $false
while (-not $ready) {
    $tries++
    if ($tries -gt 60) {
        Write-Error "MySQL did not become ready in time. Check 'docker compose logs mysql'."
        exit 1
    }
    docker exec codepulse-mysql mysqladmin ping -h 127.0.0.1 -u root -p"$dbPassword" --silent 2>$null 1>$null
    if ($LASTEXITCODE -eq 0) {
        $ready = $true
    } else {
        Start-Sleep -Seconds 2
    }
}
Write-Host "    MySQL is ready."

Write-Host "==> Running database migrations (Flyway)..."
& .\mvnw.cmd -q flyway:migrate

Write-Host "==> Starting backend (Spring Boot) and frontend (Vite) together."
Write-Host "    Backend:  http://localhost:8080"
Write-Host "    Frontend: http://localhost:5173"
Write-Host "    Press Ctrl+C to stop both."
Write-Host ""

$backendJob = Start-Job -ScriptBlock {
    Set-Location $using:PWD
    & .\mvnw.cmd spring-boot:run
}

$frontendJob = Start-Job -ScriptBlock {
    Set-Location (Join-Path $using:PWD "js")
    pnpm install
    pnpm dev
}

try {
    while ($true) {
        Receive-Job $backendJob -ErrorAction SilentlyContinue
        Receive-Job $frontendJob -ErrorAction SilentlyContinue
        if ($backendJob.State -ne "Running" -and $frontendJob.State -ne "Running") {
            break
        }
        Start-Sleep -Seconds 1
    }
} finally {
    Write-Host ""
    Write-Host "==> Stopping backend and frontend (containers from docker compose are left running)..."
    Stop-Job $backendJob, $frontendJob -ErrorAction SilentlyContinue | Out-Null
    Remove-Job $backendJob, $frontendJob -Force -ErrorAction SilentlyContinue | Out-Null
}
