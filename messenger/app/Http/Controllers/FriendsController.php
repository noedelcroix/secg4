<?php

namespace App\Http\Controllers;
use App\Models\Friends;
use Illuminate\Http\Request;

class FriendsController{

    public static function getFriends($token){
        $response=null;
        try {
            $response = Friends::getFriends($token);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    
    public static function getOnlineFriends($token){
        $response=null;
        try {
            $response =  Friends::getOnlineFriends($token);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    
    public static function addFriend(Request $request){
        $response =null;
        try {
            $token = $request->post("token");
            $login = $request->post("login");
            $response=Friends::addFriend($token,$login);     
           } catch (Exception $ex) {
            return response()->json(false, 500);
        }        
        return response()->json($response);
    }

    public static function delFriend($token,Request $request){
        try {
            $login = $request->post("login");
            Friends::delFriend($token,$login); 
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }     
        return response()->json(false, 201);   
        
        
    }

}