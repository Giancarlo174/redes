<?php
require_once '../config/database.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"));
    
    if (!empty($data->email) && !empty($data->password)) {
        $database = new Database();
        $db = $database->getConnection();
        
        $query = "SELECT id, nombre, email, rol FROM usuarios 
                  WHERE email = :email AND password = :password";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(":email", $data->email);
        $stmt->bindParam(":password", $data->password);
        $stmt->execute();
        
        if ($stmt->rowCount() > 0) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            
            // Permitir login para admin y cliente
            if ($row['rol'] === 'admin' || $row['rol'] === 'cliente') {
                echo json_encode([
                    'success' => true,
                    'message' => 'Login exitoso',
                    'data' => [
                        'id' => (int)$row['id'],
                        'nombre' => $row['nombre'],
                        'email' => $row['email'],
                        'rol' => $row['rol']
                    ]
                ]);
            } else {
                echo json_encode([
                    'success' => false,
                    'message' => 'Rol no autorizado para acceder al sistema.'
                ]);
            }
        } else {
            echo json_encode([
                'success' => false,
                'message' => 'Email o contraseña incorrectos'
            ]);
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
