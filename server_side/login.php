<?php

     include 'DB_Connect.php';

	 // Check whether username or password is set from android	
     if(isset($_POST['username']) && isset($_POST['password']))
     {
		  // Innitialize Variable
	      $response = array();
          $response["success"] = false;  
	   	  $username = $_POST['username'];
          $password = $_POST['password'];
		  $hashPassword = md5($password);
		  
		  // Query database for row exist or not
          $sql = 'SELECT * FROM user_app WHERE  email = :username AND password = :password';
          $stmt = $conn->prepare($sql);
          $stmt->bindParam(':username', $username, PDO::PARAM_STR);
          $stmt->bindParam(':password', $hashPassword, PDO::PARAM_STR);
          $stmt->execute();
          if($stmt->rowCount())
          {
			 $response["success"] = true;	

          }  
          elseif(!$stmt->rowCount())
          {
			  	$response["success"] = false;
          }
		  
		  // send result back to android
   		  echo json_encode($response);
		  
  	}
?>