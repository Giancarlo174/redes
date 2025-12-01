<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $data = json_decode(file_get_contents("php://input"));
    
    if (!empty($data->id) && !empty($data->estado)) {
        $database = new Database();
        $db = $database->getConnection();
        
        $query = "UPDATE turnos SET estado = :estado WHERE id = :id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":estado", $data->estado);
        $stmt->bindParam(":id", $data->id);
        
        if ($stmt->execute()) {
            echo json_encode([
                'success' => true,
                'message' => 'Turno actualizado exitosamente'
            ]);
        } else {
            echo json_encode([
                'success' => false,
                'message' => 'Error al actualizar turno'
            ]);
        }
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'ID y estado son requeridos'
        ]);
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
