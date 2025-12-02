<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $fecha = isset($_GET['fecha']) ? $_GET['fecha'] : date('Y-m-d');
    $idSucursal = isset($_GET['id_sucursal']) ? $_GET['id_sucursal'] : null;
    
    $query = "SELECT 
                COUNT(CASE WHEN estado = 'pendiente' THEN 1 END) as en_espera,
                COUNT(CASE WHEN estado = 'completado' THEN 1 END) as atendidos,
                COUNT(CASE WHEN estado = 'cancelado' THEN 1 END) as cancelados,
                COUNT(*) as total
              FROM turnos 
              WHERE DATE(fecha_creacion) = :fecha";

    if ($idSucursal && $idSucursal != -1) {
        $query .= " AND id_sucursal = :id_sucursal";
    }
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":fecha", $fecha);

    if ($idSucursal && $idSucursal != -1) {
        $stmt->bindParam(":id_sucursal", $idSucursal);
    }

    $stmt->execute();
    
    $stats = $stmt->fetch(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'data' => [
            'en_espera' => (int)$stats['en_espera'],
            'atendidos' => (int)$stats['atendidos'],
            'cancelados' => (int)$stats['cancelados'],
            'total' => (int)$stats['total']
        ]
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
