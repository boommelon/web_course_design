param(
    [Parameter(Mandatory = $true)]
    [string]$WarPath,

    [string]$TomcatHome = $env:CATALINA_HOME,

    [string]$ContextPath = "graduation-design",

    [int]$Port = 8080,

    [string]$DbUrl = $env:GD_DB_URL,

    [string]$DbUsername = $env:GD_DB_USERNAME,

    [string]$DbPassword = $env:GD_DB_PASSWORD
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

function Assert-UnderPath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ChildPath,

        [Parameter(Mandatory = $true)]
        [string]$ParentPath
    )

    $childFull = [System.IO.Path]::GetFullPath($ChildPath)
    $parentFull = [System.IO.Path]::GetFullPath($ParentPath).TrimEnd('\') + '\'

    if (-not $childFull.StartsWith($parentFull, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to deploy outside Tomcat webapps: $childFull"
    }
}

$warFullPath = Resolve-RequiredPath -Path $WarPath -Name "WAR file"
$tomcatHomeFull = Resolve-RequiredPath -Path $TomcatHome -Name "Tomcat home"
$webappsPath = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "webapps") -Name "Tomcat webapps"
$startScriptPath = Resolve-RequiredPath -Path (Join-Path $PSScriptRoot "start-tomcat9-hidden.ps1") -Name "Tomcat hidden start script"

$targetWarPath = Join-Path $webappsPath "$ContextPath.war"
$targetExplodedPath = Join-Path $webappsPath $ContextPath

Assert-UnderPath -ChildPath $targetWarPath -ParentPath $webappsPath
Assert-UnderPath -ChildPath $targetExplodedPath -ParentPath $webappsPath

$tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
$tomcatRunning = $null -ne $tomcatConnection

if (-not $tomcatRunning) {
    if (Test-Path -LiteralPath $targetWarPath) {
        Remove-Item -LiteralPath $targetWarPath -Force
    }

    if (Test-Path -LiteralPath $targetExplodedPath) {
        Remove-Item -LiteralPath $targetExplodedPath -Recurse -Force
    }
}

Copy-Item -LiteralPath $warFullPath -Destination $targetWarPath -Force
Write-Host "Deployed WAR to $targetWarPath"

if ($tomcatRunning) {
    Write-Host "Tomcat already appears to be running on port $Port."
} else {
    & $startScriptPath -TomcatHome $tomcatHomeFull -Port $Port -DbUrl $DbUrl -DbUsername $DbUsername -DbPassword $DbPassword
}

Write-Host "Open http://localhost:$Port/$ContextPath/"
