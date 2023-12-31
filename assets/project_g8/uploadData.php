<?php
    // Open connection
    try {
        $pdo=new PDO('mysql:host=localhost;dbname=cs361_project_g8','root','');
    }catch (PDOException $e){
        echo 'Error: ' . $e->getMessage(); exit();
    }
    // Run Insert
    $type = $_REQUEST["sType"]; 
    $descs = $_REQUEST["sDesc"];
    $amount = $_REQUEST["sAmount"];
    if(!empty($_POST["sImage"])){
        $path = 'images/' . time() . '-' . rand(10000, 100000) . '.jpg';
        if(file_put_contents($path, base64_decode($_POST["sImage"]))){
            $sql = "INSERT INTO account(type,descs,amount,image) VALUES (?,?,?,?)";
            $stm= $pdo->prepare($sql);
            if(!$stm->execute([$type, $descs, $amount, $path])){
                $arr['StatusID'] = "0"; 
                $arr['Error'] = "Cannot save data!";
            }else{
                $arr['StatusID'] = "1"; 
                $arr['Error'] = "";
            }
        }
    }else{
        $sql = "INSERT INTO account(type,descs,amount) VALUES (?,?,?)";
        $stm= $pdo->prepare($sql);
        if(!$stm->execute([$type, $descs, $amount])){
            $arr['StatusID'] = "0"; 
            $arr['Error'] = "Cannot save data!";
        }else{
            $arr['StatusID'] = "1"; 
            $arr['Error'] = "";
        }
    }
    
    // Close connection
    $pdo = null;
    echo json_encode($arr);
?>
