# ============================================================
# 启动本地基础设施：MySQL(3307) + Redis(6379) + MinIO(9005/9006) + MongoDB(27017) + RabbitMQ(5672/15672)
# 用法：右键本文件 -> 使用 PowerShell 运行
# ============================================================

$mysqlBin = 'C:\Program Files\MySQL\MySQL Server 9.7\bin'
$mysqlIni = '$PSScriptRoot\mysql-local\my.ini'
$redisDir = '$PSScriptRoot\tools\redis'
$minioExe = '$PSScriptRoot\tools\minio.exe'
$minioData = '$PSScriptRoot\minio-data'
$mongoExe = '$PSScriptRoot\tools\mongodb\mongodb-win32-x86_64-windows-7.0.14\bin\mongod.exe'
$mongoData = '$PSScriptRoot\mongo-data'
$rabbitCmd = '$PSScriptRoot\start-rabbitmq.cmd'

function Test-LocalPort($port) {
    return (Test-NetConnection -ComputerName 127.0.0.1 -Port $port -WarningAction SilentlyContinue).TcpTestSucceeded
}

if (-not (Test-LocalPort 3307)) {
    Start-Process -FilePath "$mysqlBin\mysqld.exe" -ArgumentList "--defaults-file=$mysqlIni" -WindowStyle Hidden
    Write-Host 'MySQL 启动中 ...'
} else { Write-Host 'MySQL 已在运行' }

if (-not (Test-LocalPort 6379)) {
    Start-Process -FilePath "$redisDir\redis-server.exe" -ArgumentList "`"$redisDir\redis-local.conf`"" -WorkingDirectory $redisDir -WindowStyle Hidden
    Write-Host 'Redis 启动中 ...'
} else { Write-Host 'Redis 已在运行' }

if (-not (Test-LocalPort 9005)) {
    Start-Process -FilePath $minioExe -ArgumentList 'server', $minioData, '--address', ':9005', '--console-address', ':9006' -WindowStyle Hidden
    Write-Host 'MinIO 启动中 ...'
} else { Write-Host 'MinIO 已在运行' }

if (-not (Test-LocalPort 27017)) {
    Start-Process -FilePath $mongoExe -ArgumentList '--dbpath', $mongoData, '--port', '27017', '--bind_ip', '127.0.0.1', '--logpath', "$mongoData\mongod.log", '--logappend' -WindowStyle Hidden
    Write-Host 'MongoDB 启动中 ...'
} else { Write-Host 'MongoDB 已在运行' }

if (-not (Test-LocalPort 5672)) {
    Start-Process -FilePath $rabbitCmd -WindowStyle Hidden
    Write-Host 'RabbitMQ 启动中 ...（需要 30~60 秒）'
} else { Write-Host 'RabbitMQ 已在运行' }

Write-Host ''
Write-Host '等待服务就绪 ...'
Start-Sleep -Seconds 45
foreach ($p in 3307, 6379, 9005, 27017, 5672, 15672) {
    $ok = Test-LocalPort $p
    Write-Host ("端口 {0}: {1}" -f $p, $(if ($ok) { '正常' } else { '未就绪（RabbitMQ 可能还在启动，稍等再试）' }))
}
Write-Host ''
Write-Host '基础设施已就绪，接着启动后端和管理后台（见 本地启动说明.md）。'
