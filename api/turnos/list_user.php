<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $database = new Database();
    $db = $database->getConnection();
    
    $id_usuario = isset($_GET['id_usuario']) ? $_GET['id_usuario'] : null;
    $fecha = isset($_GET['fecha']) ? $_GET['fecha'] : date('Y-m-d');
    
    if (!$id_usuario) {
        echo json_encode([
            'success' => false,
            'message' => 'ID de usuario es requerido'
        ]);
        exit;
    }
    
    // Obtener los turnos del usuario específico
    $query = "SELECT t.id, t.numero_turno, t.estado, t.fecha_creacion, t.actualizado_en, t.id_sucursal
              FROM turnos t
              WHERE t.id_usuario = :id_usuario AND DATE(t.fecha_creacion) = :fecha
              ORDER BY t.fecha_creacion DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":id_usuario", $id_usuario);
    $stmt->bindParam(":fecha", $fecha);
    $stmt->execute();
    
    $turnos = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $turnos[] = $row;
    }
    
    $idSucursalUsuario = null;
    if (!empty($turnos)) {
        $idSucursalUsuario = $turnos[0]['id_sucursal'];
    }

    // Obtener el turno actualmente en atención (si existe) - Filtrar por sucursal si aplica
    $queryEnAtencion = "SELECT numero_turno FROM turnos 
                        WHERE estado = 'completado' 
                        AND DATE(fecha_creacion) = :fecha";
    
    if ($idSucursalUsuario) {
        $queryEnAtencion .= " AND id_sucursal = :id_sucursal";
    }
    
    $queryEnAtencion .= " ORDER BY actualizado_en DESC LIMIT 1";

    $stmtEnAtencion = $db->prepare($queryEnAtencion);
    $stmtEnAtencion->bindParam(":fecha", $fecha);
    if ($idSucursalUsuario) {
        $stmtEnAtencion->bindParam(":id_sucursal", $idSucursalUsuario);
    }
    $stmtEnAtencion->execute();
    $turnoEnAtencion = $stmtEnAtencion->fetch(PDO::FETCH_ASSOC);
    
    // Contar cuántos turnos hay antes del usuario en la cola
    $posicionEnCola = 0;
    if (!empty($turnos)) {
        $primerTurno = $turnos[0];
        if ($primerTurno['estado'] === 'pendiente') {
            $queryPosicion = "SELECT COUNT(*) as posicion FROM turnos 
                             WHERE estado = 'pendiente' 
                             AND fecha_creacion < :fecha_turno 
                             AND DATE(fecha_creacion) = :fecha";
            
            if ($idSucursalUsuario) {
                $queryPosicion .= " AND id_sucursal = :id_sucursal";
            }

            $stmtPosicion = $db->prepare($queryPosicion);
            $stmtPosicion->bindParam(":fecha_turno", $primerTurno['fecha_creacion']);
            $stmtPosicion->bindParam(":fecha", $fecha);
            if ($idSucursalUsuario) {
                $stmtPosicion->bindParam(":id_sucursal", $idSucursalUsuario);
            }
            $stmtPosicion->execute();
            $result = $stmtPosicion->fetch(PDO::FETCH_ASSOC);
            $posicionEnCola = (int)$result['posicion'];
        }
    }
    
    echo json_encode([
        'success' => true,
        'data' => [
            'turnos' => $turnos,
            'turno_en_atencion' => $turnoEnAtencion ? $turnoEnAtencion['numero_turno'] : null,
            'posicion_en_cola' => $posicionEnCola
        ],
        'count' => count($turnos)
    ]);
} else {
    echo json_encode([
        'success' => false,
        'message' => 'Método no permitido'
    ]);
}
?>
