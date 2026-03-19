$ProgressPreference = 'SilentlyContinue'
$downloadDir = "D:\Program Files"

Write-Host "开始下载 ELK Stack..." -ForegroundColor Green

$files = @(
    @{
        name = "elasticsearch"
        url = "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-windows-x86_64.zip"
        zip = "elasticsearch-8.11.0-windows-x86_64.zip"
    },
    @{
        name = "kibana"
        url = "https://artifacts.elastic.co/downloads/kibana/kibana-8.11.0-windows-x86_64.zip"
        zip = "kibana-8.11.0-windows-x86_64.zip"
    },
    @{
        name = "logstash"
        url = "https://artifacts.elastic.co/downloads/logstash/logstash-8.11.0-windows-x86_64.zip"
        zip = "logstash-8.11.0-windows-x86_64.zip"
    }
)

foreach ($file in $files) {
    $zipPath = Join-Path $downloadDir $file.zip
    
    if (Test-Path $zipPath) {
        Write-Host "$($file.name) 已存在，跳过下载" -ForegroundColor Yellow
    } else {
        Write-Host "下载 $($file.name)..." -ForegroundColor Cyan
        try {
            Invoke-WebRequest -Uri $file.url -OutFile $zipPath -TimeoutSec 600
            Write-Host "$($file.name) 下载完成" -ForegroundColor Green
        } catch {
            Write-Host "$($file.name) 下载失败: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "所有下载完成!" -ForegroundColor Green
Write-Host "请手动解压到 D:\Program Files\" -ForegroundColor Yellow
