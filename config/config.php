<?php
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_NAME', 'turno_facil');

function getConnection() {
    $conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if ($conn->connect_error) {
        http_response_code(500);
        die(json_encode(['success'=>false,'message'=>'DB error']));
    }
    $conn->set_charset('utf8');
    return $conn;
}
function jsonHeaders() {
    header('Content-Type: application/json; charset=UTF-8');
    header('Access-Control-Allow-Origin: *');
}
function ensureCleanOutput() {
    if (ob_get_length()) {
        ob_clean();
    }
    header_remove('Set-Cookie');
}
?>
