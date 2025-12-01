<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    
    if (!empty($data->nombre) && !empty($data->cedula)) {
        $database = new Database();
        $db = $database->getConnection();
        
        // Generar email automáticamente
        $email = $data->cedula . "@turno.facil.com";
        
        // Verificar si el usuario ya existe
        $checkQuery = "SELECT id FROM usuarios WHERE email = :email";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":email", $email);
        $checkStmt->execute();
        
        $userId = null;
        
        if ($checkStmt->rowCount() > 0) {
            // Usuario ya existe
            $row = $checkStmt->fetch(PDO::FETCH_ASSOC);
            $userId = $row['id'];
        } else {
            // Crear nuevo usuario
            $insertQuery = "INSERT INTO usuarios (nombre, email, password, rol) 
                           VALUES (:nombre, :email, :password, 'cliente')";
            $insertStmt = $db->prepare($insertQuery);
            $insertStmt->bindParam(":nombre", $data->nombre);
            $insertStmt->bindParam(":email", $email);
            $insertStmt->bindParam(":password", $data->cedula);
            
            if ($insertStmt->execute()) {
                $userId = $db->lastInsertId();
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Error al crear usuario'
                ]);
                exit;
            }
        }
        
        // Generar número de turno
        $turnoQuery = "SELECT COUNT(*) as total FROM turnos WHERE DATE(fecha_creacion) = CURDATE()";
        $turnoStmt = $db->prepare($turnoQuery);
        $turnoStmt->execute();
        $turnoRow = $turnoStmt->fetch(PDO::FETCH_ASSOC);
        $totalTurnos = $turnoRow['total'] + 1;
        
        // Generar letra aleatoria (A-Z)
        $letra = chr(65 + ($totalTurnos % 26));
        $numero = str_pad($totalTurnos, 2, '0', STR_PAD_LEFT);
        $numeroTurno = $letra . $numero;
        
        // Crear turno
        $createTurnoQuery = "INSERT INTO turnos (id_usuario, numero_turno, estado) 
                            VALUES (:id_usuario, :numero_turno, 'pendiente')";
        $createTurnoStmt = $db->prepare($createTurnoQuery);
        $createTurnoStmt->bindParam(":id_usuario", $userId);
        $createTurnoStmt->bindParam(":numero_turno", $numeroTurno);
        
        if ($createTurnoStmt->execute()) {
            echo json_encode([
                'success' => true,
                'message' => 'Turno generado exitosamente',
                'data' => [
                    'turno_id' => $db->lastInsertId(),
                    'numero_turno' => $numeroTurno,
                    'usuario_id' => $userId,
                    'nombre' => $data->nombre,
                    'email' => $email
                ]
            ]);
        } else {
            echo json_encode([
                'success' => false,
                'message' => 'Error al crear turno'
            ]);
        }
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Nombre y cédula son requeridos'
        ]);
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
