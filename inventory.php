<?php
class MyDB extends SQLite3
{
    function __construct()
    {
        $this->open('mysqlitedb.db');
    }
}

$db = new MyDB();

if ('POST' === $_SERVER['REQUEST_METHOD'])
{
    $itemJson = (string)$entityBody = file_get_contents('php://input');

	//$item = json_decode($itemJson, true);

    $db->exec('CREATE TABLE IF NOT EXISTS items (itemJson STRING)');
    $statement = $db->prepare("INSERT INTO items (itemJson) VALUES (:itemJson)");
    $statement->bindParam(':itemJson', $itemJson, SQLITE3_TEXT);
    $statement->execute();
}

$result = $db->query('SELECT itemJson FROM items');

while ($row = $result->fetchArray())
{
    echo $row['itemJson'] . '<br /><br />';
}
?>