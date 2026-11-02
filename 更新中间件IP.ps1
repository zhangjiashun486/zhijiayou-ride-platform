# ============================================================
# 一键把项目配置里的中间件 IP 同步成 VMware 虚拟机的当前 IP
# 用法（在项目根目录执行）：
#   powershell -ExecutionPolicy Bypass -File .\更新中间件IP.ps1
# ============================================================

$ErrorActionPreference = 'Stop'

$vmrun = 'C:\Program Files (x86)\VMware\VMware Workstation\vmrun.exe'
$vmx   = Join-Path $env:USERPROFILE 'Documents\Virtual Machines\CentOS 7 64 位\CentOS 7 64 位.vmx'

if (-not (Test-Path $vmrun)) { Write-Host '[错误] 没找到 vmrun，请确认 VMware Workstation 已安装'; exit 1 }
if (-not (Test-Path $vmx))   { Write-Host '[错误] 没找到虚拟机文件'; exit 1 }

Write-Host '正在读取虚拟机 IP ...'
$ip = (& $vmrun getGuestIPAddress $vmx 2>$null | Select-Object -First 1)
if ($ip) { $ip = $ip.Trim() }
if ($ip -notmatch '^\d{1,3}(\.\d{1,3}){3}$') {
    Write-Host "[错误] 读取不到 IP，请先启动虚拟机并进入系统。当前返回：$ip"
    exit 1
}
Write-Host "虚拟机当前 IP：$ip"

$files = @(
    (Join-Path $PSScriptRoot 'backend\src\main\resources\application.yml'),
    (Join-Path $PSScriptRoot 'manager\src\main\resources\application.yml')
)

foreach ($file in $files) {
    if (-not (Test-Path $file)) { Write-Host "[跳过] 文件不存在：$file"; continue }
    $content = Get-Content -LiteralPath $file -Raw -Encoding UTF8
    $newContent = [regex]::Replace($content, '192\.168\.\d{1,3}\.\d{1,3}', $ip)
    if ($newContent -ne $content) {
        [System.IO.File]::WriteAllText($file, $newContent, (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "[已更新] $file"
    } else {
        Write-Host "[无变化] $file"
    }
}

Write-Host ''
Write-Host '完成！重启 backend 和 manager 即可生效。'
