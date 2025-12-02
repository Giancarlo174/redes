<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    
    // Validar campos requeridos
    if (!empty($data->email) && !empty($data->password)) {
        $database = new Database();
        $db = $database->getConnection();
        
        $email = $data->email;
        $password = $data->password;
        $nombre = isset($data->nombre) ? $data->nombre : '';
        // Default sucursal ID (should be dynamic in a full app, but defaulting to 1 for now)
        $sucursalId = 1; 
        
        // Verificar si el usuario ya existe
        $checkQuery = "SELECT id, password, nombre FROM usuarios WHERE email = :email";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":email", $email);
        $checkStmt->execute();
        
        $userId = null;
        $userName = "";
        
        if ($checkStmt->rowCount() > 0) {
            // Usuario ya existe - Validar contraseña (LOGIN)
            $row = $checkStmt->fetch(PDO::FETCH_ASSOC);
            
            // Comparación directa de contraseña (según implementación actual insegura)
            if ($password === $row['password']) {
                $userId = $row['id'];
                $userName = $row['nombre'];
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Contraseña incorrecta'
                ]);
                exit;
            }
        } else {
            // Usuario no existe - Registrar nuevo (REGISTER)
            if (empty($nombre)) {
                echo json_encode([
                    'success' => false,
                    'message' => 'Para registrarse por primera vez, el nombre es requerido'
                ]);
                exit;
            }

            // Insertar usuario con id_sucursal
            $insertQuery = "INSERT INTO usuarios (id_sucursal, nombre, email, password, rol) 
                           VALUES (:id_sucursal, :nombre, :email, :password, 'cliente')";
            $insertStmt = $db->prepare($insertQuery);
            $insertStmt->bindParam(":id_sucursal", $sucursalId);
            $insertStmt->bindParam(":nombre", $nombre);
            $insertStmt->bindParam(":email", $email);
            $insertStmt->bindParam(":password", $password);
            
            if ($insertStmt->execute()) {
                $userId = $db->lastInsertId();
                $userName = $nombre;
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Error al registrar usuario'
                ]);
                exit;
            }
        }
        
        // Si tenemos userId, generamos el turno
        if ($userId) {
            // Verificar si el usuario ya tiene un turno pendiente
            $pendingTurnQuery = "SELECT id, numero_turno FROM turnos WHERE id_usuario = :id_usuario AND estado = 'pendiente' LIMIT 1";
            $pendingStmt = $db->prepare($pendingTurnQuery);
            $pendingStmt->bindParam(":id_usuario", $userId);
            $pendingStmt->execute();
            
            if ($pendingStmt->rowCount() > 0) {
                $existingTurn = $pendingStmt->fetch(PDO::FETCH_ASSOC);
                echo json_encode([
                    'success' => true,
                    'message' => 'Ya tienes un turno pendiente',
                    'data' => [
                        'turno_id' => $existingTurn['id'],
                        'numero_turno' => $existingTurn['numero_turno'],
                        'usuario_id' => $userId,
                        'nombre' => $userName,
                        'email' => $email
                    ]
                ]);
                exit;
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
            
            // Crear turno con id_sucursal
            $createTurnoQuery = "INSERT INTO turnos (id_usuario, id_sucursal, numero_turno, estado) 
                                VALUES (:id_usuario, :id_sucursal, :numero_turno, 'pendiente')";
            $createTurnoStmt = $db->prepare($createTurnoQuery);
            $createTurnoStmt->bindParam(":id_usuario", $userId);
            $createTurnoStmt->bindParam(":id_sucursal", $sucursalId);
            $createTurnoStmt->bindParam(":numero_turno", $numeroTurno);
            
            if ($createTurnoStmt->execute()) {
                echo json_encode([
                    'success' => true,
                    'message' => 'Turno generado exitosamente',
                    'data' => [
                        'turno_id' => $db->lastInsertId(),
                        'numero_turno' => $numeroTurno,
                        'usuario_id' => $userId,
                        'nombre' => $userName,
                        'email' => $email
                    ]
                ]);
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Error al crear turno'
                ]);
            }
        }
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Email y contraseña son requeridos'
        ]);
    }
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
