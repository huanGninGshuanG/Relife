<?php
	require_once 'login.php';
echo "<link rel='stylesheet' type='text/css' href='./css/style.css' />";
echo "<link rel=\"stylesheet\" href=\"https://unpkg.com/mobi.css/dist/mobi.min.css\">";
	$conn = new mysqli($host, $username, $password, $db_name);
	if($conn->connect_error) die($conn->connect_error);

	if(isset($_POST['delete'])&&isset($_POST['last_modified_time']))
	{
		$last_modified_time = get_post($conn, 'last_modified_time');
		$query = "DELETE FROM tb1_note WHERE last_modified_time='$last_modified_time'";
		$result = $conn->query($query);
		if(!$result) echo "DELETE failed: $query<br>" . 
			$conn->error . "<br><br>";
	}

	if(isset($_POST['content'])&&
		isset($_POST['important'])&&
		isset($_POST['last_modified_time']))
	{
		$content = get_post($conn, 'content');
		$important = get_post($conn, 'important');
		$last_modified_time = get_post($conn, 'last_modified_time');
		$query = "INSERT INTO tb1_note(content, important, last_modified_time) VALUES" .
			"('$content', '$important', '$last_modified_time')";
		$result = $conn->query($query);
		if(!$result) echo "INSERT failed: $query<br>".
			$conn->error . "<br><br>";
	}

	echo <<<_END
<div class="flex-center">
    <div class="container">
<form action="note_service.php" method="post" class="form">
                    <input type="text" name="search"><input type="submit" value="SEARCH" class="btn btn-small submit">
</form>
    </div>
</div>
_END;

if(isset($_POST['search']))
{
    $str=get_post($conn, 'search');
    $query = "SELECT * FROM tb1_note WHERE content LIKE '%$str%'";
    $result=$conn->query($query);
    if(!$result) die("Database access failed: " . $conn->error);
    $rows = $result->num_rows;
    $flag = 1;
    for ($j=0; $j<$rows; ++$j)
    {
        $result->data_seek($j);
        $row = $result->fetch_array(MYSQLI_NUM);
        if($row)
        {
            $flag=0;
            echo <<<_END
<pre>
		   Content: $row[1]
		 Important: $row[2]
        Last_modified_time: $row[3]
</pre>
_END;
        }
        else{
            $flag=1;
        }
    }
    if($flag){
        echo "not found";
    }
}

	echo <<<_END
<div class="flex-center">
    <div class="container">
<form action='note_service.php' method='post' class="form">
		   Content <input type='text' name='content'>
		 Important <input type='text' name='important'>
        Last_modified_time <input type='text' name='last_modified_time'>
				   <input type='submit' value='ADD' class="btn btn-small submit"></form>
    </div>
</div>
_END;
	
	$query = "SELECT * FROM tb1_note";
	$result = $conn->query($query);
	if(!$result) die("Database access failed: " . $conn->error);
	
	$rows = $result->num_rows;
	
	for($j=0; $j<$rows; ++$j)
	{
		$result->data_seek($j);
		$row = $result->fetch_array(MYSQLI_NUM);
		
		echo <<<_END
<br/><br/>
<pre>
		   Content: $row[1]
		 Important: $row[2]
        Last_modified_time: $row[3]
</pre>
<form action='note_service.php' method='post'>
<input type='hidden' name='delete' value='yes'>
<input type='hidden' name='last_modified_time' value='$row[3]'>
<input type='submit' value='DELETE' class="btn btn-small submit"><br/></form>
_END;
	}
	
	$result->close();
	$conn->close();
	
	function get_post($conn, $var)
	{
		return $conn->real_escape_string($_POST[$var]);
	}
?>