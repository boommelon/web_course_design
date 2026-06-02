param(
    [Parameter(Mandatory = $true)]
    [string]$ProjectDir,

    [string]$TomcatHome = $env:CATALINA_HOME,

    [string]$ContextPath = "graduation-design",

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
        throw "Refusing to write outside expected parent: $childFull"
    }
}

$projectFull = Resolve-RequiredPath -Path $ProjectDir -Name "Project directory"
$tomcatHomeFull = Resolve-RequiredPath -Path $TomcatHome -Name "Tomcat home"
$webappsPath = Resolve-RequiredPath -Path (Join-Path $tomcatHomeFull "webapps") -Name "Tomcat webapps"
$startScriptPath = Resolve-RequiredPath -Path (Join-Path $PSScriptRoot "start-tomcat9-hidden.ps1") -Name "Tomcat hidden start script"

$sourceWebapp = Resolve-RequiredPath -Path (Join-Path $projectFull "src\main\webapp") -Name "Webapp source"
$classesPath = Resolve-RequiredPath -Path (Join-Path $projectFull "target\classes") -Name "Compiled classes"
$targetAppPath = Join-Path $webappsPath $ContextPath
$targetWebInfPath = Join-Path $targetAppPath "WEB-INF"
$targetClassesPath = Join-Path $targetWebInfPath "classes"
$reloadMarkerPath = Join-Path $targetWebInfPath "web.xml"

Assert-UnderPath -ChildPath $targetAppPath -ParentPath $webappsPath
Assert-UnderPath -ChildPath $targetClassesPath -ParentPath $webappsPath

if (-not (Test-Path -LiteralPath $targetAppPath)) {
    New-Item -ItemType Directory -Path $targetAppPath | Out-Null
}

robocopy $sourceWebapp $targetAppPath /E /XD classes /NFL /NDL /NJH /NJS /NP
$webappCopyExit = $LASTEXITCODE
if ($webappCopyExit -gt 7) {
    throw "Failed to copy webapp resources. Robocopy exit code: $webappCopyExit"
}

if (-not (Test-Path -LiteralPath $targetClassesPath)) {
    New-Item -ItemType Directory -Path $targetClassesPath | Out-Null
}

robocopy $classesPath $targetClassesPath /E /NFL /NDL /NJH /NJS /NP
$classesCopyExit = $LASTEXITCODE
if ($classesCopyExit -gt 7) {
    throw "Failed to copy compiled classes. Robocopy exit code: $classesCopyExit"
}

if (Test-Path -LiteralPath $reloadMarkerPath) {
    (Get-Item -LiteralPath $reloadMarkerPath).LastWriteTime = Get-Date
}

$tomcatConnection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($null -eq $tomcatConnection) {
    & $startScriptPath -TomcatHome $tomcatHomeFull -Port $Port
} else {
    Write-Host "Tomcat already appears to be running on port $Port."
}

Write-Host "Quick deployed to $targetAppPath"
Write-Host "Open http://localhost:$Port/$ContextPath/"
