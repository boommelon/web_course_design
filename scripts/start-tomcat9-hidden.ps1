param(
    [string]$TomcatHome = $env:CATALINA_HOME,

    [int]$Port = 8080
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
$javawPath = Resolve-RequiredPath -Path (Join-Path $javaHomeFull "bin\javaw.exe") -Name "javaw.exe"
$bootstrapJar = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "bin\bootstrap.jar") -Name "Tomcat bootstrap.jar"
$juliJar = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "bin\tomcat-juli.jar") -Name "Tomcat tomcat-juli.jar"
$loggingConfig = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "conf\logging.properties") -Name "Tomcat logging.properties"
$tempDir = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "temp") -Name "Tomcat temp directory"

$tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($null -ne $tomcatConnection) {
    Write-Host "Tomcat already appears to be running on port $Port."
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
    "start"
)

Start-Process -FilePath $javawPath -ArgumentList $arguments -WorkingDirectory $tomcatHomeFull -WindowStyle Hidden
Write-Host "Started Tomcat in background from $tomcatHomeFull"
