<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $estado = isset($_GET['estado']) ? $_GET['estado'] : null;
    $fecha = isset($_GET['fecha']) ? $_GET['fecha'] : date('Y-m-d');
    $idSucursal = isset($_GET['id_sucursal']) ? $_GET['id_sucursal'] : null;
    
    $query = "SELECT t.id, t.numero_turno, t.estado, t.fecha_creacion, t.actualizado_en,
                     u.nombre, u.email, s.nombre as nombre_sucursal
              FROM turnos t
              INNER JOIN usuarios u ON t.id_usuario = u.id
              LEFT JOIN sucursales s ON t.id_sucursal = s.id_sucursal
              WHERE (DATE(t.fecha_creacion) = :fecha OR t.estado = 'pendiente')";
    
    if ($estado) {
        $query .= " AND t.estado = :estado";
    }

    if ($idSucursal && $idSucursal != -1) {
        $query .= " AND t.id_sucursal = :id_sucursal";
    }
    
    $query .= " ORDER BY t.fecha_creacion ASC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":fecha", $fecha);
    
    if ($estado) {
        $stmt->bindParam(":estado", $estado);
    }

    if ($idSucursal && $idSucursal != -1) {
        $stmt->bindParam(":id_sucursal", $idSucursal);
    }
    
    $stmt->execute();
    
    $turnos = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $turnos[] = $row;
    }
    
    echo json_encode([
        'success' => true,
        'data' => $turnos,
        'count' => count($turnos)
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
