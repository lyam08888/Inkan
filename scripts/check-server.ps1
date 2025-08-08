# Script pour vérifier le statut du serveur Shivas
param(
    [int]$LoginPort = 6789,
    [int]$GamePort = 9876
)

function Test-ServerPort {
    param([string]$Host, [int]$Port)
    try {
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $tcpClient.ConnectAsync($Host, $Port).Wait(1000)
        $result = $tcpClient.Connected
        $tcpClient.Close()
        return $result
    } catch {
        return $false
    }
}

$loginServerStatus = Test-ServerPort "127.0.0.1" $LoginPort
$gameServerStatus = Test-ServerPort "127.0.0.1" $GamePort

$status = @{
    LoginServer = @{
        Port = $LoginPort
        Status = if ($loginServerStatus) { "ONLINE" } else { "OFFLINE" }
    }
    GameServer = @{
        Port = $GamePort
        Status = if ($gameServerStatus) { "ONLINE" } else { "OFFLINE" }
    }
    Overall = if ($loginServerStatus -and $gameServerStatus) { "ONLINE" } else { "OFFLINE" }
}

# Retourner le statut en JSON pour l'interface web
$status | ConvertTo-Json -Compress
"@
</invoke>