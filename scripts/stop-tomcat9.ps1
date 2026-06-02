param(
    [string]$TomcatHome = $env:CATALINA_HOME,

    [int]$Port = 8080,

    [int]$TimeoutSeconds = 20
)

$ErrorActionPreference = "Stop"

function Resolve-RequiredPath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if ([string]::IsNullOrWhiteSpace($Path) -or -not (Test-Path -LiteralPath $Path)) {
        throw "$Name does not exist: $Path"
    }

    return (Resolve-Path -LiteralPath $Path).Path
}

$tomcatHomeFull = Resolve-RequiredPath -Path $TomcatHome -Name "Tomcat home"
$javaHomeFull = Resolve-RequiredPath -Path $env:JAVA_HOME -Name "JAVA_HOME"
$javaPath = Resolve-RequiredPath -Path (Join-Path $javaHomeFull "bin\java.exe") -Name "java.exe"
$bootstrapJar = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "bin\bootstrap.jar") -Name "Tomcat bootstrap.jar"
$juliJar = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "bin\tomcat-juli.jar") -Name "Tomcat tomcat-juli.jar"
$loggingConfig = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "conf\logging.properties") -Name "Tomcat logging.properties"
$tempDir = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "temp") -Name "Tomcat temp directory"

$tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($null -eq $tomcatConnection) {
    Write-Host "Tomcat is not listening on port $Port."
    exit 0
}

$env:CATALINA_HOME = $tomcatHomeFull
$env:CATALINA_BASE = $tomcatHomeFull

$classpath = "$bootstrapJar;$juliJar"
$arguments = @(
    "-Djava.util.logging.config.file=$loggingConfig",
    "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager",
    "-Dcatalina.base=$tomcatHomeFull",
    "-Dcatalina.home=$tomcatHomeFull",
    "-Djava.io.tmpdir=$tempDir",
    "-classpath",
    $classpath,
    "org.apache.catalina.startup.Bootstrap",
    "stop"
)

& $javaPath @arguments

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Milliseconds 500
    $tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($null -eq $tomcatConnection) {
        Write-Host "Tomcat stopped."
        exit 0
    }
}

$tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($null -ne $tomcatConnection) {
    $processId = $tomcatConnection.OwningProcess
    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $processId"

    if ($process.CommandLine -like "*org.apache.catalina.startup.Bootstrap*" -and $process.CommandLine -like "*$tomcatHomeFull*") {
        Stop-Process -Id $processId -Force
        Write-Host "Tomcat did not stop gracefully, so process $processId was stopped."
    } else {
        throw "Port $Port is still in use by process $processId, but it does not look like this Tomcat instance."
    }
}
