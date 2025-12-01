<?php
require_once 'config.php';

jsonHeaders();


$isAndroidApp = (
    isset($_SERVER['HTTP_USER_AGENT']) &&
    (strpos($_SERVER['HTTP_USER_AGENT'], 'Android') !== false ||
        strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false)
) || isset($_SERVER['HTTP_X_REQUESTED_WITH']);

$isBrowser = !$isAndroidApp && isset($_SERVER['HTTP_USER_AGENT']);

try {
    $conn = getConnection();

    $response = [
        'success' => true,
        'message' => 'Conexión Exitosa',
        'server_info' => $conn->server_info,
        'host_info' => $conn->host_info,
        'db_name' => DB_NAME,
        'client_type' => $isAndroidApp ? 'android' : 'browser'
    ];

    $conn->close();

    if ($isBrowser) {
        header('Content-Type: text/html; charset=UTF-8');
        echo 'Conexión a la base de datos ' . htmlspecialchars(DB_NAME) . ' exitosa.';
    } else {
        ensureCleanOutput();
        echo json_encode($response);
    }
} catch (Exception $e) {
    if ($isBrowser) {
        header('Content-Type: text/html; charset=UTF-8');
        echo 'Error de conexión a la base de datos';
    } else {
        ensureCleanOutput();
        echo json_encode([
            'success' => false,
            'message' => $e->getMessage(),
            'client_type' => $isAndroidApp ? 'android' : 'api'
        ]);
    }
}
?>