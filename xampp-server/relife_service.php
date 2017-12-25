<?php
	$host="localhost";
	$username="root";
	$password="701016qqYY3210.";
	$db_name="relife";
	
	$con = new mysqli($host, $username, $password, $db_name);
	if(mysqli_connect_errno()){
		echo "Error: Could not connect to database.";
		exit;
	}
	
	if(isset($_REQUEST["selectall"]))
	{
		$sql = "select * from tb1_note";
		$result = mysqli_query($con, $sql) or die("Error in Selecting " . mysqli_error($con));

		
		while($row =mysqli_fetch_assoc($result))
		{
			$contactArray['data'][] = $row;
		}
		echo json_encode($contactArray);
	}
	
	if(isset($_REQUEST["insertnote"]))
	{
		$content=$_REQUEST['content'];
		$important=$_REQUEST['important'];
		$last_modified_time=$_REQUEST['last_modified_time'];
		
		
		$sql="insert into tb1_note (content,important,last_modified_time) values ('$content', '$important', '$last_modified_time')";		
		echo $count=mysqli_query($con, $sql);
	}
	
	if(isset($_REQUEST["inserttask"]))
	{
		$task = $_REQUEST['task'];
		$status = $_REQUEST['status'];
		$deadline = $_REQUEST['deadlinedate'];
		
		$sql="insert into tb2_tdlist (task, status, deadlinedate) values ('$task', '$status', '$deadline')";
		echo $count=mysqli_query($con, $sql);
	}
?>