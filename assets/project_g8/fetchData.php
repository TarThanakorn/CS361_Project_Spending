<?php
    // Open connection
    try {
        $pdo=new PDO('mysql:host=localhost;dbname=cs361_project_g8','root','');
    }catch (PDOException $e){
        echo 'Error: ' . $e->getMessage(); exit();
        }
        // Run Query
        $sql = 'SELECT * FROM account ORDER BY id DESC';
        $stm = $pdo->prepare($sql); // Prevent MySQL injection.
        $stm->execute();
        while ($row = $stm->fetch(PDO::FETCH_ASSOC)){
        $output[] = $row;
    }
    // Close connection
    $pdo = null;
    echo json_encode($output);
?>
