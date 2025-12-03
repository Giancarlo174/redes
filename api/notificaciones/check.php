<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $id_usuario = isset($_GET['id_usuario']) ? $_GET['id_usuario'] : null;
    
    if (!$id_usuario) {
        echo json_encode(['success' => false, 'message' => 'ID de usuario requerido']);
        exit;
    }
    
    // 1. Obtener el turno más reciente del usuario (pendiente o completado hoy)
    $query = "SELECT id, numero_turno, estado, fecha_creacion, actualizado_en, id_sucursal 
              FROM turnos 
              WHERE id_usuario = :id_usuario 
              AND DATE(fecha_creacion) = CURDATE()
              ORDER BY actualizado_en DESC LIMIT 1";
              
    $stmt = $db->prepare($query);
    $stmt->bindParam(":id_usuario", $id_usuario);
    $stmt->execute();
    
    if ($stmt->rowCount() == 0) {
        echo json_encode(['success' => true, 'notification' => null]);
        exit;
    }
    
    $turno = $stmt->fetch(PDO::FETCH_ASSOC);
    $turnoId = $turno['id'];
    $estado = $turno['estado'];
    $numeroTurno = $turno['numero_turno'];
    
    $titulo = "";
    $mensaje = "";
    $tipo = ""; // atendido, siguiente, aproximandose
    
    // 2. Determinar si necesitamos notificar
    if ($estado === 'completado') {
        // Verificar si se completó hace poco (ej. 5 minutos) para no notificar cosas viejas si entra tarde
        // Pero confiaremos en la tabla de notificaciones para evitar duplicados
        $titulo = "Turno Atendido";
        $mensaje = "Su turno $numeroTurno fue atendido. Por favor acérquese al módulo.";
        $tipo = "atendido";
    } elseif ($estado === 'cancelado') {
        $titulo = "Turno Cancelado";
        $mensaje = "Su turno $numeroTurno ha sido cancelado. Por favor contacte al personal si cree que es un error.";
        $tipo = "cancelado";
    } elseif ($estado === 'pendiente') {
        // Calcular posición en la cola (todos los pendientes antes que este, sin importar fecha)
        $queryPos = "SELECT COUNT(*) as pos FROM turnos 
                     WHERE estado = 'pendiente' 
                     AND id_sucursal = :id_sucursal
                     AND fecha_creacion < :fecha_creacion";
                     
        $stmtPos = $db->prepare($queryPos);
        $stmtPos->bindParam(":id_sucursal", $turno['id_sucursal']);
        $stmtPos->bindParam(":fecha_creacion", $turno['fecha_creacion']);
        $stmtPos->execute();
        $rowPos = $stmtPos->fetch(PDO::FETCH_ASSOC);
        // Corrección: rowPos['pos'] sin espacio
        $posicion = intval($rowPos['pos']);
        
        if ($posicion == 0) {
            $titulo = "Turno Siguiente";
            $mensaje = "¡Es su turno! El turno $numeroTurno será atendido a continuación.";
            $tipo = "siguiente";
        } elseif ($posicion <= 2) {
            $titulo = "Turno Aproximandose";
            $mensaje = "Su turno $numeroTurno se está acercando. Hay $posicion turno(s) antes del suyo.";
            $tipo = "aproximandose";
        }
    }
    
    // 3. Si hay algo que notificar, verificar si ya se envió esa notificación específica
    if ($titulo !== "") {
        // Usamos el título como identificador del "tipo" de notificación para este turno
        // Para evitar spam, verificamos si ya existe una notificación con este título para este usuario hoy
        // Idealmente vincularíamos con ID de turno, pero la tabla notificaciones no tiene id_turno.
        // Usaremos el mensaje o título para distinguir.
        
        $checkQuery = "SELECT id FROM notificaciones 
                       WHERE id_usuario = :id_usuario 
                       AND titulo = :titulo 
                       AND mensaje LIKE :mensaje_part
                       AND DATE(fecha_envio) = CURDATE()";
                       
        $stmtCheck = $db->prepare($checkQuery);
        $stmtCheck->bindParam(":id_usuario", $id_usuario);
        $stmtCheck->bindParam(":titulo", $titulo);
        $msgPart = "%" . $numeroTurno . "%"; // Verificar que sea del mismo turno
        $stmtCheck->bindParam(":mensaje_part", $msgPart);
        $stmtCheck->execute();
        
        if ($stmtCheck->rowCount() == 0) {
            // No existe, crearla
            $insertQuery = "INSERT INTO notificaciones (id_usuario, titulo, mensaje, leida) 
                            VALUES (:id_usuario, :titulo, :mensaje, 0)";
            $stmtInsert = $db->prepare($insertQuery);
            $stmtInsert->bindParam(":id_usuario", $id_usuario);
            $stmtInsert->bindParam(":titulo", $titulo);
            $stmtInsert->bindParam(":mensaje", $mensaje);
            $stmtInsert->execute();
            
            echo json_encode([
                'success' => true,
                'notification' => [
                    'titulo' => $titulo,
                    'mensaje' => $mensaje,
                    'tipo' => $tipo
                ]
            ]);
            exit;
        }
    }
    
    echo json_encode(['success' => true, 'notification' => null]);
    
} else {
    echo json_encode(['success' => false, 'message' => 'Método no permitido']);
}
?>
