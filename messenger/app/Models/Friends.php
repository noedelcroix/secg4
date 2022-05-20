<?php
namespace App\Models;

class Friends{

    public static function getFriends($token){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        $friends = \DB::select('select users.login from friends join users on friends.user2=users.id where user2 = ?',[$user[0]->id]);
        return $friends;
    }
    public static function addFriend($login, $token){
        $user = \DB::select("select id from users where token =?",[$token]);
        error_log(count($user));
        if(count($user)==0){
            
            throw new Exception('no token found');
        }
        $friendsId = \DB::select('select id from users where login = ?',[$login]);
        if(count($friendsId)==0){
            throw new Exception('no friend found');
        }
        $result = \DB::insert("INSERT INTO `friends`(user1,user2) VALUES(?,?)",[$user[0]->id, $friendsId[0]->id]);
    }
    public static function delFriend($token,$login){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        $friendsId = \DB::select('select id from users where login = ?',[$login]);
        if(count($friendsId)==0){
            throw new Exception('no friend found');
        }
        \DB::delete('DELETE FROM friends WHERE user1 = ?',[$user[0]->id,$friendsId[0]->id]);
    }
    public static function getOnlineFriends($token){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        return \DB::select('select users.login from users join friends on users.id=friends.user2 where friends.user1=?',[$user[0]->id]);
    }
    public static function getPendingInvitations($token){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        return \DB::select('select users.login from users join friends on users.id=friends.user1 where friends.user2=? AND friends.user1!= ?' ,[$user[0]->id, $user[0]->id]);

    }

}