Write-Host "=== Construyendo todos los proyectos ===" -ForegroundColor Cyan
mvn clean install -DskipTests -f "$PSScriptRoot/eureka-server/pom.xml"  | Out-Host
if (-not $?) { exit 1 }
mvn clean install -DskipTests -f "$PSScriptRoot/api-gateway/pom.xml"   | Out-Host
if (-not $?) { exit 1 }
mvn clean install -DskipTests -f "$PSScriptRoot/personal-medico/pom.xml" | Out-Host
if (-not $?) { exit 1 }
mvn clean install -DskipTests -f "$PSScriptRoot/delivery-mascotas/pom.xml" | Out-Host
if (-not $?) { exit 1 }

Write-Host "`n=== Iniciando servicios ===" -ForegroundColor Cyan
$root = $PSScriptRoot

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root/eureka-server'; mvn spring-boot:run"
Start-Sleep -Seconds 8

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root/api-gateway'; mvn spring-boot:run"
Start-Sleep -Seconds 3

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root/personal-medico'; mvn spring-boot:run"
Start-Sleep -Seconds 3

Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root/delivery-mascotas'; mvn spring-boot:run"

Write-Host "`nTodos los servicios iniciados en ventanas separadas." -ForegroundColor Green
