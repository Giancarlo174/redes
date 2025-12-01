<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $estado = isset($_GET['estado']) ? $_GET['estado'] : null;
    $fecha = isset($_GET['fecha']) ? $_GET['fecha'] : date('Y-m-d');
    
    $query = "SELECT t.id, t.numero_turno, t.estado, t.fecha_creacion, t.actualizado_en,
                     u.nombre, u.email
              FROM turnos t
              INNER JOIN usuarios u ON t.id_usuario = u.id
              WHERE DATE(t.fecha_creacion) = :fecha";
    
    if ($estado) {
        $query .= " AND t.estado = :estado";
    }
    
    $query .= " ORDER BY t.fecha_creacion ASC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":fecha", $fecha);
    
    if ($estado) {
        $stmt->bindParam(":estado", $estado);
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
