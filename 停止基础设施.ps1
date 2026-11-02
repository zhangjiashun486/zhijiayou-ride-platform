# ============================================================
# 停止本地基础设施
# ============================================================

foreach ($port in 3307, 6379, 9005, 9006, 27017, 5672, 15672) {
    $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    foreach ($c in $conns) {
        try {
            Stop-Process -Id $c.OwningProcess -Force
            Write-Host "已停止端口 $port 的进程 (PID $($c.OwningProcess))"
        } catch {
            Write-Host "停止端口 $port 失败：$($_.Exception.Message)"
        }
    }
}
Get-Process erl,epmd -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Write-Host '完成。'
