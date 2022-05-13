<?php
namespace App\Models;

class Friends{

    public static function getFriends($token){
        $user = \DB::select('select id from users where token = ?',[$token]);
        $friends = \DB::select('select users.login from friends join users on friends.user2=users.id where user2 = ?',[$user[0]->id]);
        return $friends;
    }
    public static function addFriend($token,$login){
        $user = \DB::select('select id from users where token = ?',[$token]);
        $result = \DB::insert("INSERT INTO `friends`(user1,user2) VALUES(?,?)",[$user[0]->id, $login]);
    }
    public static function delFriend($token,$login){
        $user = \DB::select('select id from users where token = ?',[$token]);
        $friendsId = \DB::select('select id from users where login = ?',[$login]);
        \DB::delete('DELETE FROM friends WHERE user1 = ?',[$user[0]->id,$friendsId[0]->id]);
    }

}