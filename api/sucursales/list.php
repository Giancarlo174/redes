<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $query = "SELECT id_sucursal, nombre FROM sucursales ORDER BY nombre ASC";
    
    $stmt = $db->prepare($query);
    $stmt->execute();
    
    $sucursales = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $sucursales[] = [
            'id' => $row['id_sucursal'],
            'nombre' => $row['nombre']
        ];
    }
    
    echo json_encode([
        'success' => true,
        'data' => $sucursales
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
