<?php
namespace App\Models;
class Message{
    
    public static function getMessages($token, $user){
        return \DB::select('select sndr.login,rciver.login,content,date from messages join users sndr on messages.sender=sndr.id join 
        users rciver on rciver.id=messages.receiver where (sndr.token = ? and rciver.login = ?) or (sndr.login = ? and rciver.token = ?) order by date desc',[$token,$user,$user,$token]);
    }

    public static function postMessage($token,$receiver,$content){
        $user = \DB::select('select id from users where token = ?',[$token]);
        $date = date('d-m-y h:i:s');
        return $result = \DB::insert("INSERT INTO `messages`(sender,receiver,content,date) VALUES(?,?,?,?)",[$user[0]->id, $receiver,$content,$date]);
    }

}

?>