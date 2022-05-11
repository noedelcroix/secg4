<?php

namespace App\Http\Controllers;
use App\Models\Friends;
use Illuminate\Http\Request;

class FriendsController{

    public static function getFriends($token){
        return Friends::getFriends($token);
    }
    public static function getOnlineFriends($token){
        return Friends::getOnlineFriends($token);
    }
    public static function addFriend(Request $request){
        $token = $request->post("token");
        $login = $request->post("login");
        return Friends::addFriend($token,$login);
    }
    public static function delFriend(Request $request){
        $token = $request->post("token");
        $login = $request->post("login");
        return Friends::delFriend($token,$login);
    }

}