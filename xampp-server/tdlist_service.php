<?php
require_once 'login.php';
$conn = new mysqli($host, $username, $password, $db_name);
echo "<link rel='stylesheet' type='text/css' href='./css/style.css' />";
echo "<link rel=\"stylesheet\" href=\"https://unpkg.com/mobi.css/dist/mobi.min.css\">";
if($conn->connect_error) die($conn->connect_error);

if(isset($_POST['delete'])&&isset($_POST['task']))
{
    $task = get_post($conn, 'task');
    $query = "DELETE FROM tb2_tdlist WHERE task='$task'";
    $result = $conn->query($query);
    if(!$result) echo "DELETE failed: $query<br>" .
        $conn->error . "<br><br>";
}

if(isset($_POST['task'])&&
    isset($_POST['status'])&&
    isset($_POST['deadlinedate']))
{
    $task = get_post($conn, 'task');
    $status = get_post($conn, 'status');
    $deadline = get_post($conn, 'deadlinedate');
    $query = "INSERT INTO tb2_tdlist(task, status, deadlinedate) VALUES" .
        "('$task', '$status', '$deadline')";
    $result = $conn->query($query);
    if(!$result) echo "INSERT failed: $query<br>".
        $conn->error . "<br><br>";
}

echo <<<_END
<div class="flex-center">
    <div class="container">
<form action='tdlist_service.php' method='post' class="form">
		   Task <input type='text' name='task'>
		  Status <input type='text' name='status'>
              Deadline <input type='text' name='deadlinedate'>
				   <input type='submit' class="btn btn-small submit" value='ADD'>
</form>
	</div>
</div>
_END;

$query = "SELECT * FROM tb2_tdlist";
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
		   Task: $row[1]
		 Status: $row[2]
               Deadline: $row[3]
</pre>
<form action='tdlist_service.php' method='post' class="form">
<input type='hidden' name='delete' value='yes'>
<input type='hidden' name='task' value='$row[1]'>
<input type='submit' value='DELETE' class="btn btn-small submit"><br></form>
_END;
}

$result->close();
$conn->close();

function get_post($conn, $var)
{
    return $conn->real_escape_string($_POST[$var]);
}
?>